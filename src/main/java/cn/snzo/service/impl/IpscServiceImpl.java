package cn.snzo.service.impl;

import cn.snzo.common.Constants;
import cn.snzo.entity.Conductor;
import cn.snzo.entity.Conference;
import cn.snzo.entity.Log;
import cn.snzo.exception.ServiceException;
import cn.snzo.repository.ConferenceRepository;
import cn.snzo.repository.LogRepository;
import cn.snzo.service.IConductorService;
import cn.snzo.service.IConferenceRoomService;
import cn.snzo.service.ISysSettingService;
import cn.snzo.service.IpscService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.utils.IpscUtil;
import cn.snzo.vo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hesong.ipsc.ccf.RpcError;
import com.hesong.ipsc.ccf.RpcResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

/**
 * Created by chentao on 2017/7/11 0011.
 */
@Service
public class IpscServiceImpl implements IpscService {

    private static Logger logger = LoggerFactory.getLogger(IpscServiceImpl.class);

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private ISysSettingService sysSettingService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private IConferenceRoomService conferenceRoomService;

    @Autowired
    private IConductorService conductorService;

    /**
     * 建立会议
     * @param conferenceStartShow
     * @param tokenName
     * @return 0 成功 1 失败 3 超时
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public int startConference(ConferenceStartShow conferenceStartShow, String tokenName) throws InterruptedException, IOException {

        int roomId = conferenceStartShow.getRoomId();
        ConferenceRoomShow conferenceRoomShow = conferenceRoomService.getOne(roomId);
        if (conferenceRoomShow == null) {
            return 3;
        }
        //检查会议室是否在使用中
        boolean roomIsInUse = conferenceRoomShow.getIsInUse() != null
                && conferenceRoomShow.getIsInUse() == 1;
        if (roomIsInUse) {
            return 4;
        }
        logger.info("建立会议");
        logger.info("参数：{}", conferenceStartShow);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("max_seconds", Constants.MAX_CONF_SECONDS); /// 会议最长时间，这是必填参数
        if (conferenceStartShow.isRecordEnable()) {
            SysSettingShow sysSettingShow = sysSettingService.getLatestSetting();
            if (sysSettingShow != null) {
                String recordPath = sysSettingShow.getRecordingPath();
                logger.info("录音文件存放路径：" + recordPath);
                params.put("record_file", recordPath); /// 会议录音存放路径
            } else {
                throw new ServiceException("请设置录音存放路径");
            }
        }
        logger.info("创建会议资源参数 {}", params);

        Log log = new Log("", OperResTypeEnum.CONFERENCE.ordinal(),
                "sys.conf.construct", "创建会议", tokenName, OperTypeEnum.CREATE.ordinal(), OperResultEnum.SUCCESS.ordinal());
            IpscUtil.createConference(
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> result       = (Map<String, Object>) o;
                            String              conferenceId = (String) result.get("res_id");
                            logger.info(">>>>>>会议资源建立成功：confId={}", conferenceId);
                            log.setOperResId(conferenceId);
                            logRepository.save(log);

                            //保存会议信息
                            saveConference(conferenceStartShow, conferenceId);

                            //外呼
                            logger.info("进行外呼", conferenceId);
                            try {
                                addCallToConf(conferenceStartShow.getPhones(), conferenceId, tokenName);
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        protected void onError(RpcError rpcError) {
                            logger.error("创建会议资源错误：{} {}", rpcError.getCode(), rpcError.getMessage());
                            log.setOperResult(OperResultEnum.ERROR.ordinal());
                            logRepository.save(log);
                        }

                        @Override
                        protected void onTimeout() {
                            logger.error("创建会议资源超时无响应");
                            log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                            logRepository.save(log);
                        }
                    }
            );
        return log.getOperResult();
    }




    @Transactional
    private void saveConference(ConferenceStartShow conferenceStartShow, String conferenceId) {
        Conference conference = new Conference();
        conference.setConductorId(conferenceStartShow.getConductorId());
        conference.setResId(conferenceId);

        conference.setRoomId(conferenceStartShow.getRoomId());
        ConferenceRoomShow conferenceRoomShow = conferenceRoomService.getOne(conferenceStartShow.getRoomId());
        if (conferenceRoomShow != null) {
            conference.setRoomNo(conferenceRoomShow.getNumber());
            //将会议室设置为使用中
            conferenceRoomService.modifyStatus(conferenceStartShow.getRoomId(), 1);
        }
        Conductor conductor = conductorService.getOne(conferenceStartShow.getConductorId());
        if (conductor != null) {
            conference.setConductorName(conductor.getUsername());
        }
        conference.setStartAt(new Date());
        conference.setEndAt(new Date());
        conference.setStatus(1);
        conferenceRepository.save(conference);
    }


    /**
     * 结束会议
     * @param resId
     * @param tokenName
     * @return 0 成功 1 失败 3 超时
     * @throws IOException
     */
    @Override
    public int stopConference(String resId, String tokenName) throws IOException {
        logger.info("结束会议resId:{}", resId);
        Log log = new Log(resId, OperResTypeEnum.CONFERENCE.ordinal(),
                "sys.conf.release",
                "删除会议", tokenName, OperTypeEnum.OPERATE.ordinal(),
                OperResultEnum.SUCCESS.ordinal());
        IpscUtil.stopConference(resId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("结束会议{}成功", resId);
                Conference conference = conferenceRepository.findByResId(resId);
                conference.setStatus(2);
                conferenceRepository.save(conference);
                //将会议室设置为空置中
                conferenceRoomService.modifyStatus(conference.getRoomId(), 0);
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.error("结束会议{}失败", resId);
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onTimeout() {
                logger.info("结束会议{}超时", resId);
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                logRepository.save(log);
            }
        });
        return log.getOperResult();
    }



    @Override
    public int addCallToConf(List<String> phones, String conferenceId, String tokenName) throws IOException, InterruptedException {
//        SysSettingShow sysSettingShow = sysSettingService.getLatestSetting();
        logger.info("呼叫 {} 加入会议 {}", phones, conferenceId);
        Log log = new Log("", OperResTypeEnum.CALL.ordinal(),
                "sys.call.construct",
                "创建呼叫资源", tokenName, OperTypeEnum.CREATE.ordinal(),
                OperResultEnum.SUCCESS.ordinal());

        IpscUtil.callOut(phones, IpscUtil.VOIP,
                new RpcResultListener() {
                    @Override
                    protected void onResult(Object o) {
                        Map<String, Object> result = (Map<String, Object>) o;
                        String              callId = (String) result.get("res_id");
                        logger.info("呼叫资源建立成功，ID={}。系统正在执行外呼……注意这不是呼叫成功！", callId);
                        IpscUtil.callConfMap.put(callId, conferenceId);
                        log.setOperResId(callId);
                        logRepository.save(log);
                    }

                    @Override
                    protected void onError(RpcError rpcError) {
                        logger.error("创建呼叫资源错误：{} {}", rpcError.getCode(), rpcError.getMessage());
                        log.setOperResult(OperResultEnum.ERROR.ordinal());
                        logRepository.save(log);
                    }

                    @Override
                    protected void onTimeout() {
                        logger.error("创建呼叫资源超时无响应");
                        log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                        logRepository.save(log);
                    }
                });
        return log.getOperResult();
    }

    @Override
    public int startRecord(String conferenceId, String tokenName) throws IOException {

        SysSettingShow sysSettingShow = sysSettingService.getLatestSetting();
        if (sysSettingShow == null) {
            return 2;
        }
        String path = sysSettingShow.getRecordingPath();
        if (path == null || path.isEmpty()) {
            return 3;
        }
        String filename = CommonUtils.getRecordFileName(path);
        Log log = new Log(conferenceId, OperResTypeEnum.CONFERENCE.ordinal(),
                "sys.conf.record_start","开始录音",
                tokenName, OperTypeEnum.OPERATE.ordinal(),
                OperResultEnum.SUCCESS.ordinal());
        IpscUtil.startRecord(conferenceId, filename, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("会议{}开始录音", conferenceId);
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("会议{}开始录音失败, errorMsg={}", conferenceId, rpcError.getMessage());
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onTimeout() {
                logger.info("会议{}开始录音超时", conferenceId);
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                logRepository.save(log);
            }
        });
        return log.getOperResult();
    }

    @Override
    public int stopRecord(String conferenceId, String tokenName) throws IOException {
        Log log = new Log(conferenceId, OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.record_stop",
                "停止录音", tokenName, OperTypeEnum.OPERATE.ordinal(), OperResultEnum.SUCCESS.ordinal());
        IpscUtil.stopRecord(conferenceId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("会议{}已结束录音", conferenceId);
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("会议{}结束录音失败, errorMsg={}", conferenceId, rpcError.getMessage());
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onTimeout() {
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                logRepository.save(log);
                logger.info("会议{}结束录音超时", conferenceId);
            }
        });
        return log.getOperResult();
    }

    @Override
    public int removeCallFromConf(String callId, String conferenceId, String tokenName) throws IOException {
        logger.info("从会议 {} 移除呼叫{}", callId, conferenceId);
        Log log = new Log(callId, OperResTypeEnum.CALL.ordinal(), "sys.call.conf_exit",
                "退出会议", tokenName, OperTypeEnum.OPERATE.ordinal(), OperResultEnum.SUCCESS.ordinal());
        IpscUtil.exitConferece(conferenceId, callId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("离开会议成功，confId={},callId={}", conferenceId, callId);
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                logger.info("离开会议错误，confId={},callId={}, errorMsg={}", conferenceId, callId, rpcError.getMessage());
            }

            @Override
            protected void onTimeout() {
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                logger.info("离开会议超时，confId={},callId={}", conferenceId, callId);
            }
        });
        return log.getOperResult();
    }

    @Override
    public int changeCallMode(String callId, String conferenceId, int mode, String tokenName) throws IOException {
        logger.info("改变与会者声音模式，callId={},confId={},mode={}", callId, conferenceId, mode);
        Log log = new Log(conferenceId, OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.set_part_voice_mode",
                "改变与会者的声音收放模式", tokenName, OperTypeEnum.OPERATE.ordinal(), OperResultEnum.SUCCESS.ordinal());
        IpscUtil.changePartMode(conferenceId, callId, mode, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("修改与会者声音模式成功，confId={},callId={},mode={}", conferenceId, callId, mode);
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.error("修改与会者声音模式失败，confId={},callId={},mode={} , errorMsg={}", conferenceId, callId, mode, rpcError.getMessage());
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onTimeout() {
                logger.error("修改与会者声音模式超时，confId={},callId={},mode={}", conferenceId, callId, mode);
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                logRepository.save(log);
            }
        });
        return log.getOperResult();
    }

    @Override
    public List<ConferencePart> getConfParts(String confResId, String username) throws IOException {
        logger.info("获取会议{}与会者列表", confResId);
        Log log = new Log(confResId, OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.get_parts",
                "获取会议与会者列表", username, OperTypeEnum.OPERATE.ordinal(), OperResultEnum.SUCCESS.ordinal());
        List<ConferencePart> conferenceParts = new ArrayList<ConferencePart>();
        IpscUtil.getConfParts(confResId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("获取与会者列表返回结果：{}", o.toString());
                logger.info("Object ={}", o);
                String array = JSON.toJSONString(o);
                logger.info("array = {}", array);
                Iterator<Object> iterator = JSON.parseArray(array).iterator();
                while (iterator.hasNext()) {
                    JSONObject obj = (JSONObject) iterator.next();
                    logger.info("obj = ", obj);
//                    conferenceParts.add(new ConferencePart(obj));
                }
                logger.info(array.toString());
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("获取与会者列表失败: {} {}", rpcError.getCode(), rpcError.getMessage());
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onTimeout() {
                logger.info("获取与会者列表超时");
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                logRepository.save(log);
            }
        });
        return conferenceParts;
    }


}

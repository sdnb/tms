package cn.snzo.service.impl;

import cn.snzo.common.Constants;
import cn.snzo.entity.Conference;
import cn.snzo.entity.Log;
import cn.snzo.vo.OperResTypeEnum;
import cn.snzo.vo.OperTypeEnum;
import cn.snzo.exception.ServiceException;
import cn.snzo.repository.ConferenceRepository;
import cn.snzo.repository.LogRepository;
import cn.snzo.service.ISysSettingService;
import cn.snzo.service.IpscService;
import cn.snzo.utils.IpscUtil;
import cn.snzo.utils.RandomUtils;
import cn.snzo.vo.ConferenceStartShow;
import cn.snzo.vo.SysSettingShow;
import com.hesong.ipsc.ccf.RpcError;
import com.hesong.ipsc.ccf.RpcResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public int startConference(ConferenceStartShow conferenceStartShow, String tokenName) throws InterruptedException, IOException {
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
            IpscUtil.createConference(
                    params,
                    new ConferenceCreateRpcListener(conferenceStartShow, tokenName)
            );
        return 1;
    }



    private void saveConference(ConferenceStartShow conferenceStartShow, String conferenceId) {
        Conference conference = new Conference();
        conference.setConductorId(conferenceStartShow.getConductorId());
        conference.setRoomId(conferenceStartShow.getRoomId());
        conference.setResId(conferenceId);
        conference.setStartAt(new Date());
        conference.setEndAt(new Date());
        conference.setStatus(1);
        conferenceRepository.save(conference);
    }


    @Override
    public int stopConference(String resId, String tokenName) throws IOException {
        logger.info("结束会议resId:{}", resId);
        IpscUtil.stopConference(resId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("结束会议{}成功", resId);
                Conference conference = conferenceRepository.findByResId(resId);
                conference.setStatus(2);
                conferenceRepository.save(conference);

                Log log = new Log(resId, OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.release",
                        "删除会议", tokenName, OperTypeEnum.OPERATE.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.error("结束会议{}失败", resId);
            }

            @Override
            protected void onTimeout() {
                logger.info("结束会议{}超时", resId);
            }
        });
        return 1;
    }

    @Override
    public int addCallToConf(List<String> phones, String conferenceId, String tokenName) throws IOException, InterruptedException {
//        SysSettingShow sysSettingShow = sysSettingService.getLatestSetting();
        logger.info("呼叫 {} 加入会议 {}", phones, conferenceId);
        IpscUtil.callOut(conferenceId, phones, IpscUtil.VOIP);
        return 1;
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
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FORMATE_yyyyMMddHHmmss);
        String recordName = path + localTime.format(formatter) + RandomUtils.getRandomNum(4);
        IpscUtil.startRecord(conferenceId, recordName, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("会议{}开始录音", conferenceId);
                Log log = new Log(conferenceId, OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.record_start",
                        "开始录音", tokenName, OperTypeEnum.OPERATE.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("会议{}开始录音失败", conferenceId);
            }

            @Override
            protected void onTimeout() {
                logger.info("会议{}开始录音超时", conferenceId);
            }
        });
        return 1;
    }

    @Override
    public int stopRecord(String conferenceId, String tokenName) throws IOException {
        IpscUtil.stopRecord(conferenceId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("会议{}已结束录音", conferenceId);
                Log log = new Log(conferenceId, OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.record_stop",
                        "停止录音", tokenName, OperTypeEnum.OPERATE.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("会议{}结束录音失败", conferenceId);
            }

            @Override
            protected void onTimeout() {
                logger.info("会议{}结束录音超时", conferenceId);
            }
        });
        return 1;
    }

    @Override
    public int removeCallFromConf(String callId, String conferenceId, String tokenName) throws IOException {
        logger.info("从会议 {} 移除呼叫{}", callId, conferenceId);
        IpscUtil.exitConferece(conferenceId, callId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("离开会议成功，confId={},callId={}",conferenceId, callId);
                Log log = new Log(callId, OperResTypeEnum.CALL.ordinal(), "sys.call.conf_exit",
                        "退出会议", tokenName, OperTypeEnum.OPERATE.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("离开会议错误，confId={},callId={}",conferenceId, callId);
            }

            @Override
            protected void onTimeout() {
                logger.info("离开会议超时，confId={},callId={}",conferenceId, callId);
            }
        });
        return 1;
    }

    @Override
    public int changeCallMode(String callId, String conferenceId, int mode, String tokenName) throws IOException {
        IpscUtil.changePartMode(conferenceId, callId, mode, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("修改与会者声音模式成功，confId={},callId={},mode={}", conferenceId, callId, mode);
                Log log = new Log(conferenceId, OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.set_part_voice_mode",
                        "改变与会者的声音收放模式", tokenName, OperTypeEnum.OPERATE.ordinal());
                logRepository.save(log);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.error("修改与会者声音模式失败，confId={},callId={},mode={}", conferenceId, callId, mode);
            }

            @Override
            protected void onTimeout() {
                logger.error("修改与会者声音模式超时，confId={},callId={},mode={}", conferenceId, callId, mode);
            }
        });
        return 1;
    }


    private class ConferenceCreateRpcListener extends RpcResultListener{

        private ConferenceStartShow conferenceStartShow;
        private String name;

        public ConferenceCreateRpcListener(ConferenceStartShow conferenceStartShow, String name) {
            this.conferenceStartShow = conferenceStartShow;
            this.name = name;
        }

        @Override
        protected void onResult(Object o) {
            Map<String, Object> result       = (Map<String, Object>) o;
            String              conferenceId = (String) result.get("res_id");
            logger.info(">>>>>>会议资源建立成功：confId={}", conferenceId);
            //保存会议信息
            saveConference(conferenceStartShow, conferenceId);
            //外呼
            logger.info("进行外呼", conferenceId);
            //保存日志
            Log log = new Log(conferenceId, OperResTypeEnum.CONFERENCE.ordinal(),
                    "sys.conf.construct", "创建会议", name, OperTypeEnum.CREATE.ordinal());
            logRepository.save(log);
            try {
                IpscUtil.callOut(conferenceId, conferenceStartShow.getPhones(), IpscUtil.VOIP);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onError(RpcError rpcError) {
            logger.error("创建会议资源错误：{} {}", rpcError.getCode(), rpcError.getMessage());
        }

        @Override
        protected void onTimeout() {
            logger.error("创建会议资源超时无响应");
        }
    }
}

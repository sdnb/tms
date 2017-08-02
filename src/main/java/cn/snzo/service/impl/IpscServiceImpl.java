package cn.snzo.service.impl;

import cn.snzo.common.Constants;
import cn.snzo.entity.*;
import cn.snzo.exception.ServiceException;
import cn.snzo.ipsc.CreateCallRpcResultListener;
import cn.snzo.repository.*;
import cn.snzo.service.IConductorService;
import cn.snzo.service.IConferenceRoomService;
import cn.snzo.service.ISysSettingService;
import cn.snzo.service.IpscService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.utils.DateUtil;
import cn.snzo.utils.IpscUtil;
import cn.snzo.vo.*;
import cn.snzo.ws.ChangeReminder;
import com.hesong.ipsc.ccf.RpcError;
import com.hesong.ipsc.ccf.RpcResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private ConferenceRoomRepository conferenceRoomRepository;

    @Autowired
    private ContactRepository contactRepository;


    @Autowired
    private ChangeReminder changeReminder;


    @Autowired
    private CallRepository callRepository;
    /**
     * 建立会议
     * @param conferenceStartShow
     * @param tokenName
     * @return 0 成功 1 失败 3 超时
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public Conference startConference(ConferenceStartShow conferenceStartShow, String tokenName) throws InterruptedException, IOException {

        ConferenceRoomShow conferenceRoomShow = conferenceRoomService.getRoomByConductor(conferenceStartShow.getConductorId());
        if (conferenceRoomShow == null) {
            throw new ServiceException("会议室不存在");
        }
        //检查会议室是否在使用中
        boolean roomIsUse = conferenceRepository.checkConfOfRoom(conferenceRoomShow.getId()) > 0;
        if (roomIsUse) {
            throw new ServiceException("会议室正在使用");
        }
        logger.info("=>>>>>>>>> 建立会议参数：{}", conferenceStartShow);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("max_seconds", Constants.MAX_CONF_SECONDS); /// 会议最长时间，这是必填参数
        params.put("parts_threshold", Constants.PARTS_THRESHOLD); /// 会议最长时间，这是必填参数
        if (conferenceStartShow.isRecordEnable()) {
            SysSettingShow sysSettingShow = sysSettingService.getLatestSetting();
            if (sysSettingShow != null) {
                String recordPath = sysSettingShow.getRecordingPath();
                logger.info("=>>>>>>>>> 录音文件存放目录：{}", recordPath);
                String fileName = CommonUtils.getRecordFileName(recordPath);
                logger.info("=>>>>>>>>> 录音文件名 {}", fileName);
                params.put("record_file", fileName); /// 会议录音存放路径
            } else {
                throw new ServiceException("请设置录音存放路径");
            }
        }

        //判断电话是否正在参加会议
        List<String> ps = conferenceStartShow.getPhones();
        List<String> phoneInConf = getPhoneIsInUsing(ps.toArray(new String[ps.size()]));
        if (!phoneInConf.isEmpty()) {
            logger.info("=>>>>>>>>> 电话{}正在参会", phoneInConf);
            throw new ServiceException("电话:"+phoneInConf+"正在参会中");
        }


        logger.info("=>>>>>>>>> 创建会议资源参数 {}", params);
        List<Conference> conferences = new ArrayList<>();
        Log log = new Log(OperResTypeEnum.CONFERENCE.ordinal(),"sys.conf.construct", "创建会议", tokenName, OperTypeEnum.CREATE.ordinal());

        int[] ret = new int[1];
        IpscUtil.createConference(
                params,
                new RpcResultListener() {
                    @Override
                    protected void onResult(Object o) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> result = (Map<String, Object>) o;
                        String conferenceId = (String) result.get("res_id");
                        logger.info("=>>>>>>>>> 会议资源建立成功：confId={}", conferenceId);


                        int isInRecording = 0;
                        //会议正在录音
                        if (params.get("record_file") != null) {
                            isInRecording = 1;
                        }
                        //保存会议信息
                        Conference conference = saveConference(conferenceStartShow, conferenceId, isInRecording, conferenceRoomShow.getId());

                        conferences.add(conference);
                        //外呼
                        logger.info("=>>>>>>>>> 进行外呼", conferenceId);
                        try {
                            addCallToConf(conferenceStartShow.getPhones(), conferenceId, tokenName);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        changeReminder.sendMessageToAll(conferenceId);
                        ret[0] = 1;

                        log.setOperResId(conferenceId);
                        log.setOperResult(OperResultEnum.SUCCESS.ordinal());
                        logRepository.save(log);
                    }

                    @Override
                    protected void onError(RpcError rpcError) {
                        logger.error("=>>>>>>>>> 创建会议资源错误：{} {}", rpcError.getCode(), rpcError.getMessage());
                        log.setOperResult(OperResultEnum.ERROR.ordinal());
                        logRepository.save(log);
                        ret[0] = 2;
                    }

                    @Override
                    protected void onTimeout() {
                        logger.error("=>>>>>>>>> 创建会议资源超时无响应");
                        log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                        logRepository.save(log);
                        ret[0] = 3;
                    }
                }
        );

        //等待1s，结果返回
        if (ret[0] == 0) {
            sleepMillSeconds(1000);
        }
        if (!conferences.isEmpty()) {
            return conferences.get(0);
        }
        else
            return null;
    }


    private void sleepMillSeconds(int millsecondes) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(millsecondes);
    }

    @Transactional
    private Conference saveConference(ConferenceStartShow conferenceStartShow, String conferenceId, int isInRecording, int roomId) {
        Conference conference = new Conference();
        conference.setConductorId(conferenceStartShow.getConductorId());
        conference.setResId(conferenceId);
        conference.setRoomId(roomId);
        ConferenceRoomShow conferenceRoomShow = conferenceRoomService.getOne(roomId);
        if (conferenceRoomShow != null) {
            conference.setRoomNo(conferenceRoomShow.getNumber());
        }
        Conductor conductor = conductorService.getOne(conferenceStartShow.getConductorId());
        if (conductor != null) {
            conference.setConductorName(conductor.getRealname());
        }
        conference.setStartAt(DateUtil.transServerTimeToBeiJingTime(new Date()));
        conference.setStatus(1);
        conference.setIsInRecording(isInRecording);
        return conferenceRepository.save(conference);
    }


    /**
     * 结束会议
     * @param resId
     * @param tokenName
     * @return 0 成功 1 失败 3 超时
     * @throws IOException
     */
    @Override
    public int stopConference(String resId, String tokenName) throws IOException, InterruptedException {
        logger.info("=>>>>>>>>> 结束会议resId:{}", resId);

        //会议不存在
        if (checkConfExist(resId) != 1) {
            logger.info("=>>>>>>>>> 会议 {} 不存在", resId);
            return 4;
        }

        //将所有电话挂断
        List<Integer> status = new ArrayList<>();
        status.add(1);
        status.add(2);
        List<Call> calls = callRepository.findCallByConfResIdAndStatus(resId, status);
        for (Call call : calls) {
            IpscUtil.drop(call.getResId());
            callRepository.updateStatus(call.getResId(), 3);
        }

        Log log = new Log(OperResTypeEnum.CONFERENCE.ordinal(),"sys.conf.release", "删除会议", tokenName, OperTypeEnum.OPERATE.ordinal());
        logRepository.save(log);
//        int[] ret = new int[1];
//        IpscUtil.stopConference(resId, new RpcResultListener() {
//            @Override
//            protected void onResult(Object o) {
//                logger.info("=>>>>>>>>> 结束会议{}成功", resId);
//                Conference conference = conferenceRepository.findByResId(resId);
//                conference.setStatus(2);
//                conferenceRepository.save(conference);
//
//                log.setOperResult(OperResultEnum.SUCCESS.ordinal());
//                log.setOperResId(resId);
//                logRepository.save(log);
//
//                ret[0] = 1;
//
//            }
//
//            @Override
//            protected void onError(RpcError rpcError) {
//                logger.error("=>>>>>>>>> 结束会议{}失败", resId);
//                log.setOperResult(OperResultEnum.ERROR.ordinal());
//                log.setOperResId(resId);
//                logRepository.save(log);
//                ret[0] = 2;
//            }
//
//            @Override
//            protected void onTimeout() {
//                logger.info("=>>>>>>>>> 结束会议{}超时", resId);
//                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
//                logRepository.save(log);
//                ret[0] = 3;
//            }
//        });
//        //等待1s
//        if (ret[0] == 0) {
//            sleepMillSeconds(1000);
//        }
//        return ret[0];
        return 1;
    }



    @Override
    public int addCallToConf(List<String> phones, String conferenceId, String tokenName) throws IOException, InterruptedException {
        logger.info("=>>>>>>>>> 呼叫 {} 加入会议 {}", phones, conferenceId);
        Conference conference = conferenceRepository.findByResId(conferenceId);
        //会议不存在
        if (checkConfExist(conferenceId) != 1 || conference == null) {
            logger.info("=>>>>>>>>> 会议 {} 不存在", conferenceId);
            return 4;
        }
        //判断电话是否正在参加会议
        List<String> phoneInConf = getPhoneIsInUsing(phones.toArray(new String[phones.size()]));
        if (!phoneInConf.isEmpty()) {
            logger.info("=>>>>>>>>> 电话{}正在参会", phoneInConf);
            throw new ServiceException("电话:"+phoneInConf+"正在参会中");
        }

        List<Contact> contacts = contactRepository.findByPhones(phones);
        List<String> exsistPhones = callRepository.findPhoneByConfResIdAndPhonesAndStatus(conferenceId, phones, 3);
        for (Contact contact : contacts) {

            if (!exsistPhones.isEmpty()) {
                if (exsistPhones.contains(contact.getPhone())) {
                    List<Call> calls = callRepository.findCallByConfResIdAndPhoneAndStatus(conferenceId, contact.getPhone(), 3);
                    if (calls.size() == 1) {
                        Log log = new Log(OperResTypeEnum.CALL.ordinal(), "二次呼叫", tokenName, OperTypeEnum.CREATE.ordinal());
                        Call call = calls.get(0);
                        call.setStatus(1);
                        callRepository.save(call);
                        RpcResultListener rpcResultListener = new CreateCallRpcResultListener(log, logRepository, "sys.call.construct", call, callRepository, true);
                        IpscUtil.createCallRes(contact.getPhone(), rpcResultListener);
                    }
                }
            } else {
                Log log = new Log(OperResTypeEnum.CALL.ordinal(), "创建呼叫资源", tokenName, OperTypeEnum.CREATE.ordinal());
                Call call = new Call();
                call.setConductorId(conference.getConductorId());
                call.setConfResId(conference.getResId());
                call.setDerection(2);
                call.setRoomId(conference.getRoomId());
                call.setStatus(1);
                call.setStartAt(DateUtil.transServerTimeToBeiJingTime(new Date()));
                call.setPhone(contact.getPhone());
                call.setName(contact.getName());
                call.setContactId(contact.getId());
                call.setVoiceMode(1);
                RpcResultListener rpcResultListener = new CreateCallRpcResultListener(log, logRepository, "sys.call.construct", call, callRepository, false);
                IpscUtil.createCallRes(contact.getPhone(), rpcResultListener);
            }
        }

        return 1;
    }


    /**
     * 临时加入会议
     * @param phone
     * @param conferenceId
     * @param tokenName
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int addPhoneToConf(String phone, String conferenceId, String tokenName) throws IOException, InterruptedException {
        logger.info("=>>>>>>>>> 临时呼叫电话 {} 加入会议 {}", phone, conferenceId);
        Conference conference = conferenceRepository.findByResId(conferenceId);
        //会议不存在
        if (checkConfExist(conferenceId) != 1 || conference == null) {
            logger.info("=>>>>>>>>> 会议 {} 不存在", conferenceId);
            return 4;
        }
        //判断电话是否正在参加会议
        List<String> phoneInConf = getPhoneIsInUsing(phone);
        if (!phoneInConf.isEmpty()) {
            logger.info("=>>>>>>>>> 电话{}正在参会", phoneInConf);
            throw new ServiceException("电话:"+phoneInConf+"正在参会中");
        }

        Log log = new Log(OperResTypeEnum.CALL.ordinal(), "创建呼叫资源", tokenName, OperTypeEnum.CREATE.ordinal());
        Call call = new Call();
        call.setConductorId(conference.getConductorId());
        call.setConfResId(conference.getResId());
        call.setDerection(2);
        call.setRoomId(conference.getRoomId());
        call.setStatus(1);
        call.setStartAt(new Date());
        call.setPhone(phone);
        call.setVoiceMode(1);
        call.setName("临时参会人");
        RpcResultListener listener = new CreateCallRpcResultListener(log, logRepository, "sys.call", call, callRepository, false);
        IpscUtil.createCallRes(phone, listener);
        return 1;
    }


    /**
     * 查询正在会议中的电话
     * @param phone
     * @return
     */
    private List<String> getPhoneIsInUsing(String... phone){

        List<String> phones = new ArrayList<>(Arrays.asList(phone));
        return callRepository.findCallIsOnByPhone(phones);
    }



    @Override
    public int startRecord(String conferenceId, String tokenName) throws IOException, InterruptedException {

        SysSettingShow sysSettingShow = sysSettingService.getLatestSetting();
        if (sysSettingShow == null) {
            return 4;
        }
        String path = sysSettingShow.getRecordingPath();
        if (path == null || path.isEmpty()) {
            return 4;
        }
        //会议不存在
        Conference conference = conferenceRepository.findByResId(conferenceId);
        if (conference == null || checkConfExist(conferenceId) != 1) {
            return 5;
        }

        //会议已是录音状态
        if (conference.getIsInRecording() == 1) {
            return 6;
        }

        String filename = CommonUtils.getRecordFileName(path);
        Log log = new Log(OperResTypeEnum.CONFERENCE.ordinal(),"sys.conf.record_start", "开始录音", tokenName, OperTypeEnum.OPERATE.ordinal());
        int[] ret = new int[1];
        IpscUtil.startRecord(conferenceId, filename, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("会议{}开始录音", conferenceId);
                log.setOperResId(conferenceId);
                log.setOperResult(OperResultEnum.SUCCESS.ordinal());
                logRepository.save(log);
                conference.setIsInRecording(1);
                conferenceRepository.save(conference);
                ret[0] = 1;
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("会议{}开始录音失败, errorMsg={}", conferenceId, rpcError.getMessage());
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                log.setOperResId(conferenceId);
                logRepository.save(log);
                ret[0] = 2;
            }

            @Override
            protected void onTimeout() {
                logger.info("会议{}开始录音超时", conferenceId);
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                log.setOperResId(conferenceId);
                logRepository.save(log);
                ret[0] = 3;
            }
        });
        //等待1s
        if (ret[0] == 0) {
            sleepMillSeconds(1000);
        }
        return ret[0];
    }

    @Override
    public int stopRecord(String conferenceId, String tokenName) throws IOException, InterruptedException {
        Log log = new Log(OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.record_stop", "停止录音", tokenName, OperTypeEnum.OPERATE.ordinal());
        Conference conference = conferenceRepository.findByResId(conferenceId);
        //会议不存在
        if (checkConfExist(conferenceId) != 1 || conference == null) {
            return 4;
        }

        //该会议没有录音
        if (conference.getIsInRecording() == 0) {
            return 6;
        }
        int[] ret = new int[1];
        IpscUtil.stopRecord(conferenceId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("会议{}已结束录音", conferenceId);
                log.setOperResult(OperResultEnum.SUCCESS.ordinal());
                log.setOperResId(conferenceId);
                logRepository.save(log);

                //将会议是否录音状态改为没有录音
                conference.setIsInRecording(0);
                conferenceRepository.save(conference);
                ret[0] = 1;
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("会议{}结束录音失败, errorMsg={}", conferenceId, rpcError.getMessage());
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                log.setOperResId(conferenceId);
                logRepository.save(log);
                ret[0] = 2;
            }

            @Override
            protected void onTimeout() {
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                log.setOperResId(conferenceId);
                logRepository.save(log);
                logger.info("会议{}结束录音超时", conferenceId);
                ret[0] = 3;
            }
        });
        //等待1s
        if (ret[0] == 0) {
            sleepMillSeconds(1000);
        }
        return ret[0];
    }

    @Override
    public int removeCallFromConf(String callId, String conferenceId, String tokenName) throws IOException, InterruptedException {
        logger.info("=>>>>>>>>> 从会议 {} 移除呼叫{}", callId, conferenceId);
        Log log = new Log(OperResTypeEnum.CALL.ordinal(),"sys.call.drop", "挂断呼叫", tokenName, OperTypeEnum.OPERATE.ordinal());

        //会议不存在
        if (checkConfExist(conferenceId) != 1) {
            return 4;
        }

        //呼叫不存在
        if (checkCallExist(callId) != 1) {
            return 5;
        }

        int[] ret = new int[1];
        IpscUtil.dropCall(callId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("=>>>>>>>>> 离开会议成功，confId={},callId={}", conferenceId, callId);
                log.setOperResult(OperResultEnum.SUCCESS.ordinal());
                log.setOperResId(callId);
                logRepository.save(log);
                //修改状态
                callRepository.updateStatus(callId, 3);

                //推送消息
                changeReminder.sendMessageToAll(conferenceId);
                ret[0] = 1;
            }

            @Override
            protected void onError(RpcError rpcError) {
                log.setOperResId(callId);
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                logger.info("=>>>>>>>>> 离开会议错误，confId={},callId={}, errorMsg={}", conferenceId, callId, rpcError.getMessage());

                ret[0] = 2;
            }

            @Override
            protected void onTimeout() {
                log.setOperResId(callId);
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                logger.info("=>>>>>>>>> 离开会议超时，confId={},callId={}", conferenceId, callId);
                ret[0] = 3;
            }
        });
        //等待1s
        if (ret[0] == 0) {
            sleepMillSeconds(1000);
        }
        return ret[0];
    }

    @Override
    public int changeCallMode(String callId, String conferenceId, int mode, String tokenName) throws IOException, InterruptedException {
        logger.info("=>>>>>>>>> 改变与会者声音模式，callId={},confId={},mode={}", callId, conferenceId, mode);
        Log log = new Log(OperResTypeEnum.CONFERENCE.ordinal(),"sys.conf.set_part_voice_mode",
                "改变与会者的声音收放模式", tokenName, OperTypeEnum.OPERATE.ordinal());
        int[] ret = new int[1];
        //会议不存在
        if (checkConfExist(conferenceId) != 1) {
            return 4;
        }

        //呼叫不存在
        if (checkCallExist(callId) != 1) {
            return 5;
        }
        IpscUtil.changePartMode(conferenceId, callId, mode, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("=>>>>>>>>> 修改与会者声音模式成功，confId={},callId={},mode={}", conferenceId, callId, mode);
                log.setOperResId(callId);
                log.setOperResult(OperResultEnum.SUCCESS.ordinal());
                logRepository.save(log);

                //修改声音收放模式
                callRepository.updateVoiceMode(callId, mode);

                //发送通知
                changeReminder.sendMessageToAll(conferenceId);
                ret[0] = 1;
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.error("=>>>>>>>>> 修改与会者声音模式失败，confId={},callId={},mode={} , errorMsg={}", conferenceId, callId, mode, rpcError.getMessage());
                log.setOperResult(OperResultEnum.ERROR.ordinal());
                log.setOperResId(callId);
                logRepository.save(log);
                ret[0] = 2;
            }

            @Override
            protected void onTimeout() {
                logger.error("=>>>>>>>>> 修改与会者声音模式超时，confId={},callId={},mode={}", conferenceId, callId, mode);
                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
                log.setOperResId(callId);
                logRepository.save(log);
                ret[0] = 3;
            }
        });
        //等待1s
        if (ret[0] == 0) {
            sleepMillSeconds(1000);
        }
        return ret[0];
    }



//    @Override
//    public Page<ConferencePart> getConfParts(String confResId, String username, String phone, Integer currentPage, Integer pageSize) throws IOException, InterruptedException {
//
//        logger.info("=>>>>>>>>> 获取会议{}与会者列表", confResId);
//        Log log = new Log(confResId, OperResTypeEnum.CONFERENCE.ordinal(), "sys.conf.get_parts",
//                "获取会议与会者列表", username, OperTypeEnum.OPERATE.ordinal(), OperResultEnum.SUCCESS.ordinal());
//        List<ConferencePart> conferenceParts = new ArrayList<ConferencePart>();
//        Pageable page = PageUtil.createPage(currentPage, pageSize);
//        if (checkConfExist(confResId) != 1) {
//            return new PageImpl<>(conferenceParts, page, 0);
//        }
//        int[] ret = new int[1];
//        IpscUtil.getConfParts(confResId, new RpcResultListener() {
//            @Override
//            protected void onResult(Object o) {
//                logger.info("=>>>>>>>>> 获取与会者列表返回结果：{}", o.toString());
//                String array = JSON.toJSONString(o);
//                JSONArray      jsonArray = JSON.parseArray(array);
//                List<PartData> partDatas = jsonArray.toJavaList(PartData.class);
//                for (PartData partData : partDatas) {
//                    ConferencePart part = new ConferencePart();
//                    part.setVoiceMode(partData.getVoice_mode());
//                    part.setCallId(partData.getRes_id());
//
//                    String phone = partData.getUser_data();
//                    if (phone == null) {
//                        part.setPhone("未知");
//                    } else {
//                        part.setPhone(phone);
//                    }
//                    conferenceParts.add(part);
//                }
//                logRepository.save(log);
//                ret[0] = 1;
//            }
//
//            @Override
//            protected void onError(RpcError rpcError) {
//                logger.info("=>>>>>>>>> 获取与会者列表失败: {} {}", rpcError.getCode(), rpcError.getMessage());
//                log.setOperResult(OperResultEnum.ERROR.ordinal());
//                logRepository.save(log);
//                ret[0] = 2;
//            }
//
//            @Override
//            protected void onTimeout() {
//                logger.info("=>>>>>>>>> 获取与会者列表超时");
//                log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
//                logRepository.save(log);
//                ret[0] = 3;
//            }
//        });
//        //等待1s
//        if (ret[0] == 0) {
//            sleepMillSeconds(1000);
//        }
//
//        if (StringUtils.isEmpty(phone)) {
//            return new PageImpl<>(conferenceParts, page, (long)conferenceParts.size());
//        }
//        List<ConferencePart> conferenceParts1 = conferenceParts.stream()
//                .filter(e -> e.getPhone().contains(phone))
//                .collect(Collectors.toList());
//        return new PageImpl<>(conferenceParts1, page, (long)conferenceParts1.size());
//    }

    @Override
    public int checkConfExist(String confResId) throws InterruptedException, IOException {

        int[] ret = new int[1];
        IpscUtil.checkConf(confResId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("=>>>>>>>>> 获取会议资源是否存在成功，结果：{}", o.toString());
                ret[0] = 1;
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("=>>>>>>>>> 会议资源是否存在错误：code {}, message {}", rpcError.getCode(), rpcError.getMessage());
                ret[0] = 2;
            }

            @Override
            protected void onTimeout() {
                logger.info("=>>>>>>>>> 会议资源是否存在超时");
                ret[0] = 3;
            }
        });
        //等待1s
        if (ret[0] == 0) {
            sleepMillSeconds(1000);
        }
        return ret[0];
    }


    @Override
    public int checkCallExist(String callId) throws InterruptedException, IOException {

        int[] ret = new int[1];
        IpscUtil.checkCall(callId, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info("=>>>>>>>>> 获取呼叫资源是否存在成功，结果：{}", o.toString());
                ret[0] = 1;
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info("=>>>>>>>>> 获取呼叫资源是否存在错误：code {}, message {}", rpcError.getCode(), rpcError.getMessage());
                ret[0] = 2;
            }

            @Override
            protected void onTimeout() {
                logger.info("=>>>>>>>>> 获取呼叫资源是否存在超时");
                ret[0] = 3;
            }
        });
        //等待1s
        if (ret[0] == 0) {
            sleepMillSeconds(1000);
        }
        return ret[0];
    }


//    private static class PartData{
//        private Integer voice_mode;
//        private String res_id;
//        private Integer chan;
//        private String user_data;
//
//        public Integer getVoice_mode() {
//            return voice_mode;
//        }
//
//        public void setVoice_mode(Integer voice_mode) {
//            this.voice_mode = voice_mode;
//        }
//
//        public String getRes_id() {
//            return res_id;
//        }
//
//        public void setRes_id(String res_id) {
//            this.res_id = res_id;
//        }
//
//        public Integer getChan() {
//            return chan;
//        }
//
//        public void setChan(Integer chan) {
//            this.chan = chan;
//        }
//
//        public String getUser_data() {
//            return user_data;
//        }
//
//        public void setUser_data(String user_data) {
//            this.user_data = user_data;
//        }
//    }
}

package cn.snzo.utils;

import cn.snzo.common.Constants;
import cn.snzo.entity.*;
import cn.snzo.repository.*;
import cn.snzo.ws.ChangeReminder;
import com.hesong.ipsc.ccf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chentao on 2017/7/14 0014.
 */
@Component
public class IpscUtil {

    private static Logger logger = LoggerFactory.getLogger(IpscUtil.class);

    private static ConferenceRepository conferenceRepository;



    @Autowired
    public void setConferenceRepository(ConferenceRepository conferenceRepository) {
        IpscUtil.conferenceRepository = conferenceRepository;
    }

    private static RecordingRepository  recordingRepository;

    @Autowired
    public void setRecordingRepository(RecordingRepository recordingRepository) {
        IpscUtil.recordingRepository = recordingRepository;
    }

    private static ConferenceRoomRepository conferenceRoomRepository;

    @Autowired
    public  void setConferenceRoomRepository(ConferenceRoomRepository conferenceRoomRepository) {
        IpscUtil.conferenceRoomRepository = conferenceRoomRepository;
    }

    private static ChangeReminder changeReminder;

    @Autowired
    public void setChangeReminder(ChangeReminder changeReminder) {
        IpscUtil.changeReminder = changeReminder;
    }

    private static ContactRepository contactRepository;

    @Autowired
    public void setContactRepository(ContactRepository contactRepository) {
        IpscUtil.contactRepository = contactRepository;
    }

    private static CallRepository callRepository;


    @Autowired
    public void setCallRepository(CallRepository callRepository) {
        IpscUtil.callRepository = callRepository;
    }

    public static final String VOIP = "10.1.2.152";

    //    private static final String ipscIpAddr = "192.168.2.100"; /// IPSC 服务器的内网地址
    private static final String              ipscIpAddr  = "127.0.0.1"; /// IPSC 服务器的内网地址
    private static final byte                localId     = 24;
    private static final byte                commanderId = 10;
    public static        Commander           commander   = null;
    public static        BusAddress          busAddress  = null;
    public static        Map<String, String> callConfMap = new HashMap<>();
    public static        Map<String, String> callPhoneMap = new HashMap<>();
    public static        Map<String, Integer> callEnterDtfmCount = new HashMap<>(); //每个呼入呼叫最多输入三次密码


    //    @Override
    public static void init() throws InterruptedException {
        logger.info(">>>>>>>>> Data Bus 客户端单元初始化");
        Unit.initiate(localId, new UnitCallbacks() {
            public void connectSucceed(Client client) {
                logger.info(">>>>>>>>> 成功的连接到了IPSC服务程序的 Data Bus");
                busAddress = new BusAddress(commander.getConnectingUnitId(), (byte) 0);
            }

            public void connectFailed(Client client, int i) {
                System.out.format("[{}] 连接失败", client.getId());
            }

            public void connectLost(Client client) {
                System.out.format("[{}] 连接丢失", client.getId());
            }

            public void globalConnectStateChanged(byte b, byte b1, byte b2, byte b3, String s) {
            }
        });

        Thread.sleep(20000);
        //创建commander
        logger.info(">>>>>>>>> 初始化commander");
        createCommander();
    }


    public static void createCommander() throws InterruptedException {
        /// 新建一个命令发送者
        commander = Unit.createCommander(
                commanderId,
                ipscIpAddr,
                // 事件监听器
                new RpcEventListener() {
                    public void onEvent(BusAddress busAddress, RpcRequest rpcRequest) {
                        String fullMethodName = rpcRequest.getMethod();
                        if (fullMethodName.startsWith("sys.call")) {
                            /// 呼叫事件
                            String methodName = fullMethodName.substring(9);
                            final String callId = (String) rpcRequest.getParams().get("res_id");
                            if (methodName.equals("on_released")) {
                                logger.warn(">>>>>>>>> 呼叫 {} 已经释放", callId);

                                //修改该呼叫的状态为已释放
                                callRepository.updateStatus(callId, 3);

                                //推送socket消息
                                String confId = callConfMap.get(callId);
                                if (confId != null) {
                                    changeReminder.sendMessageToAll(confId);
                                }
                                //将呼叫从缓存中清除
                                callConfMap.remove(callId);
                            } else if (methodName.equals("on_ringing")) {
                                logger.info(">>>>>>>>> 呼叫 {} 振铃", callId);
                            } else if (methodName.equals("on_dial_completed")) {
                                String error = (String) rpcRequest.getParams().get("error");
                                if (error == null) {
                                    String confId = callConfMap.get(callId);
                                    if (confId != null) {
                                        logger.info(">>>>>>>>> 呼叫 {} 拨号成功，操作呼叫资源，让它加入会议 {} ...", callId, confId);
                                        playReadyVoice(callId, confId);

                                        logger.info(">>>>>>>>> 播放滴声{}", Constants.COME_IN_TICK);
                                        playConfVoice(confId, Constants.COME_IN_TICK);

                                        logger.info(">>>>>>>>> 播放滴声完毕{}", Constants.COME_IN_TICK);
                                        callRepository.updateStatus(callId, 2);
                                        changeReminder.sendMessageToAll(confId);
                                    }
                                } else {
                                    //设置为未接听
                                    callRepository.updateStatus(callId, 4);
                                    logger.error(">>>>>>>>> 呼叫 {} 拨号失败：{}", callId, error);
                                }
                            } else if (methodName.equals("on_record_completed")) {
                                String error = (String) rpcRequest.getParams().get("error");
                                logger.info(">>>>>>>>> 录音停止callId: {} error: {}", callId, error);
                            } else if (methodName.equals("on_incoming")) {
                                String error = (String) rpcRequest.getParams().get("error");
                                if (error != null) {
                                    logger.error(">>>>>>>>> 呼入呼叫错误，callId ={}", callId);
                                } else {
                                    logger.info(">>>>>>>>> 呼入呼叫参数:{}", rpcRequest.getParams());
                                    Call newCall = new Call();
                                    newCall.setResId(callId);
                                    String fromUri = (String) rpcRequest.getParams().get("from_uri");
                                    //fromuri 格式 sip:18627720789@10.1.2.152
                                    String phone = fromUri.substring(fromUri.indexOf(":")+1, fromUri.indexOf("@"));
                                    newCall.setPhone(phone);
                                    newCall.setStatus(2);
                                    newCall.setVoiceMode(1);
                                    Contact contact = contactRepository.findByPhone(phone);
                                    newCall.setName(phone);
                                    if (contact != null) {
                                        newCall.setName(contact.getName());
                                    }

                                    newCall.setDerection(1);
                                    int beginTime =  (int) rpcRequest.getParams().get("begin_time");
                                    newCall.setStartAt(DateUtil.transServerTimeToBeiJingTime(new Date(beginTime * 1000)));
                                    callRepository.save(newCall);
                                    answer(callId);
                                }

                            } else if (methodName.equals("on_receive_dtmf_completed")) {
                                String error = (String) rpcRequest.getParams().get("error");
                                if (error == null) {
                                    String keys = (String)rpcRequest.getParams().get("keys");
                                    logger.info(">>>>>>>>>接收到dtmf码为：{}", keys);
                                    ConferenceRoom conferenceRoom = conferenceRoomRepository.findByIvrPassword(keys);
                                    boolean isRight = false;
                                    boolean isOpen = false;
                                    Conference conference = null;
                                    if (conferenceRoom != null)
                                    {
                                        isRight  = true;
                                        //查询该会议室中正在进行的会议
                                        conference = conferenceRepository.findByRoomIdAndStatus(conferenceRoom.getId(), 1);
                                        if (conference != null) {
                                            isOpen = true;
                                        }
                                    }
                                    boolean isRightAndOpen = isRight && isOpen;
                                    if (isRightAndOpen) {
                                        logger.info(">>>>>>>>>接收到的dtmf码与会议室ivr码相同,播放欢迎语音{}", Constants.READY_VOICE);
                                        logger.info(">>>>>>>>> 播放欢迎语音{}", Constants.WELCOME_VOICE);
                                        playReadyVoice(callId, conference.getResId());
                                        logger.info(">>>>>>>>> 播放滴声{}", Constants.COME_IN_TICK);
                                        playConfVoice(conference.getResId(), Constants.COME_IN_TICK);

                                        logger.info(">>>>>>>>> 播放滴声完毕{}", Constants.COME_IN_TICK);
                                    } else {
                                        logger.info(">>>>>>>>>接收到的dtmf码错误，播放错误提示音");
                                        Integer count = callEnterDtfmCount.get(callId);
                                        playWrongVoice(callId, count, isRight);
                                    }

                                }
                            } else if (methodName.equals("on_answer_completed")) {
                                logger.warn(">>>>>>>>> 呼入呼叫成功被接听，callId ={}", callId);
                                String error = (String) rpcRequest.getParams().get("error");
                                if (error != null) {
                                    logger.error(">>>>>>>>> 接听呼入呼叫{}发生错误", callId);
                                } else {
                                    logger.info(">>>>>>>>> 成功接听呼入呼叫{}，开始接收dtmf码");
                                    callReceiveDtmfStart(callId, Constants.WELCOME_VOICE);
                                }
                            }
                        } else if (fullMethodName.startsWith("sys.conf")) {
                            /// 会议事件
                            String methodName = fullMethodName.substring(9);
                            String confId = (String) rpcRequest.getParams().get("res_id");
                            if (methodName.equals("on_released")) {
                                logger.warn(">>>>>>>>> 会议 {} 已经释放", confId);
                                //会议结束，推送通知消息
                                changeReminder.sendMessageToAll(confId);
                                //修改为已结束状态
                                Conference conference = conferenceRepository.findByResId(confId);
                                conference.setStatus(2);
                                conferenceRepository.save(conference);
                            } else if (methodName.equals("on_record_completed")) {
                                logger.warn(">>>>>>>>> 会议 {} 录音已结束", confId);
                                String error = (String) rpcRequest.getParams().get("error");
                                if (error == null) {
                                    logger.warn(">>>>>>>>> 保存会议 {} 录音信息", confId);
                                    Recording recording = new Recording();
                                    Conference conference = conferenceRepository.findByResId(confId);
                                    if (conference != null) {
                                        recording.setConductorId(conference.getConductorId());
                                        recording.setConductorName(conference.getConductorName());
                                        recording.setConferenceId(conference.getId());
                                        recording.setConferenceNo(conference.getResId());
                                        String filename = (String) rpcRequest.getParams().get("record_file");
                                        logger.info(">>>>>>>>> 会议{}录音保存绝对路径：{}",conference.getResId(), filename);
                                        int index = filename.lastIndexOf("/");
                                        if (index != -1) {
                                            recording.setFilename(filename.substring(index + 1));
                                            int length = Constants.SFTP_PATH.length();
                                            //保存的是sftp目录下的子目录
                                            String filepath = filename.substring(length, index);
                                            logger.info(">>>>>>>>> 会议{}录音保存相对sftp路径：{}",conference.getResId(), filepath);
                                            recording.setFilePath(filepath);
                                        }

                                        Integer start = (Integer) rpcRequest.getParams().get("begin_time");
                                        Integer end = (Integer) rpcRequest.getParams().get("end_time");
                                        recording.setStartTime(DateUtil.transServerTimeToBeiJingTime(new Date((long)start * 1000)));
                                        recording.setEndTime(DateUtil.transServerTimeToBeiJingTime(new Date((long)end * 1000)));
                                        recording.setRoomId(conference.getRoomId());
                                        recording.setRoomNo(conference.getRoomNo());
                                        recordingRepository.save(recording);
                                    }
                                }
                            }
                        }
                    }
                });
    }


    public static void playReadyVoice(String callId, String confResId) {
        playContent(callId, Constants.READY_VOICE, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info(">>>>>>>>> 播放欢迎语音{}成功", Constants.READY_VOICE);
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    logger.error(">>>>>>>>>播放欢迎语音{}被中断", Constants.READY_VOICE);
                }

                logger.info(">>>>>>>>>将该呼叫{}加入会议{}", callId, confResId);
                addCallToConf(callId, confResId);
                logger.info(">>>>>>>>>将该呼叫{}加入会议{}完毕", callId, confResId);

            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.error(">>>>>>>>> 播放欢迎语音{}错误,code={},message={}", Constants.READY_VOICE, rpcError.getCode(), rpcError.getMessage());
            }

            @Override
            protected void onTimeout() {
                logger.error(">>>>>>>>> 播放欢迎语音{}超时", Constants.READY_VOICE);
            }
        });
    }

    public static void addCallToConf(String callId, String conferenceId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("res_id", callId);
            params.put("conf_res_id", conferenceId);
            params.put("max_seconds", Constants.MAX_CONF_SECONDS);
            commander.operateResource(
                    busAddress,
                    callId,
                    "sys.call.conf_enter",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            logger.info(">>>>>>>>> 呼叫 {} 加入会议 {} 操作完毕", callId, conferenceId);

                            Call call = callRepository.findByResId(callId);
                            if (call != null) {
                                call.setStatus(2);
                                call.setConfResId(conferenceId);
                                callRepository.save(call);
                            }
                            callConfMap.put(callId, conferenceId);
                            //往前端推送socket消息
                            changeReminder.sendMessageToAll(conferenceId);

                        }

                        @Override
                        protected void onError(RpcError rpcError) {
                            logger.error(">>>>>>>>> 呼叫 {} 加入会议 {} 操作错误: {}", callId, conferenceId, rpcError.getMessage());
                        }

                        @Override
                        protected void onTimeout() {
                            logger.error(">>>>>>>>> 呼叫 {} 加入会议 {} 操作超时无响应", callId, conferenceId);
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void playConfVoice(String confResId, String fileName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("res_id", confResId);
        params.put("content", fileName);
        try {
            commander.operateResource(
                    busAddress,
                    confResId,
                    "sys.conf.play_start",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            logger.error("播放滴声会议{}滴声", confResId, fileName);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        protected void onError(RpcError rpcError) {

                        }

                        @Override
                        protected void onTimeout() {

                        }
                    });

        } catch (IOException e) {
            logger.error("播放会议{}声音文件{}异常", confResId, fileName);
        }
    }


    public static void createCallRes(String phone, RpcResultListener rpcResultListener) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("to_uri", phone + "@" + VOIP); /// 被叫号码的 SIP URI
        params.put("max_answer_seconds", Constants.MAX_ANSWER_SECONDS); /// 该呼叫最长通话允许时间
        params.put("user_data", phone); ///用户信息
        logger.info(">>>>>>>>> 呼叫参数： {}", params);
        commander.createResource(
                busAddress,
                "sys.call",
                params,
                rpcResultListener
        );
    }

    public static void createConference(Map<String, Object> params, RpcResultListener rpcResultListener) throws IOException {
        logger.info(">>>>>>>>> createConference params {}", params);
        commander.createResource(
                busAddress,
                "sys.conf",
                params,
                rpcResultListener);
    }


    public static void stopConference(String confId, RpcResultListener listener) throws IOException {
        commander.operateResource(
                busAddress,
                confId,
                "sys.conf",
                null,
                listener);

    }


    public static void changePartMode(String confId, String callId, int mode, RpcResultListener listener) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("res_id", confId);
        params.put("call_res_id", callId);
        params.put("mode", mode);
        logger.info(">>>>>>>>> 修改与会者声音收放模式，confid={},callId={},mode={}", confId, callId, mode);
        commander.operateResource(
                busAddress,
                confId,
                "sys.conf.set_part_voice_mode",
                params,
                listener);
    }

    public static void exitConferece(String confId, String callId, RpcResultListener listener) throws IOException {
        if (commander == null) {
            logger.info(">>>>>>>>> commander客户端 未初始化");
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("res_id", confId);
        params.put("call_res_id", callId);
        logger.info(">>>>>>>>> 退出会议，confid={},callId={}", confId, callId);
        commander.operateResource(
                busAddress,
                callId,
                "sys.call.conf_exit",
                params,
                listener);
    }


    public static void dropCall(String callId, RpcResultListener listener) throws IOException {
        if (commander == null) {
            logger.info(">>>>>>>>> commander客户端 未初始化");
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("res_id", callId);
        logger.info(">>>>>>>>> 挂断呼叫,callId={}", callId);
        commander.operateResource(
                busAddress,
                callId,
                "sys.call.drop",
                params,
                listener);

    }



    public static void startRecord(String conferenceId, String path, RpcResultListener listener) throws IOException {
        if (commander != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("res_id", conferenceId);
            params.put("max_seconds", Constants.MAX_CONF_SECONDS);
            params.put("record_file", path);
            params.put("record_format", 3);
            commander.operateResource(
                    busAddress,
                    conferenceId,
                    "sys.conf.record_start",
                    params,
                    listener);
        } else {
            logger.info(">>>>>>>>> commander客户端 未初始化");
        }

    }

    public static void stopRecord(String conferenceId, RpcResultListener listener) throws IOException {
        if (commander != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("res_id", conferenceId);
            commander.operateResource(
                    busAddress,
                    conferenceId,
                    "sys.conf.record_stop",
                    params,
                    listener);
        } else {
            logger.info(">>>>>>>>> commander客户端 未初始化");
        }
    }

    public static void getConfParts(String confResId, RpcResultListener listener) throws IOException {

        Map<String, Object> params = new HashMap<>();
        params.put("res_id", confResId);
        commander.operateResource(
                busAddress,
                confResId,
                "sys.conf.get_parts",
                params,
                listener);
    }

    public static void checkConf(String confResId, RpcResultListener listener) throws IOException {
        if (commander == null) {
            logger.info(">>>>>>>>> commander客户端 未初始化");
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("res_id", confResId);
        commander.operateResource(
                busAddress,
                confResId,
                "sys.conf.exists",
                params,
                listener);

    }

    public static void checkCall(String callId, RpcResultListener listener) throws IOException {
        if (commander == null) {
            logger.info(">>>>>>>>> commander客户端 未初始化");
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("res_id", callId);
        commander.operateResource(
                busAddress,
                callId,
                "sys.call.exists",
                params,
                listener);
    }


    public static void playWrongVoice(String callId, Integer count, boolean isRight) {

        //如果该呼叫输入错误3次，播放提示音 final_wrong_passwd.wav
        count = count == null ? 1 : count;
        if (count < 3) {
            logger.info(">>>>>>>>> 输入错误{}次，呼叫id={}",count, callId);
            if (isRight) {
                callReceiveDtmfStart(callId, Constants.CLOSED_VOICE);
            } else {
                callReceiveDtmfStart(callId, Constants.WRONG_PASSWORD);
            }
            callEnterDtfmCount.put(callId, ++count);
        } else if (count == 3) {
            logger.info(">>>>>>>>> 输入错误达到3次，呼叫id={}", callId);
            playContent(callId, Constants.FINAL_WRONG_PASSWORD, new RpcResultListener() {
                @Override
                protected void onResult(Object o) {
                    logger.info(">>>>>>>>> 操作呼叫{}放音成功", callId);
                    logger.info(">>>>>>>>> 将该呼叫挂断");
                    logger.info(">>>>>>>>> 等待播放");
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    drop(callId);
                }

                @Override
                protected void onError(RpcError rpcError) {
                    logger.info(">>>>>>>>> 操作呼叫{}放音失败code={}, msg={}", callId, rpcError.getCode(), rpcError.getMessage());
                }

                @Override
                protected void onTimeout() {
                    logger.info(">>>>>>>>> 操作呼叫{}放音成功", callId);
                }
            });
            //从缓存中清除
            callEnterDtfmCount.remove(callId);
        }
    }

    public static void drop(String callId) {
        logger.info("挂断呼叫{}", callId);
        Map<String, Object> params = new HashMap<>();
        params.put("res_id", callId);
        try {
            commander.operateResource(
                    busAddress,
                    callId,
                    "sys.call.drop",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            logger.info(">>>>>>>>> 挂断呼叫{}已拒接", callId);
                        }

                        @Override
                        protected void onError(RpcError rpcError) {
                            logger.info(">>>>>>>>> 挂断呼叫{}失败,code={},msg={}", callId, rpcError.getCode(), rpcError.getMessage());
                        }

                        @Override
                        protected void onTimeout() {
                            logger.info(">>>>>>>>> 挂断呼叫{}超时", callId);
                        }
                    }
            );
        } catch (IOException e) {
            logger.error(">>>>>>>>> 挂断呼叫{}异常", callId);
        }
    }



    public static void reject(String callId) {
        logger.info("拒接呼叫{}", callId);
        Map<String, Object> params = new HashMap<>();
        params.put("res_id", callId);
        params.put("cause", "max wrong enter dtmf");
        try {
            commander.operateResource(
                    busAddress,
                    callId,
                    "sys.call.reject",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            logger.info(">>>>>>>>> 呼叫{}已拒接", callId);
                        }

                        @Override
                        protected void onError(RpcError rpcError) {
                            logger.info(">>>>>>>>> 呼叫{}拒接失败,code={},msg={}", callId, rpcError.getCode(), rpcError.getMessage());
                        }

                        @Override
                        protected void onTimeout() {
                            logger.info(">>>>>>>>> 呼叫{}拒接超时", callId);
                        }
                    }
            );
        } catch (IOException e) {
            logger.error(">>>>>>>>> 拒接呼叫{}异常", callId);
        }
    }


    public static void playContent(String callId, String filename) {
        playContent(callId, filename, new RpcResultListener() {
            @Override
            protected void onResult(Object o) {
                logger.info(">>>>>>>>> 操作呼叫{}放音成功", callId);
            }

            @Override
            protected void onError(RpcError rpcError) {
                logger.info(">>>>>>>>> 操作呼叫{}放音失败", callId);
            }

            @Override
            protected void onTimeout() {
                logger.info(">>>>>>>>> 操作呼叫{}放音超时", callId);
            }
        });
    }


    public static void playContent(String callId, String filename, RpcResultListener rpcResultListener) {
        if (commander == null) {
            logger.info(">>>>>>>>> commander客户端 未初始化");
            return;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("res_id", callId);
            params.put("content", filename);
            commander.operateResource(
                    busAddress,
                    callId,
                    "sys.call.play_start",
                    params,
                    rpcResultListener);
        } catch (IOException e) {
            logger.info(">>>>>>>>> 播放声音失败");
        }

    }


    public static void answer(String callId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("max_answer_seconds", 10000);
        params.put("res_id", callId);
        logger.warn("应答呼入呼叫参数，params ={}", params);
        try {
            commander.operateResource(
                    busAddress,
                    callId,
                    "sys.call.answer",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            logger.info(">>>>>>>>> 应答呼入 {} 操作完毕", callId);
                        }

                        @Override
                        protected void onError(RpcError rpcError) {
                            logger.error(">>>>>>>>> 应答呼入{} 操作错误: {}", callId, rpcError.getMessage());
                        }

                        @Override
                        protected void onTimeout() {
                            logger.error(">>>>>>>>> 应答呼入{} 操作超时无响应", callId);
                        }
                    }
            );
        } catch (IOException e) {
            logger.error(">>>>>>>>> 应答呼入发生异常", e);
        }

    }


    public static void callReceiveDtmfStart(String callId, String filename) {
        try {
            Map<String, Object> paramsDtmfStart = new HashMap<String, Object>();
            paramsDtmfStart.put("res_id", callId);
            paramsDtmfStart.put("play_content", filename);
            commander.operateResource(
                    busAddress,
                    callId,
                    "sys.call.receive_dtmf_start",
                    paramsDtmfStart,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            logger.info(">>>>>>>>> 开始接收DTMF码， callId={}, params={}", callId, paramsDtmfStart);
                        }

                        @Override
                        protected void onError(RpcError rpcError) {
                            logger.info(">>>>>>>>> 开始接收DTMF码发生错误， callId={}, params={}", callId, paramsDtmfStart);
                        }

                        @Override
                        protected void onTimeout() {
                            logger.info(">>>>>>>>> 开始接收DTMF码超时， callId={}, params={}", callId, paramsDtmfStart);
                        }
                    });
        } catch (IOException e) {
            logger.error(">>>>>>>>> 接收呼入码异常", e);
        }
    }



    public static void callOutSinglePhone(String phone, String voip, RpcResultListener rpcResultListener) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("to_uri", phone+"@"+voip); /// 被叫号码的 SIP URI
        params.put("max_answer_seconds", Constants.MAX_ANSWER_SECONDS); /// 该呼叫最长通话允许时间
        params.put("user_data", phone); ///用户信息
        logger.info(">>>>>>>>> 呼叫参数： {}", params);
        commander.createResource(
                busAddress,
                "sys.call",
                params,
                rpcResultListener
        );
    }

//    public static void main(String[] args) {
//        String f = "sip:18627720789@10.1.2.152";
//        System.out.println(f.substring(f.indexOf(":")+1, f.indexOf("@")));
//    }


}

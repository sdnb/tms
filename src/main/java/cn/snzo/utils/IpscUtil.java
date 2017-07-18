package cn.snzo.utils;

import cn.snzo.common.Constants;
import cn.snzo.entity.Conference;
import cn.snzo.entity.Contact;
import cn.snzo.entity.Recording;
import cn.snzo.repository.ConferenceRepository;
import cn.snzo.repository.RecordingRepository;
import com.hesong.ipsc.ccf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
        this.conferenceRepository = conferenceRepository;
    }

    private static RecordingRepository  recordingRepository;

    @Autowired
    public void setRecordingRepository(RecordingRepository recordingRepository) {
        this.recordingRepository = recordingRepository;
    }

    public static final String VOIP = "10.1.2.152";

    //    private static final String ipscIpAddr = "192.168.2.100"; /// IPSC 服务器的内网地址
    private static final String    ipscIpAddr  = "127.0.0.1"; /// IPSC 服务器的内网地址
    private static final byte      localId     = 24;
    private static final byte      commanderId = 10;
    public static        Commander commander   = null;
    public static        BusAddress          busAddress  = null;
    public static        Map<String, String> callConfMap = new HashMap<>();


    //    @Override
    public static void init() throws InterruptedException {
        logger.info("Data Bus 客户端单元初始化");
        Unit.initiate(localId, new UnitCallbacks() {
            public void connectSucceed(Client client) {
                logger.info("成功的连接到了IPSC服务程序的 Data Bus");
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

        Thread.sleep(10000);
        //创建commander
        logger.info("初始化commander");
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
                                logger.warn("呼叫 {} 已经释放", callId);
                            } else if (methodName.equals("on_ringing")) {
                                logger.info("呼叫 {} 振铃", callId);
                            } else if (methodName.equals("on_dial_completed")) {
                                String error = (String) rpcRequest.getParams().get("error");
                                if (error == null) {
                                    String confId = callConfMap.get(callId);
                                    if (confId != null) {
                                        logger.info("呼叫 {} 拨号成功，操作呼叫资源，让它加入会议 {} ...", callId, confId);
                                        addCallToConf(callId, callConfMap.get(callId));
                                    }
                                } else {
                                    logger.error("呼叫 {} 拨号失败：{}", callId, error);
                                }
                            }
                            else if (methodName.equals("on_record_completed")) {
                                String error = (String) rpcRequest.getParams().get("error");
                                logger.info("录音停止callId: {} error: {}", callId, error);
                            } else if (methodName.equals("on_incoming")) {
                                logger.warn("呼入呼叫，callId ={}", callId);
                                try {
                                    String error = (String) rpcRequest.getParams().get("error");
                                    Map<String, Object> params = new HashMap<String, Object>();
                                    params.put("max_answer_seconds", 10000);
                                    params.put("res_id", callId);
                                    if (error == null) {

                                        commander.operateResource(
                                                busAddress,
                                                callId,
                                                "sys.call.answer",
                                                params,
                                                new RpcResultListener() {
                                                    @Override
                                                    protected void onResult(Object o) {
                                                        logger.info("应答呼入 {} 操作完毕", callId, callConfMap.get(callId));
                                                    }

                                                    @Override
                                                    protected void onError(RpcError rpcError) {
                                                        logger.error("应答呼入 操作错误: {}", callId, callConfMap.get(callId), rpcError.getMessage());
                                                    }

                                                    @Override
                                                    protected void onTimeout() {
                                                        logger.error("应答呼入 操作超时无响应", callId, callConfMap.get(callId));
                                                    }
                                                }
                                        );
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else if (fullMethodName.startsWith("sys.conf")) {
                            /// 会议事件
                            String methodName = fullMethodName.substring(9);
                            String confId = (String) rpcRequest.getParams().get("res_id");
                            if (methodName.equals("on_released")) {
                                logger.warn("会议 {} 已经释放", confId);

                                //修改为已结束状态
                                Conference conference = conferenceRepository.findByResId(confId);
                                conference.setStatus(2);
                                conferenceRepository.save(conference);
                            } else if (methodName.equals("on_record_completed")) {
                                logger.warn("会议 {} 录音已结束", confId);
                                String error = (String) rpcRequest.getParams().get("error");
                                if (error == null) {
                                    logger.warn("保存会议 {} 录音信息", confId);
                                    Recording recording = new Recording();
                                    Conference conference = conferenceRepository.findByResId(confId);
                                    if (conference != null) {
                                        recording.setConferenceId(conference.getId());
                                        recording.setConferenceNo(conference.getResId());
                                        String filename = (String) rpcRequest.getParams().get("record_file");
                                        int index = filename.lastIndexOf("/");
                                        if (index != -1) {
                                            recording.setFilename(filename.substring(index+1));
                                            recording.setFilePath(filename.substring(0, index));
                                        }

                                        Object start = rpcRequest.getParams().get("begin_time");
                                        Object end = rpcRequest.getParams().get("end_time");
                                        logger.info("beginTime {}", start);
                                        logger.info("endTime {}", end);
                                        logger.info("class of start : {}", start.getClass());
//                                        recording.setStartTime(new Date((Integer)start));
//                                        recording.setStartTime(new Date((Integer)end));
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
                            logger.info("呼叫 {} 加入会议 {} 操作完毕", callId, conferenceId);
                        }

                        @Override
                        protected void onError(RpcError rpcError) {
                            logger.error("呼叫 {} 加入会议 {} 操作错误: {}", callId, conferenceId, rpcError.getMessage());
                        }

                        @Override
                        protected void onTimeout() {
                            logger.error("呼叫 {} 加入会议 {} 操作超时无响应", callId, conferenceId);
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void callOut(List<Contact> contacts, String ip, RpcResultListener listener) throws IOException, InterruptedException {
        for (Contact te : contacts) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("to_uri", te.getPhone()+"@"+ip); /// 被叫号码的 SIP URI
            params.put("max_answer_seconds", Constants.MAX_ANSWER_SECONDS); /// 该呼叫最长通话允许时间
            String name = te.getName();
            logger.info("Name ={} ", name);
            String reencode = new String(name.getBytes("gbk"),"utf-8");
            logger.info("reEncode Name ={} ", reencode);
            params.put("user_data", te.getPhone()+"-"+ te.getPhone()); ///用户信息
            logger.info("呼叫参数： {}", params);
            commander.createResource(
                    busAddress,
                    "sys.call",
                    params,
                    listener
            );
        }
    }


    public static void createConference(Map<String, Object> params, RpcResultListener rpcResultListener) throws IOException {
        if (commander != null) {
            commander.createResource(
                    busAddress,
                    "sys.conf",
                    params,
                    rpcResultListener);
        } else {
            logger.info("commander客户端 未初始化");
        }
    }


    public static void stopConference(String confId, RpcResultListener listener) throws IOException {
        if (commander != null) {
            commander.operateResource(
                    busAddress,
                    confId,
                    "sys.conf",
                    null,
                    listener);
        } else {
            logger.info("commander客户端 未初始化");
        }
    }


    public static void changePartMode(String confId, String callId, int mode, RpcResultListener listener) throws IOException {
        if (commander != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("res_id", confId);
            params.put("call_res_id", callId);
            params.put("mode", mode);
            logger.info("修改与会者声音收放模式，confid={},callId={},mode={}", confId, callId, mode);
            commander.operateResource(
                    busAddress,
                    confId,
                    "sys.conf.set_part_voice_mode",
                    params,
                    listener);
        } else {
            logger.info("commander客户端 未初始化");
        }
    }

    public static void exitConferece(String confId, String callId, RpcResultListener listener) throws IOException {
        if (commander != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("res_id", confId);
            params.put("call_res_id", callId);
            logger.info("退出会议，confid={},callId={}", confId, callId);
            commander.operateResource(
                    busAddress,
                    callId,
                    "sys.call.conf_exit",
                    params,
                    listener);
        } else {
            logger.info("commander客户端 未初始化");
        }
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
            logger.info("commander客户端 未初始化");
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
            logger.info("commander客户端 未初始化");
        }
    }

    public static void getConfParts(String confResId, RpcResultListener listener) throws IOException {
        if (commander != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("res_id", confResId);
            commander.operateResource(
                    busAddress,
                    confResId,
                    "sys.conf.get_parts",
                    params,
                    listener);
        } else {
            logger.info("commander客户端 未初始化");
        }
    }

    public static void checkConf(String confResId, RpcResultListener listener) throws IOException {
        if (commander == null) {
            logger.info("commander客户端 未初始化");
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
            logger.info("commander客户端 未初始化");
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
}

package cn.snzo.ipsc;

import cn.snzo.common.Constants;
import cn.snzo.entity.Conference;
import cn.snzo.repository.ConferenceRepository;
import cn.snzo.vo.ConferenceStartShow;
import com.hesong.ipsc.ccf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by chentao on 2017/7/11 0011.
 */
@Service
public class ConferenceCreator {

    private static Logger logger = LoggerFactory.getLogger(ConferenceCreator.class);

    public static Commander  commander    = null;
    public static String     conferenceId = "";
    public static BusAddress busAddress   = null;

    @Autowired
    private ConferenceRepository conferenceRepository;

    public void init() {
        logger.info("Data Bus 客户端单元初始化");
        Unit.initiate(Constants.localId, new UnitCallbacks() {
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
    }

    public  Commander createCommander() throws InterruptedException {
        /// 新建一个命令发送者
        commander = Unit.createCommander(
                Constants.commanderId,
                Constants.ipscIpAddr,
                /// 事件监听器
                new RpcEventListener() {
                    public void onEvent(BusAddress busAddress, RpcRequest rpcRequest) {
                        String fullMethodName = rpcRequest.getMethod();
                        if (fullMethodName.startsWith("sys.call")) {
                            /// 呼叫事件
                            String methodName = fullMethodName.substring(9);
                            final String callId = (String) rpcRequest.getParams().get("res_id");
                            logger.info("sys.call rpcRequest里面的参数{}", rpcRequest.getParams());
                            if (methodName.equals("on_released")) {
                                logger.warn("呼叫 {} 已经释放", callId);
                            } else if (methodName.equals("on_ringing")) {
                                logger.info("呼叫 {} 振铃", callId);
                            } else if (methodName.equals("on_dial_completed")) {
                                String error = (String) rpcRequest.getParams().get("error");
                                if (error == null) {
                                    logger.info("呼叫 {} 拨号成功，操作呼叫资源，让它加入会议 {} ...", callId, conferenceId);
                                    addCallToConf(callId, conferenceId);
                                } else {
                                    logger.error("呼叫 {} 拨号失败：{}", callId, error);
                                }
                            } else if (methodName.equals("on_record_completed")) {
                                String error = (String) rpcRequest.getParams().get("error");
                                logger.info("录音停止callId: {} error: {}", callId, error);
                            }
                        } else if (fullMethodName.startsWith("sys.conf")) {
                            /// 会议事件
                            String methodName = fullMethodName.substring(9);
                            String confId = (String) rpcRequest.getParams().get("res_id");
                            if (methodName.equals("on_released")) {
                                logger.warn("会议 {} 已经释放", confId);
//                                if (confId.equals(conferenceId)) {
//                                    conferenceId = "";
//                                }
                            }
                        }
                    }
                });
        return commander;
    }


    private void addCallToConf(String callId, String conferenceId) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("res_id", callId);
            params.put("conf_res_id", conferenceId);
            params.put("max_seconds", 300);
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

    public void startConference(ConferenceStartShow conferenceStartShow) throws InterruptedException {
        try {

            logger.info("建立会议");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("max_seconds", 3600); /// 会议最长时间，这是必填参数
            if (conferenceStartShow.isRecordEnable()) {
                //todo 获取录音存放路径
                String recordPath = "/data/sftp/mysftp/record/20170709141801.wav";
                logger.info("录音文件存放路径：" + recordPath);
                params.put("record_file", recordPath); /// 会议录音存放路径
            }

            logger.info("busAddress {}", busAddress);
            logger.info("params {}", params);
            logger.info("conferenceId {}", conferenceId);
            commander.createResource(
                    busAddress,
                    "sys.conf",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {

                            Map<String, Object> result = (Map<String, Object>) o;
                            conferenceId = (String) result.get("res_id");
                            logger.info(">>>>>>会议资源建立成功：confId={}", conferenceId);
                            Conference conference = new Conference();
                            conference.setConductorId(conferenceStartShow.getConductorId());
                            conference.setRoomId(conferenceStartShow.getRoomId());
                            conference.setResId(conferenceId);
                            conference.setStartAt(new Date());
                            conference.setEndAt(new Date());
                            conference.setStatus(1);
                            conferenceRepository.save(conference);
                            logger.info("<<<<<<会议资源建立成功，ID={}", conferenceId);

                            //外呼
                            logger.info("进行外呼", conferenceId);
                            try {
                                callOut(commander, conferenceStartShow.getPhones(), Constants.VOIP);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
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
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void stopConference(String conferenceId) {

    }


    public void callOut(Commander commander, List<String> phones, String ip) throws IOException, InterruptedException {
        logger.info("呼叫 {}", phones);
        phones = phones.stream().map(e -> e+"@"+ip).collect(Collectors.toList());
        for (String te : phones) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("to_uri", te); /// 被叫号码的 SIP URI
            params.put("max_answer_seconds", 300); /// 该呼叫最长通话允许时间
            commander.createResource(
                    busAddress,
                    "sys.call",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            Map<String, Object> result = (Map<String, Object>) o;
                            String callId = (String) result.get("res_id");
                            logger.info("呼叫资源建立成功，ID={}。系统正在执行外呼……注意这不是呼叫成功！", callId);
                        }

                        @Override
                        protected void onError(RpcError rpcError) {
                            logger.error("创建呼叫资源错误：{} {}", rpcError.getCode(), rpcError.getMessage());
                        }

                        @Override
                        protected void onTimeout() {
                            logger.error("创建呼叫资源超时无响应");
                        }
                    }
            );
        }
    }
}

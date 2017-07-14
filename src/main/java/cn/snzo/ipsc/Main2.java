//package cn.snzo.ipsc;
//
//import com.hesong.ipsc.ccf.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.*;
//
///**
// * Created by tanbr on 2017/5/10.
// */
//public class Main2 {
//    private static final Logger logger = LoggerFactory.getLogger(Main2.class);
//
//    //    private static final String ipscIpAddr = "192.168.2.100"; /// IPSC 服务器的内网地址
//    private static final String ipscIpAddr  = "127.0.0.1"; /// IPSC 服务器的内网地址
//    private static final byte   localId     = 24;
//    private static final byte   commanderId = 10;
//
//    public static Commander  commander    = null;
//    public static String     conferenceId = "";
//    public static BusAddress busAddress   = null;
//
//
//    public static void init() {
//        logger.info("Data Bus 客户端单元初始化");
//        Unit.initiate(localId, new UnitCallbacks() {
//            public void connectSucceed(Client client) {
//                logger.info("成功的连接到了IPSC服务程序的 Data Bus");
//                busAddress = new BusAddress(commander.getConnectingUnitId(), (byte) 0);
//            }
//
//            public void connectFailed(Client client, int i) {
//                System.out.format("[{}] 连接失败", client.getId());
//            }
//
//            public void connectLost(Client client) {
//                System.out.format("[{}] 连接丢失", client.getId());
//            }
//
//            public void globalConnectStateChanged(byte b, byte b1, byte b2, byte b3, String s) {
//            }
//        });
//    }
//
//    public static Commander makeCommander() throws InterruptedException, IOException {
//        /// 新建一个命令发送者
//        return Unit.createCommander(
//                commanderId,
//                ipscIpAddr,
//                /// 事件监听器
//                new RpcEventListener() {
//                    public void onEvent(BusAddress busAddress, RpcRequest rpcRequest) {
//                        String fullMethodName = rpcRequest.getMethod();
//                        if (fullMethodName.startsWith("sys.call")) {
//                            /// 呼叫事件
//                            String methodName = fullMethodName.substring(9);
//                            final String callId = (String) rpcRequest.getParams().get("res_id");
//                            if (methodName.equals("on_released")) {
//                                logger.warn("呼叫 {} 已经释放", callId);
//                            } else if (methodName.equals("on_ringing")) {
//                                logger.info("呼叫 {} 振铃", callId);
//                            } else if (methodName.equals("on_dial_completed")) {
//                                String error = (String) rpcRequest.getParams().get("error");
//                                if (error == null) {
//                                    logger.info("呼叫 {} 拨号成功，操作呼叫资源，让它加入会议 {} ...", callId, conferenceId);
//                                    try {
//                                        Map<String, Object> params = new HashMap<String, Object>();
//                                        params.put("res_id", callId);
//                                        params.put("conf_res_id", conferenceId);
//                                        params.put("max_seconds", 300);
//                                        commander.operateResource(
//                                                busAddress,
//                                                callId,
//                                                "sys.call.conf_enter",
//                                                params,
//                                                new RpcResultListener() {
//                                                    @Override
//                                                    protected void onResult(Object o) {
//                                                        logger.info("呼叫 {} 加入会议 {} 操作完毕", callId, conferenceId);
//                                                    }
//
//                                                    @Override
//                                                    protected void onError(RpcError rpcError) {
//                                                        logger.error("呼叫 {} 加入会议 {} 操作错误: {}", callId, conferenceId, rpcError.getMessage());
//                                                    }
//
//                                                    @Override
//                                                    protected void onTimeout() {
//                                                        logger.error("呼叫 {} 加入会议 {} 操作超时无响应", callId, conferenceId);
//                                                    }
//                                                }
//                                        );
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                } else {
//                                    logger.error("呼叫 {} 拨号失败：{}", callId, error);
//                                }
//                            }
////                            else if (methodName.equals("on_incoming")) {
////                                logger.warn("呼入 {}", callId);
////                                try {
////                                    String error = (String) rpcRequest.getParams().get("error");
////                                    Map<String, Object> params = new HashMap<String, Object>();
////                                    params.put("max_answer_seconds", 10000);
////                                    params.put("res_id", callId);
////                                    if (error == null) {
////
////                                        commander.operateResource(
////                                                busAddress,
////                                                callId,
////                                                "sys.call.answer",
////                                                params,
////                                                new RpcResultListener() {
////                                                    @Override
////                                                    protected void onResult(Object o) {
////                                                        logger.info("应答呼入 {} 操作完毕", callId, conferenceId);
////                                                    }
////
////                                                    @Override
////                                                    protected void onError(RpcError rpcError) {
////                                                        logger.error("应答呼入 操作错误: {}", callId, conferenceId, rpcError.getMessage());
////                                                    }
////
////                                                    @Override
////                                                    protected void onTimeout() {
////                                                        logger.error("应答呼入 操作超时无响应", callId, conferenceId);
////                                                    }
////                                                }
////                                        );
////                                    }
////                                } catch (IOException e) {
////                                    e.printStackTrace();
////                                }
////
////                            }
//                            else if (methodName.equals("on_record_completed")) {
//                                String error = (String) rpcRequest.getParams().get("error");
//                                logger.info("录音停止callId: {} error: {}", callId, error);
//                            }
//                        } else if (fullMethodName.startsWith("sys.conf")) {
//                            /// 会议事件
//                            String methodName = fullMethodName.substring(9);
//                            String confId = (String) rpcRequest.getParams().get("res_id");
//                            if (methodName.equals("on_released")) {
//                                logger.warn("会议 {} 已经释放", confId);
////                                if (confId.equals(conferenceId)) {
////                                    conferenceId = "";
////                                }
//                            }
//                        }
//                    }
//                });
//    }
//    public static void excute() throws InterruptedException, IOException {
//
//        init();
//
//        commander = makeCommander();
//
//        /// 开始执行
//        String inputStr;
//        Scanner scanner = new Scanner(System.in);
//        System.out.printf("会议DEMO开始了! \n" +
//                "\t输入 \"conf\" 建立会议.\n" +
//                "\t输入 \"call + <空格> + <电话号码>\" 呼叫该号码并在呼通后加入会议\n"+
//                "\t输入 \"stop+ <空格> +<callId>\" 停止录音");
//        while (true) {
//            inputStr = scanner.nextLine().trim().toLowerCase();
//            if (inputStr.equals("conf")) {
//                if (!conferenceId.isEmpty()) {
//                    logger.warn("这个DEMO就写了一个会议，别新建多个！");
//                    continue;
//                }
//                logger.info("建立会议");
//                Map<String, Object> params = new HashMap<String, Object>();
//                params.put("max_seconds", 300); /// 会议最长时间，这是必填参数
//                String recordPath = "/data/sftp/mysftp/record/20170709141801.wav";
//                logger.info("录音文件存放路径：" + recordPath);
//                params.put("record_file", recordPath); /// 会议录音存放路径
//                commander.createResource(
//                        busAddress,
//                        "sys.conf",
//                        params,
//                        new RpcResultListener() {
//                            @Override
//                            protected void onResult(Object o) {
//                                Map<String, Object> result = (Map<String, Object>) o;
//                                conferenceId = (String) result.get("res_id");
//                                logger.info("会议资源建立成功，ID={}", conferenceId);
//                            }
//
//                            @Override
//                            protected void onError(RpcError rpcError) {
//                                logger.error("创建会议资源错误：{} {}", rpcError.getCode(), rpcError.getMessage());
//                            }
//
//                            @Override
//                            protected void onTimeout() {
//                                logger.error("创建会议资源超时无响应");
//                            }
//                        }
//                );
//            } else if (inputStr.startsWith("call")) {
//
//                String tel = inputStr.substring(4).trim();
//                List<String> tels = new ArrayList<String>();
//                Collections.addAll(tels, tel.split(","));
//                logger.info("呼叫 {}", tels);
//                for (String te : tels) {
//                    Map<String, Object> params = new HashMap<String, Object>();
//                    params.put("to_uri", te); /// 被叫号码的 SIP URI
//                    params.put("max_answer_seconds", 300); /// 该呼叫最长通话允许时间
//                    commander.createResource(
//                            busAddress,
//                            "sys.call",
//                            params,
//                            new RpcResultListener() {
//                                @Override
//                                protected void onResult(Object o) {
//                                    Map<String, Object> result = (Map<String, Object>) o;
//                                    String callId = (String) result.get("res_id");
//                                    logger.info("呼叫资源建立成功，ID={}。系统正在执行外呼……注意这不是呼叫成功！", callId);
//                                }
//
//                                @Override
//                                protected void onError(RpcError rpcError) {
//                                    logger.error("创建呼叫资源错误：{} {}", rpcError.getCode(), rpcError.getMessage());
//                                }
//
//                                @Override
//                                protected void onTimeout() {
//                                    logger.error("创建呼叫资源超时无响应");
//                                }
//                            }
//                    );
//                }
//
//            } else if (inputStr.startsWith("stop")) {
//                Map<String, Object> params = new HashMap<String, Object>();
//                final String callId = inputStr.substring(4).trim();
//                params.put("res_id", callId);
//                commander.operateResource(
//                        busAddress,
//                        callId,
//                        "sys.call.record_stop",
//                        params,
//                        new RpcResultListener() {
//
//                            @Override
//                            protected void onResult(Object result) {
//                                logger.info("停止录音成功 呼叫资源id= {}", callId);
//                            }
//
//                            @Override
//                            protected void onError(RpcError error) {
//                                logger.info("停止录音失败 呼叫资源id= {}", callId);
//                            }
//
//                            @Override
//                            protected void onTimeout() {
//                                logger.info("停止录音超时 呼叫资源id= {}", callId);
//                            }
//                        }
//                );
//            } else if (inputStr.startsWith("list")) {
//                Map<String, Object> params = new HashMap<String, Object>();
//                params.put("res_id", conferenceId);
//                commander.operateResource(
//                        busAddress,
//                        conferenceId,
//                        "sys.conf.get_parts",
//                        params,
//                        new RpcResultListener() {
//
//                            @Override
//                            protected void onResult(Object result) {
//                                logger.info("获取与会方列表成功 {}", result.toString());
//                            }
//
//                            @Override
//                            protected void onError(RpcError error) {
//                                logger.info("获取与会方列表失败 会议id={}", conferenceId);
//                            }
//
//                            @Override
//                            protected void onTimeout() {
//                                logger.info("获取与会方列表超时 会议id={} ", conferenceId);
//                            }
//                        }
//                );
//            } else if (inputStr.startsWith("cvm")) {
//                String[] strs = inputStr.trim().split(" ");
//                final String callId = strs[1];
//                final String mode = strs[2];
//                Map<String, Object> params = new HashMap<String, Object>();
//                params.put("res_id", conferenceId);
//                params.put("call_res_id", callId);
//                params.put("mode", Integer.valueOf(mode));
//                commander.operateResource(
//                        busAddress,
//                        conferenceId,
//                        "sys.conf.set_part_voice_mode",
//                        params,
//                        new RpcResultListener() {
//
//                            @Override
//                            protected void onResult(Object result) {
//                                logger.info("改变参会人收放模式成功 callId:{}, mode:{}", callId, mode);
//                            }
//
//                            @Override
//                            protected void onError(RpcError error) {
//                                logger.info("改变参会人收放模式失败 callId:{}, mode:{}", callId, mode);
//                            }
//
//                            @Override
//                            protected void onTimeout() {
//                                logger.info("改变参会人收放模式超时 callId:{}, mode:{} ", callId, mode);
//                            }
//                        }
//                );
//            } else if (inputStr.startsWith("conf-start-record")) {
//                Map<String, Object> params = new HashMap<String, Object>();
//                params.put("res_id", conferenceId);
//                params.put("max_seconds", 10000);
//                params.put("record_file", "/data/sftp/mysftp/record/20170709231.wav");
//                final int record_format = 18;
//                params.put("record_format", record_format);
//                commander.operateResource(
//                        busAddress,
//                        conferenceId,
//                        "sys.conf.record_start",
//                        params,
//                        new RpcResultListener() {
//
//                            @Override
//                            protected void onResult(Object result) {
//                                logger.info("会议开始录音成功 conferenceId:{}, record_format:{}", conferenceId, record_format);
//                            }
//
//                            @Override
//                            protected void onError(RpcError error) {
//                                logger.info("会议开始录音失败 conferenceId:{}, record_format:{}", conferenceId, record_format);
//                            }
//
//                            @Override
//                            protected void onTimeout() {
//                                logger.info("会议开始录音超时 conferenceId:{}, record_format:{} ", conferenceId, record_format);
//                            }
//                        }
//                );
//            } else if (inputStr.startsWith("conf-stop-record")) {
//                Map<String, Object> params = new HashMap<String, Object>();
//                params.put("res_id", conferenceId);
//                commander.operateResource(
//                        busAddress,
//                        conferenceId,
//                        "sys.conf.record_stop",
//                        params,
//                        new RpcResultListener() {
//
//                            @Override
//                            protected void onResult(Object result) {
//                                logger.info("会议停止录音成功 conferenceId:{}", conferenceId);
//                            }
//
//                            @Override
//                            protected void onError(RpcError error) {
//                                logger.info("会议停止录音失败 conferenceId:{}", conferenceId);
//                            }
//
//                            @Override
//                            protected void onTimeout() {
//                                logger.info("会议停止录音超时 conferenceId:{}", conferenceId);
//                            }
//                        }
//                );
//            }
//        }
//    }
//
//}

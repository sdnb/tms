package cn.snzo.service.impl;

import cn.snzo.entity.Conference;
import cn.snzo.ipsc.Main2;
import cn.snzo.repository.ConferenceRepository;
import cn.snzo.service.IpscService;
import cn.snzo.vo.ConferenceStartShow;
import com.hesong.ipsc.ccf.Commander;
import com.hesong.ipsc.ccf.RpcError;
import com.hesong.ipsc.ccf.RpcResultListener;
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
public class IpscServiceImpl implements IpscService {

    private Logger logger = LoggerFactory.getLogger(IpscServiceImpl.class);

    public static final String VOIP = "10.1.2.152";

    @Autowired
    private ConferenceRepository conferenceRepository;
    @Override
    public void startConference(ConferenceStartShow conferenceStartShow) {
        try {

            logger.info("建立会议");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("max_seconds", 300); /// 会议最长时间，这是必填参数
            if (conferenceStartShow.isRecordEnable()) {
                //todo 获取录音存放路径
                String recordPath = "/data/sftp/mysftp/record/20170709141801.wav";
                logger.info("录音文件存放路径：" + recordPath);
                params.put("record_file", recordPath); /// 会议录音存放路径
            }
            String conferenceId = "";
            Commander commander = Main2.makeCommander(conferenceId);
            commander.createResource(
                    Main2.busAddress,
                    "sys.conf",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            Map<String, Object> result = (Map<String, Object>) o;
                            String conferenceId = (String) result.get("res_id");
                            Conference conference = new Conference();
                            conference.setConductorId(conferenceStartShow.getConductorId());
                            conference.setRoomId(conferenceStartShow.getRoomId());
                            conference.setResId(conferenceId);
                            conference.setStartAt(new Date());
                            conference.setEndAt(new Date());
                            conference.setStatus(1);
                            conferenceRepository.save(conference);
                            logger.info("会议资源建立成功，ID={}", conferenceId);

                            //外呼
                            logger.info("进行外呼", conferenceId);
                            try {
                                callOut(conferenceId, conferenceStartShow.getPhones(), VOIP);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stopConference(String conferenceId) {

    }


    public void callOut(String conferenceId, List<String> phones, String ip) throws IOException, InterruptedException {
        logger.info("呼叫 {}", phones);
        phones = phones.stream().map(e -> e+"@"+ip).collect(Collectors.toList());
        for (String te : phones) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("to_uri", te); /// 被叫号码的 SIP URI
            params.put("max_answer_seconds", 300); /// 该呼叫最长通话允许时间
            Main2.makeCommander(conferenceId).createResource(
                    Main2.busAddress,
                    "sys.call",
                    params,
                    new RpcResultListener() {
                        @Override
                        protected void onResult(Object o) {
                            Map<String, Object> result = (Map<String, Object>) o;
                            String              callId = (String) result.get("res_id");
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

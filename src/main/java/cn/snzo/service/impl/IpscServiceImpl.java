package cn.snzo.service.impl;

import cn.snzo.entity.Conference;
import cn.snzo.repository.ConferenceRepository;
import cn.snzo.service.IpscService;
import cn.snzo.utils.IpscUtil;
import cn.snzo.vo.ConferenceStartShow;
import com.hesong.ipsc.ccf.RpcError;
import com.hesong.ipsc.ccf.RpcResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chentao on 2017/7/11 0011.
 */
@Service
public class IpscServiceImpl implements IpscService {

    private static Logger logger = LoggerFactory.getLogger(IpscServiceImpl.class);

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Override
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
            logger.info("创建会议资源参数 {}", params);
            IpscUtil.createConference(
                    params,
                    new ConferenceCreateRpcListener(conferenceStartShow)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

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
    public void stopConference(String conferenceId) {

    }


    private class ConferenceCreateRpcListener extends RpcResultListener{

        private ConferenceStartShow conferenceStartShow;

        public ConferenceCreateRpcListener(ConferenceStartShow conferenceStartShow) {
            this.conferenceStartShow = conferenceStartShow;
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

package cn.snzo.service;

import cn.snzo.entity.Conference;
import cn.snzo.vo.ConferencePart;
import cn.snzo.vo.ConferenceStartShow;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

/**
 * Created by chentao on 2017/7/11 0011.
 */
public interface IpscService {

//    void init();

//    void createCommander() throws InterruptedException;

    Conference startConference(ConferenceStartShow conferenceStartShow, String tokenName) throws InterruptedException, IOException;

    int stopConference(String confResId, String tokenName) throws IOException, InterruptedException;

    int addCallToConf(List<String> phones, String conferenceId, String tokenName) throws IOException, InterruptedException;

    int startRecord(String confResId, String tokenName) throws IOException, InterruptedException;

    int stopRecord(String confResId, String tokenName) throws IOException, InterruptedException;

    int removeCallFromConf(String callId, String confResId, String tokenName) throws IOException, InterruptedException;

    int changeCallMode(String callId, String confResId,  int mode, String tokenName) throws IOException, InterruptedException;


    Page<ConferencePart> getConfParts(String confResId, String username, String phone, Integer currentPage, Integer pageSize) throws IOException, InterruptedException;


    /**
     * 检查会议资源是否存在
     * @param confResId
     * @return
     */
    int checkConfExist(String confResId) throws InterruptedException, IOException;

    /**
     * 检查呼叫资源是否存在
     * @param callId
     * @return
     */
    int checkCallExist(String callId) throws InterruptedException, IOException ;
}

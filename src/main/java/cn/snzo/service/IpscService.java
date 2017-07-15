package cn.snzo.service;

import cn.snzo.vo.ConferencePart;
import cn.snzo.vo.ConferenceStartShow;

import java.io.IOException;
import java.util.List;

/**
 * Created by chentao on 2017/7/11 0011.
 */
public interface IpscService {

//    void init();

//    void createCommander() throws InterruptedException;

    int startConference(ConferenceStartShow conferenceStartShow, String tokenName) throws InterruptedException, IOException;

    int stopConference(String confResId, String tokenName) throws IOException;

    int addCallToConf(List<String> phones, String conferenceId, String tokenName) throws IOException, InterruptedException;

    int startRecord(String confResId, String tokenName) throws IOException;

    int stopRecord(String confResId, String tokenName) throws IOException;

    int removeCallFromConf(String callId, String confResId, String tokenName) throws IOException;

    int changeCallMode(String callId, String confResId,  int mode, String tokenName) throws IOException;


    List<ConferencePart> getConfParts(String confResId, String username) throws IOException;
}

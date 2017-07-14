package cn.snzo.service;

import cn.snzo.vo.ConferenceStartShow;

/**
 * Created by chentao on 2017/7/11 0011.
 */
public interface IpscService {

//    void init();

//    void createCommander() throws InterruptedException;

    void startConference(ConferenceStartShow conferenceStartShow) throws InterruptedException;

    void stopConference(String conferenceId);
}

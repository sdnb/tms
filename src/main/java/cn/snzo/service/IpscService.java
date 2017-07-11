package cn.snzo.service;

import cn.snzo.vo.ConferenceStartShow;

/**
 * Created by chentao on 2017/7/11 0011.
 */
public interface IpscService {
    void startConference(ConferenceStartShow conferenceStartShow);


    void stopConference(String conferenceId);



}

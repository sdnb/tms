package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import cn.snzo.ipsc.ConferenceCreator;
import cn.snzo.vo.ConferenceStartShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@RestController
@RequestMapping("/api")
public class ConferenceController extends BaseController {

    Logger logger = LoggerFactory.getLogger(ConferenceController.class);

    @Autowired
    private ConferenceCreator conferenceCreator;
    //todo
    @RequestMapping(value = "/conference/start", method = RequestMethod.POST)
    public ObjectResult add(@RequestBody ConferenceStartShow conferenceStartShow) {
        try {
            conferenceCreator.startConference(conferenceStartShow);
            return successRes("发起会议成功");
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("发起会议异常");
        }
    }


    //todo
    @RequestMapping(value = "/conference/end", method = RequestMethod.POST)
    public ObjectResult end() {
        return successRes(null);
    }
}

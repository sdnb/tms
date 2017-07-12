package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.impl.IpscServiceImpl;
import cn.snzo.vo.ConferenceStartShow;
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


    @Autowired
    private IpscServiceImpl ipscService;

    //todo
    @RequestMapping(value = "/conference/start", method = RequestMethod.POST)
    public ObjectResult add(@RequestBody ConferenceStartShow conferenceStartShow)
    {
        try {
            ipscService.startConference(conferenceStartShow);
        } catch (InterruptedException e) {
            return failureRes("发起会议失败");
        }
        return successRes("发起会议成功");
    }



    //todo
    @RequestMapping(value = "/conference/end", method = RequestMethod.POST)
    public ObjectResult end() {
        return successRes(null);
    }

//    @RequestMapping(value = "/conference/call"
//    public ObjectResult call() {
//
//    }
}

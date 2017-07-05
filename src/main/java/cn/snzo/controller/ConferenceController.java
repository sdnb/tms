package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@RestController
@RequestMapping("/api")
public class ConferenceController extends BaseController {


    //todo
    @RequestMapping(value = "/conference/start", method = RequestMethod.POST)
    public ObjectResult add() {
        return successRes(null);
    }



    //todo
    @RequestMapping(value = "/conference/end", method = RequestMethod.POST)
    public ObjectResult end() {
        return successRes(null);
    }

}

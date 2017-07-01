package cn.snzo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Controller
public class HomeController {


    @RequestMapping(value = {"/","/mge/**"},method = RequestMethod.GET)
    public String mgePage(){
        return "forward:/sz-gly.html";
    }
}

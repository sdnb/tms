package cn.snzo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ThomasC on 2017/6/29 0029.
 */
@RestController
public class TestController {


    @RequestMapping("/hwe")
    public String twe(){
        return "Hwe";
    }
}

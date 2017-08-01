package cn.snzo.controller;

import cn.snzo.common.Constants;
import cn.snzo.repository.CallRepository;
import cn.snzo.repository.ConductorRepository;
import cn.snzo.repository.ContactRepository;
import cn.snzo.service.IConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThomasC on 2017/6/29 0029.
 */
@RestController
public class TestController {

    @Autowired
    private IConductorService conductorService;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private CallRepository callRepository;

    @RequestMapping("/hwe")
    public Object twe(){
        List<Integer> status = new ArrayList<>();
        status.add(1);
        status.add(2);
        callRepository.findCallByConfResIdAndStatus(null, status);
        return Constants.VOICE_PATH;
    }

    @RequestMapping(value = {"/ws1"},method = RequestMethod.GET)
    public String ws() { return "forward:/ws.html"; }
}

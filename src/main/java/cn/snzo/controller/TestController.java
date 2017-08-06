package cn.snzo.controller;

import cn.snzo.common.Constants;
import cn.snzo.repository.CallRepository;
import cn.snzo.repository.ConductorRepository;
import cn.snzo.repository.ContactRepository;
import cn.snzo.service.IConductorService;
import cn.snzo.service.IpscService;
import cn.snzo.utils.IpscUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private IpscService ipscService;

    @RequestMapping("/hwe")
    public Object twe(@RequestParam(required = false)String confResId){
        IpscUtil.playConfVoice(confResId, Constants.COME_IN_TICK);
//        List<Integer> status = new ArrayList<>();
//        status.add(1);
//        status.add(2);
//        callRepository.findCallByConfResIdAndStatus(null, status);
        return Constants.VOICE_PATH;
    }

    @RequestMapping(value = {"/ws1"},method = RequestMethod.GET)
    public String ws() { return "forward:/ws.html"; }
}

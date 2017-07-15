package cn.snzo.controller;

import cn.snzo.entity.Conductor;
import cn.snzo.repository.ConductorRepository;
import cn.snzo.service.IConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @RequestMapping("/hwe")
    public String twe(){
        Conductor conductor = conductorRepository.findOne(1);
        return conductorService.getOne(1).toString();
    }
}

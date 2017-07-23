package cn.snzo.controller;

import cn.snzo.entity.Contact;
import cn.snzo.repository.ConductorRepository;
import cn.snzo.repository.ContactRepository;
import cn.snzo.service.IConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThomasC on 2017/6/29 0029.
 */
@Controller
public class TestController {

    @Autowired
    private IConductorService conductorService;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private ContactRepository contactRepository;

    @RequestMapping("/hwe")
    public Object twe(){
        List<String> phones = new ArrayList<>();
        phones.add("18627720789");
        phones.add("18627720788");
        List<Contact> c = contactRepository.findByPhones(phones);
        return c;
    }

    @RequestMapping(value = {"/ws1"},method = RequestMethod.GET)
    public String ws() { return "forward:/ws.html"; }
}

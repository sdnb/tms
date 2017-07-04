package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import cn.snzo.entity.PhoneBook;
import cn.snzo.service.IPhoneBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@RestController
@RequestMapping("/api")
public class PhoneBookController extends BaseController{


    @Autowired
    private IPhoneBookService phoneBookService;


    /**
     * 查询电话簿
     * @param type 类型
     * @param roomId 会议室id
     * @return
     */
    @RequestMapping(value = "/phonebook", method = RequestMethod.GET)
    public ObjectResult findAll(@RequestParam(name = "type", required = false)Integer type,
                                @RequestParam(name = "roomId", required = false)Integer roomId) {
        List<PhoneBook> phoneBooks = phoneBookService.findAllByKeys(type, roomId);
        return successRes(phoneBooks);
    }
}

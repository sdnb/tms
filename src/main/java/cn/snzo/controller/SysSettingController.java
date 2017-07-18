package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.ISysSettingService;
import cn.snzo.vo.AccountShow;
import cn.snzo.vo.SysSettingShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@RestController
@RequestMapping("/api")
public class SysSettingController extends BaseController{


    @Autowired
    private ISysSettingService sysSettingService;


    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public ObjectResult add(@RequestBody SysSettingShow sysSettingShow) {
        int ret = sysSettingService.add(sysSettingShow);
        return successRes("新增成功");
    }


    @RequestMapping(value = "/setting/{id}", method = RequestMethod.PUT)
    public ObjectResult update(@PathVariable("id")int id,
                               @RequestBody SysSettingShow sysSettingShow) {
        int ret = sysSettingService.modify(id, sysSettingShow);
        if (ret == 1) {
            return successRes("修改成功");
        } else {
            return failureRes("修改失败");
        }

    }


    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public ObjectResult get() {
        SysSettingShow sysSettingShow = sysSettingService.getLatestSetting();
        if (sysSettingShow == null) {
            return failureRes("未进行系统设置");
        }
        return successRes(sysSettingShow);
    }



    @RequestMapping(value = "/setting/pwd", method = RequestMethod.PUT)
    public ObjectResult updatePwd(@RequestBody AccountShow accountShow) {
        int ret = sysSettingService.modifyPwd(accountShow);
        if (ret == 1) {
            return successRes("修改成功");
        } else if (ret == 2){
            return failureRes("该用户不存在");
        } else if (ret == 3){
            return failureRes("新密码不能为空");
        } else if (ret == 4){
            return failureRes("修改异常");
        } else if (ret == 5){
            return failureRes("原密码错误");
        } else {
            return failureRes("修改失败");
        }
    }



}

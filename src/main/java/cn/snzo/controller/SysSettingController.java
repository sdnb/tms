package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.ISysSettingService;
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

}

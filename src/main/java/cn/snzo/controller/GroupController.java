package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.IGroupService;
import cn.snzo.vo.GroupShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@RestController
@RequestMapping("/api")
public class GroupController extends BaseController{



    private final Logger logger = LoggerFactory.getLogger(ConductorController.class);

    @Autowired
    private IGroupService groupService;

    /**
     * 新建分组
     * @param groupShow
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public ObjectResult add(@Validated @RequestBody
                                GroupShow groupShow,
                            BindingResult bindingResult) {
        try {
            ObjectResult result = argCheck(bindingResult);
            if (result != null) {
                return result;
            }
            int ret = groupService.add(groupShow);
            if (ret == 1) {
                return successRes("新增成功");
            } else if (ret == 2){
                return failureRes("");
            } else {
                return failureRes("新增失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes(e.getMessage());
        }

    }
}

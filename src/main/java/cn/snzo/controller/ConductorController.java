package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.utils.CommonUtils;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.IConductorService;
import cn.snzo.vo.ConductorShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 * Created by Administrator on 2017/6/29 0029.
 */
@RestController
@RequestMapping("/api")
public class ConductorController extends BaseController{

    @Autowired
    private IConductorService conductorService;


    private final Logger logger = LoggerFactory.getLogger(ConductorController.class);
    /**
     * 新增会议主持人
     * @param conductorShow
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/conductor", method = RequestMethod.POST)
    public ObjectResult add(@Validated @RequestBody
                                ConductorShow conductorShow,
                            BindingResult bindingResult) {
        try {
            ObjectResult result = argCheck(bindingResult);
            if (result != null) {
                return result;
            }
            int ret = conductorService.add(conductorShow);
            if (ret == 1) {
                return successRes("新增成功");
            } else if (ret == 2){
                return failureRes("账号信息不能为空");
            } else if (ret == 3){
                return failureRes("手机号重复");
            } else {
                return failureRes("新增失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes(e.getMessage());
        }

    }


/*
    @RequestMapping(value = "/conductor/{id}", method = RequestMethod.PUT)
    public ObjectResult modify(@PathVariable int id,@Validated @RequestBody ConductorShow conductorShow,
                               BindingResult bindingResult) {
        ObjectResult result = argCheck(bindingResult);
        if (result != null) {
            return result;
        }
        int ret = conductorService.modify(id, conductorShow);
        if (ret == 1) {
            return successRes("修改成功");
        } else {
            return failureRes("修改失败");
        }
    }
*/

    /**
     * 分页查询主持人
     * @param name 真实姓名
     * @param phone 电话
     * @param currentPage
     * @param pageSize
     * @param response
     * @return
     */
    @RequestMapping(value = "/conductor/page", method = RequestMethod.GET)
    public ObjectResult getPage(@RequestParam(value = "name", required = false)String name,
                                @RequestParam(value = "phone", required = false)String phone,
                                @RequestParam(value = "currentPage", required = false)Integer currentPage,
                                @RequestParam(value = "pageSize", required = false)Integer pageSize,
                                HttpServletResponse response) {
        Page<ConductorShow> page = conductorService.findPage(name, phone, pageSize, currentPage);
        CommonUtils.setResponseHeaders(page.getTotalElements(), page.getTotalPages(), page.getNumber(), response);
        return new ObjectResult("true", page.getContent());
    }


    /**
     * 删除主持人
     * @param id
     * @return
     */
    @RequestMapping(value = "/conductor/{id}", method = RequestMethod.DELETE)
    public ObjectResult delete(@PathVariable int id) {
        try {
            int ret = conductorService.delete(id);
            if (ret == 1) {
                return successRes("删除成功");
            } else if (ret == 2) {
                return failureRes("该主持人正在会议中无法删除");
            } else if (ret == 3) {
                return failureRes("该主持人不存在");
            } else {
                return failureRes("删除失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("服务异常");
        }

    }

}

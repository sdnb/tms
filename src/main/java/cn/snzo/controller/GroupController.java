package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.CommonUtils;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.IGroupService;
import cn.snzo.vo.ContactGroupRelativeShow;
import cn.snzo.vo.GroupShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@RestController
@RequestMapping("/api")
public class GroupController extends BaseController{



    private final Logger logger = LoggerFactory.getLogger(GroupController.class);

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
                return failureRes("组名重复");
            } else {
                return failureRes("新增失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes(e.getMessage());
        }

    }


    /**
     * 分页查分组
     * @param name
     * @param currentPage
     * @param pageSize
     * @param response
     * @return
     */
    @RequestMapping(value = "/group/page", method = RequestMethod.GET)
    public ObjectResult getGroups( @RequestParam(value = "name", required = false)String name,
                                   @RequestParam(value = "currentPage", required = false)Integer currentPage,
                                   @RequestParam(value = "pageSize", required = false)Integer pageSize,
                                  HttpServletResponse response) {
        Page<GroupShow> page = groupService.getPage(name, pageSize, currentPage);
        CommonUtils.setResponseHeaders(page.getTotalElements(), page.getTotalPages(), page.getNumber(), response);
        return new ObjectResult("true", page.getContent());
    }


    /**
     * 删除分组
     * @param id
     * @return
     */
    @RequestMapping(value = "/group/{id}", method = RequestMethod.DELETE)
    public ObjectResult delete(@PathVariable(value = "id")int id) {
        try {
            int ret = groupService.delete(id);

            if (ret == 1) {
                return successRes("删除成功");
            } else {
                return failureRes("删除失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("服务异常");
        }
    }


    /**
     * 添加组员
     * @return
     */
    @RequestMapping(value = "/group/contact", method = RequestMethod.POST)
    public ObjectResult addContact(@Validated @RequestBody ContactGroupRelativeShow groupRelativeShow,
                                   BindingResult bindingResult) {
        try {
            ObjectResult checkResult = argCheck(bindingResult);
            if (checkResult != null) {
                return failureRes(checkResult);
            }
            int ret = groupService.addContact(groupRelativeShow);
            if (ret == 1) {
                return successRes("添加成功");
            } else {
                return failureRes("添加失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("添加异常");
        }
    }


    /**
     *
     * 删除组员
     * @return
     */
    @RequestMapping(value = "/group/{gid}/contact/{cid}", method = RequestMethod.DELETE)
    public ObjectResult removeContact(@PathVariable("gid") int gid,
                                      @PathVariable("cid") int cid) {
        try {
            int ret = groupService.removeContact(gid, cid);
            if (ret > 0) {
                return successRes("删除成功");
            } else {
                return failureRes("无该组员");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("删除异常");
        }
    }
}

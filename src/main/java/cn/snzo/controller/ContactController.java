package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import cn.snzo.exception.ServiceException;
import cn.snzo.service.IContactService;
import cn.snzo.service.ITokenService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.vo.ContactShow;
import cn.snzo.vo.LoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@RestController
@RequestMapping("/api")
public class ContactController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private IContactService contactService;

    @Autowired
    private ITokenService<LoginInfo> tokenService;



    /**
     * 新增联系人
     * @param contactShow
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public ObjectResult add(@Validated @RequestBody
                                ContactShow contactShow,
                            BindingResult bindingResult) {
        try {
            ObjectResult result = argCheck(bindingResult);
            if (result != null) {
                return result;
            }
            int ret = contactService.add(contactShow);
            if (ret == 1) {
                return successRes("新增成功");
            } else if (ret == 2) {
                return failureRes("电话已存在");
            } else {
                return failureRes("新增失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes(e.getMessage());
        }
    }



    /**
     * 导入联系人
     * @return
     */
    @RequestMapping(value = "/contact/excel/{bookId}", method = RequestMethod.POST)
    public ObjectResult add(MultipartFile file, @PathVariable(value = "bookId")int bookId) {
        try {

            int ret = contactService.importFromExcel(file, bookId);
            if (ret == 1) {
                return successRes("导入成功");
            } else {
                return failureRes("导入失败");
            }
        } catch (ServiceException e) {
            logErrInfo(e, logger);
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("导入异常");
        }
    }


    /**
     * 分页查联系人
     * @param name
     * @param phone
     * @param bookId
     * @param addSysBookId
     * @param bookType
     * @param groupId
     * @param currentPage
     * @param pageSize
     * @param response
     * @return
     */
    @RequestMapping(value = "/contact/page", method = RequestMethod.GET)
    public ObjectResult getContacts( @RequestParam(value = "name", required = false)String name,
                                     @RequestParam(value = "phone", required = false)String phone,
                                     @RequestParam(value = "bookId", required = false)Integer bookId,
                                     @RequestParam(value = "sysBookId", required = false)Boolean addSysBookId,
                                     @RequestParam(value = "bookType", required = false)Integer bookType,
                                     @RequestParam(value = "groupId", required = false)Integer groupId,
                                     @RequestParam(value = "conductorId", required = false)Integer conductorId,
                                     @RequestParam(value = "currentPage", required = false)Integer currentPage,
                                     @RequestParam(value = "pageSize", required = false)Integer pageSize,
                                     HttpServletResponse response) {
        Page<ContactShow> page = contactService.getPage( groupId, bookId, name, phone, bookType, addSysBookId, conductorId, currentPage, pageSize);
        CommonUtils.setResponseHeaders(page.getTotalElements(), page.getTotalPages(), page.getNumber(), response);
        return new ObjectResult("true", page.getContent());
    }


    /**
     * 查询所有待入会的联系人
     * @param currentPage
     * @param pageSize
     * @param response
     * @return
     */
    @RequestMapping(value = "/contact/conf", method = RequestMethod.GET)
    public ObjectResult getContacts(@RequestParam(value = "conductorId", required = true)Integer conductorId,
                                    @RequestParam(value = "currentPage", required = false)Integer currentPage,
                                    @RequestParam(value = "pageSize", required = false)Integer pageSize,
                                    HttpServletResponse response) {
        try {
            Page<ContactShow> page = contactService.findContactByCurrUser(conductorId, currentPage, pageSize);
            CommonUtils.setResponseHeaders(page.getTotalElements(), page.getTotalPages(), page.getNumber(), response);
            return new ObjectResult("true", page.getContent());
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("获取失败");
        }

    }

    /**
     * 新增联系人
     * @param contactShow
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/contact/{cid}", method = RequestMethod.PUT)
    public ObjectResult modify(@PathVariable(value = "cid")int cid,
                               @Validated @RequestBody ContactShow contactShow,
                            BindingResult bindingResult) {
        try {
            ObjectResult result = argCheck(bindingResult);
            if (result != null) {
                return result;
            }
            int ret = contactService.modify(cid, contactShow);
            if (ret == 1) {
                return successRes("修改成功");
            } else if (ret == 2) {
                return failureRes("联系人不存在");
            } else if (ret == 3) {
                return failureRes("电话已存在");
            } else {
                return failureRes("修改失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes(e.getMessage());
        }
    }


    /**
     * 删除联系人
     * @param cid
     * @return
     */
    @RequestMapping(value = "/contact/{cid}", method = RequestMethod.DELETE)
    public ObjectResult delete(@PathVariable(value = "cid") int cid) {
        try {
            int ret = contactService.delete(cid);

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



}

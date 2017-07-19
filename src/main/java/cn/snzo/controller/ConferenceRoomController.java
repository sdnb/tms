package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.IConferenceRoomService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.vo.ConferenceRoomShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@RestController
@RequestMapping("/api")
public class ConferenceRoomController extends BaseController{

    @Autowired
    private IConferenceRoomService conferenceRoomService;

    private final Logger logger = LoggerFactory.getLogger(ConferenceRoomController.class);

    @RequestMapping(value = "/conferenceRoom", method = RequestMethod.POST)
    public ObjectResult add(@Validated @RequestBody
                                ConferenceRoomShow conferenceRoomShow,
                            BindingResult bindingResult) {
        try {
            ObjectResult result = argCheck(bindingResult);
            if (result != null) {
                return result;
            }
            int ret = conferenceRoomService.add(conferenceRoomShow);
            if (ret == 1) {
                return successRes("新增成功");
            } else if (ret == 2) {
                return failureRes("ivr密码重复");
            } else if (ret == 3) {
                return failureRes("该主持人已经和别的会议室绑定");
            } else {
                return failureRes("新增失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("服务异常");
        }
    }



    /**
     * 分页查会议室
     * @param name 主持人姓名
     * @param ivr ivr密码
     * @param number 会议室编号
     * @param currentPage
     * @param pageSize
     * @param response
     * @return
     */
    @RequestMapping(value = "/conferenceRoom/page", method = RequestMethod.GET)
    public ObjectResult getPage(@RequestParam(value = "name", required = false)String name,
                                @RequestParam(value = "ivr", required = false)String ivr,
                                @RequestParam(value = "number", required = false)String number,
                                @RequestParam(value = "currentPage", required = false)Integer currentPage,
                                @RequestParam(value = "pageSize", required = false)Integer pageSize,
                                HttpServletResponse response) {
        Page<ConferenceRoomShow> page = conferenceRoomService.findPage(ivr, number, name, currentPage, pageSize);
        CommonUtils.setResponseHeaders(page.getTotalElements(), page.getTotalPages(), page.getNumber(), response);
        response.addHeader("Page", String.valueOf(page.getNumber()+1));
        response.addHeader("Page-Count", String.valueOf(page.getTotalPages()));
        return new ObjectResult("true", page.getContent());
    }

    /**
     * 根据主持人查会议室
     * @param conductorId 主持人Id
     * @param response
     * @return
     */
    @RequestMapping(value = "/conferenceRoom/conductor",method = RequestMethod.GET)
    public ObjectResult getRoomByConductor(@RequestParam(value = "conductorId", required = true)Integer conductorId,
                                HttpServletResponse response) {
        ConferenceRoomShow conferenceRoomShow = conferenceRoomService.getRoomByConductor(conductorId);
        if (conferenceRoomShow != null) {
            List<ConferenceRoomShow> conferenceRoomShows = new ArrayList<>();
            conferenceRoomShows.add(conferenceRoomShow);
            return successRes(conferenceRoomShows);
        } else {
            return failureRes("该主持人未绑定会议室");
        }
    }

    /**
     * 修改会议室
     * @param id
     * @param conferenceRoomShow
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/conferenceRoom/{id}", method = RequestMethod.PUT)
    public ObjectResult modify(@PathVariable int id,@Validated @RequestBody ConferenceRoomShow conferenceRoomShow,
                               BindingResult bindingResult) {
        try {
            ObjectResult result = argCheck(bindingResult);
            if (result != null) {
                return result;
            }
            int ret = conferenceRoomService.modify(id, conferenceRoomShow);
            if (ret == 1) {
                return successRes("修改成功");
            } else if (ret == 2){
                return failureRes("该会议室不存在");
            } else if (ret == 3) {
                return failureRes("会议室正在使用，请稍后重试");
            } else if (ret == 4) {
                return failureRes("ivr密码重复");
            } else if (ret == 5) {
                return failureRes("该主持人已经和别的会议室绑定");
            } else {
                return failureRes("修改失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("服务异常");
        }

    }



    /**
     * 删除会议室
     * @param id
     * @return
     */
    @RequestMapping(value = "/conferenceRoom/{id}", method = RequestMethod.DELETE)
    public ObjectResult delete(@PathVariable int id) {
        try {
            int ret = conferenceRoomService.delete(id);
            if (ret == 1) {
                return successRes("删除成功");
            } else if (ret == 2) {
                return failureRes("会议室不存在");
            } else if (ret == 3) {
                return failureRes("会议室正在使用无法删除");
            } else {
                return failureRes("删除失败");
            }
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("服务异常");
        }

    }


    /**
     * 查会议室所有联系人（包括系统电话簿中的联系人） todo
     * @param id
     * @return
     */
    @RequestMapping(value = "/conferenceRoom/{id}/contact", method = RequestMethod.GET)
    public ObjectResult getAllContactOfRoom(@PathVariable("id")int id) {
        return successRes(null);
    }





}

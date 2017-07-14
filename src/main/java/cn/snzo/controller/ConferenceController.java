package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.Constants;
import cn.snzo.common.ObjectResult;
import cn.snzo.exception.ServiceException;
import cn.snzo.service.ITokenService;
import cn.snzo.service.impl.IpscServiceImpl;
import cn.snzo.vo.AddCallShow;
import cn.snzo.vo.ConferenceStartShow;
import cn.snzo.vo.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@RestController
@RequestMapping("/api")
public class ConferenceController extends BaseController {


    @Autowired
    private IpscServiceImpl ipscService;

    @Autowired
    private ITokenService<LoginInfo> tokenService;
    //todo
    @RequestMapping(value = "/conference/start", method = RequestMethod.POST)
    public ObjectResult add(@RequestBody ConferenceStartShow conferenceStartShow,
                            @CookieValue(value = Constants.STAFF_TOKEN, required = false)String token)

    {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            ipscService.startConference(conferenceStartShow, username);
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("发起会议异常");
        }
        return successRes("发起会议成功");
    }



    /**
        结束会议
     */
    @RequestMapping(value = "/conference/end", method = RequestMethod.PUT)
    public ObjectResult end(@RequestParam(value = "confId", required = true) String confId,
                            @CookieValue(value = Constants.STAFF_TOKEN, required = false)String token) {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            ipscService.stopConference(confId, username);
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("结束会议异常");
        }
        return successRes("结束成功");
    }


    /**
      添加呼叫到会议
     */
    @RequestMapping(value = "/conference/phones", method = RequestMethod.POST)
    public ObjectResult addCall(@RequestBody AddCallShow addCallShow,
                            @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token) {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            ipscService.addCallToConf(addCallShow.getPhones(), addCallShow.getConfResId(), username);
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("结束会议异常");
        }
        return successRes("结束成功");
    }



    /**
     * 改变与会者的声音收放模式
     * @param confResId 会议资源id
     * @param callId 呼叫资源id
     * @param mode 收放模式  1 放音+收音 2 收音 3 放音  4 无
     * @param token
     * @return
     */
    @RequestMapping(value = "/conference/call/changeMode", method = RequestMethod.PUT)
    public ObjectResult forbid(@RequestParam(value = "confResId", required = true) String confResId,
                               @RequestParam(value = "callId", required = true) String callId,
                               @RequestParam(value = "mode", required = true) int mode,
                                @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token) {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            ipscService.changeCallMode(callId, confResId, mode, username);
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("结束会议异常");
        }
        return successRes("结束成功");
    }


    /**
     * 退出会议
     * @param confResId 会议资源id
     * @param callId 呼叫资源id
     * @param token
     * @return
     */
    @RequestMapping(value = "/conference/call/exit", method = RequestMethod.PUT)
    public ObjectResult exitConf(@RequestParam(value = "confResId", required = true) String confResId,
                               @RequestParam(value = "callId", required = true) String callId,
                               @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token) {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            ipscService.removeCallFromConf(callId, confResId, username);
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("结束会议异常");
        }
        return successRes("结束成功");
    }


    /**
     * 开始录音
     * @param confResId 会议资源id
     * @param token
     * @return
     */
    @RequestMapping(value = "/conference/record/start", method = RequestMethod.PUT)
    public ObjectResult startRecord(@RequestParam(value = "confResId", required = true) String confResId,
                                    @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token) {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            ipscService.startRecord(confResId, username);
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("结束会议异常");
        }
        return successRes("结束成功");
    }

    /**
     * 停止录音
     * @param confResId 会议资源id
     * @param token
     * @return
     */
    @RequestMapping(value = "/conference/record/stop", method = RequestMethod.PUT)
    public ObjectResult stopReord(@RequestParam(value = "confResId", required = true) String confResId,
                                 @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token) {

        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            ipscService.stopRecord(confResId, username);
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("结束会议异常");
        }
        return successRes("结束成功");
    }

}

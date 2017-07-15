package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.Constants;
import cn.snzo.common.ObjectResult;
import cn.snzo.exception.ServiceException;
import cn.snzo.service.ITokenService;
import cn.snzo.service.impl.IpscServiceImpl;
import cn.snzo.vo.AddCallShow;
import cn.snzo.vo.ConferencePart;
import cn.snzo.vo.ConferenceStartShow;
import cn.snzo.vo.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 发起会议
     * @param conferenceStartShow
     * @param token
     * @return
     */
    @RequestMapping(value = "/conference/start", method = RequestMethod.POST)
    public ObjectResult add(@RequestBody ConferenceStartShow conferenceStartShow,
                            @CookieValue(value = Constants.STAFF_TOKEN, required = false)String token)

    {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            int code = ipscService.startConference(conferenceStartShow, username);
            if (code == 0) {
                return successRes("会议发起成功");
            } else if (code == 1) {
                return failureRes("会议发起错误");
            } else if (code == 2) {
                return failureRes("会议发起超时");
            } else if (code == 3){
                return failureRes("会议室不存在");
            } else if (code == 4){
                return failureRes("会议室在使用中");
            } else {
                return failureRes("会议发起失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("发起会议异常");
        }
    }



    /**
     * 结束会议
     * @param confId 会议资源id
     * @param token
     * @return
     */
    @RequestMapping(value = "/conference/end", method = RequestMethod.PUT)
    public ObjectResult end(@RequestParam(value = "confId", required = true) String confId,
                            @CookieValue(value = Constants.STAFF_TOKEN, required = false)String token) {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            int code = ipscService.stopConference(confId, username);
            if (code == 0) {
                return successRes("结束会议成功");
            } else if (code == 1) {
                return failureRes("结束会议错误");
            } else if (code == 2) {
                return failureRes("结束会议超时");
            } else {
                return failureRes("结束会议失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("结束会议异常");
        }
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
            int code = ipscService.addCallToConf(addCallShow.getPhones(), addCallShow.getConfResId(), username);

            if (code == 0) {
                return successRes("添加呼叫成功");
            } else if (code == 1) {
                return failureRes("添加呼叫错误");
            } else if (code == 2) {
                return failureRes("添加呼叫超时");
            } else {
                return failureRes("添加呼叫失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("添加呼叫异常");
        }
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
            int code = ipscService.changeCallMode(callId, confResId, mode, username);
            if (code == 0) {
                return successRes("改变成功");
            } else if (code == 1) {
                return failureRes("改变错误");
            } else if (code == 2) {
                return failureRes("改变超时");
            } else {
                return failureRes("改变失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("结改变异常");
        }
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
            int code = ipscService.removeCallFromConf(callId, confResId, username);
            if (code == 0) {
                return successRes("退出成功");
            } else if (code == 1) {
                return failureRes("退出错误");
            } else if (code == 2) {
                return failureRes("退出超时");
            } else {
                return failureRes("退出失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("退出异常");
        }
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
            int code = ipscService.startRecord(confResId, username);
            if (code == 0) {
                return successRes("开始录音");
            } else if (code == 1) {
                return failureRes("开始录音错误");
            } else if (code == 2) {
                return failureRes("开始录音超时");
            } else if (code == 3) {
                return failureRes("请设置录音文件存储地址");
            } else {
                return failureRes("开始录音失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("开始录音异常");
        }
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
           int code = ipscService.stopRecord(confResId, username);
            if (code == 0) {
                return successRes("停止录音");
            } else if (code == 1) {
                return failureRes("停止录音错误");
            } else if (code == 2) {
                return failureRes("停止录音超时");
            } else {
                return failureRes("停止录音失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("停止录音异常");
        }
    }


    /**
     * 得到与会人列表
     * @param confResId 会议资源id
     * @param token
     * @return
     */
    @RequestMapping(value = "/conference/parts", method = RequestMethod.GET)
    public ObjectResult getParts(@RequestParam(value = "confResId", required = true) String confResId,
                                  @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token) {

        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            List<ConferencePart> parts = ipscService.getConfParts(confResId, username);
            return successRes(parts);
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            return failureRes("获取与会者异常");
        }
    }

}

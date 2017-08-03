package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.Constants;
import cn.snzo.common.ObjectResult;
import cn.snzo.entity.Call;
import cn.snzo.entity.Conference;
import cn.snzo.exception.ServiceException;
import cn.snzo.service.ICallService;
import cn.snzo.service.IConferenceService;
import cn.snzo.service.ITokenService;
import cn.snzo.service.impl.IpscServiceImpl;
import cn.snzo.utils.CommonUtils;
import cn.snzo.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@RestController
@RequestMapping("/api")
public class ConferenceController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ConferenceController.class);
    @Autowired
    private IpscServiceImpl ipscService;

    @Autowired
    private ITokenService<LoginInfo> tokenService;

    @Autowired
    private IConferenceService conferenceService;

    @Autowired
    private ICallService callService;

    /**
     * 发起会议
     * @param conferenceStartShow
     * @param token
     * @return
     */
    @RequestMapping(value = "/conference/start", method = RequestMethod.POST)
    public ObjectResult add(@RequestBody ConferenceStartShow conferenceStartShow,
                            @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token)

    {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            Conference conference = ipscService.startConference(conferenceStartShow, username);
            if (conference != null) {
                return successRes(conference);
            } else {
                return failureRes("创建会议失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
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
    public ObjectResult end(@RequestParam(value = "confId", required = true) Integer confId,
                            @CookieValue(value = Constants.STAFF_TOKEN, required = false)String token) {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            int code = ipscService.stopConference(confId, username);
            if (code == 1) {
                return successRes("结束会议成功");
            } else if (code == 2) {
                return failureRes("结束会议错误");
            } else if (code == 3) {
                return failureRes("结束会议超时");
            } else if (code == 4) {
                return failureRes("会议不存在");
            } else {
                return failureRes("结束会议失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
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
            logger.info("所有参会人电话：{}", addCallShow.getPhones());
            if (code == 1) {
                return successRes("添加呼叫成功");
            } else if (code == 2) {
                return failureRes("添加呼叫错误");
            } else if (code == 3) {
                return failureRes("添加呼叫超时");
            } else if (code == 4) {
                return failureRes("会议资源不存在");
            } else {
                return failureRes("添加呼叫失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
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
            if (code == 1) {
                return successRes("改变成功");
            } else if (code == 2) {
                return failureRes("改变错误");
            } else if (code == 3) {
                return failureRes("改变超时");
            } else if (code == 4) {
                return failureRes("会议不存在");
            } else if (code == 5) {
                return failureRes("呼叫不存在");
            } else {
                return failureRes("改变失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
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
            if (code == 1) {
                return successRes("退出成功");
            } else if (code == 2) {
                return failureRes("退出错误");
            } else if (code == 3) {
                return failureRes("退出超时");
            } else if (code == 4) {
                return failureRes("会议不存在");
            } else if (code == 5) {
                return failureRes("呼叫不存在");
            } else {
                return failureRes("退出失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
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
            if (code == 1) {
                return successRes("开始录音");
            } else if (code == 2) {
                return failureRes("开始录音错误");
            } else if (code == 3) {
                return failureRes("开始录音超时");
            } else if (code == 4) {
                return failureRes("请设置录音文件存储地址");
            } else if (code == 5) {
                return failureRes("会议不存在");
            } else if (code == 6) {
                return failureRes("会议已是录音状态");
            } else {
                return failureRes("开始录音失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
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
            if (code == 1) {
                return successRes("停止录音");
            } else if (code == 2) {
                return failureRes("停止录音错误");
            } else if (code == 3) {
                return failureRes("停止录音超时");
            } else if (code == 4) {
                return failureRes("会议不存在");
            } else {
                return failureRes("停止录音失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("停止录音异常");
        }
    }


//    /**
//     * 得到与会人列表
//     * @param confResId 会议资源id
//     * @param token
//     * @return
//     */
//    @RequestMapping(value = "/conference/parts", method = RequestMethod.GET)
//    public ObjectResult getParts(@RequestParam(value = "confResId", required = true) String confResId,
//                                 @RequestParam(value = "phone", required = false) String phone,
//                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
//                                 @RequestParam(value = "pageSize", required = false) Integer pageSize,
//                                  @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token,
//                                 HttpServletResponse response) {
//        try {
//            LoginInfo loginInfo = tokenService.loadToken(token);
//            String username = loginInfo == null ? "" : loginInfo.getUsername();
//            Page<ConferencePart> parts = ipscService.getConfParts(confResId, username, phone, currentPage, pageSize);
//            CommonUtils.setResponseHeaders(parts.getTotalElements(), parts.getTotalPages(), parts.getNumber(), response);
//            return successRes(parts.getContent());
//        } catch (ServiceException e) {
//            return failureRes(e.getMessage());
//        } catch (Exception e) {
//            logErrInfo(e, logger);
//            return failureRes("获取与会者异常");
//        }
//    }



    /**
     * 查询会议
     * @param
     * @return
     */
    @RequestMapping(value = "/conference/page", method = RequestMethod.GET)
    public ObjectResult getConferencePage(@RequestParam(value = "id", required = false) Integer id,
                                          @RequestParam(value = "roomId", required = false) Integer roomId,
                                          @RequestParam(value = "status", required = false) Integer status,
                                          @RequestParam(value = "conductorId", required = false) Integer conductorId,
                                          @RequestParam(value = "confResId", required = false) String confResId,
                                          @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                          HttpServletResponse response) {

        try {
            Page<ConferenceShow> parts = conferenceService.findPage(id, roomId, status, conductorId, confResId, currentPage, pageSize);
            CommonUtils.setResponseHeaders(parts.getTotalElements(),  parts.getTotalPages(), parts.getNumber(), response);
            return successRes(parts.getContent());
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("查询会议异常");
        }
    }



    /**
     * 查询参会人
     * @param
     * @return
     */
    @RequestMapping(value = "/conference/parts", method = RequestMethod.GET)
    public ObjectResult getCallPage(@RequestParam("confResId")String confResId,
                                          @RequestParam(value = "roomId", required = false)Integer roomId,
                                          @RequestParam(value = "phone", required = false)String phone,
                                          @RequestParam(value = "status", required = false) Integer status,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                          HttpServletResponse response) {
        try {
            Page<Call> calls = callService.findPage(confResId, roomId, phone, status, name, currentPage, pageSize);
            CommonUtils.setResponseHeaders(calls.getTotalElements(),  calls.getTotalPages(), calls.getNumber(), response);
            //得到在线人数
            int onlineCount = callService.getCountOnline(confResId);
            response.addHeader("online_count", onlineCount+"");
            return successRes(calls.getContent());
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("查询参会人异常");
        }
    }



    /**
      添加临时呼叫到会议
     */
    @RequestMapping(value = "/conference/phone", method = RequestMethod.GET)
    public ObjectResult addTempPhone(@RequestParam String phone,
                                     @RequestParam String confResId,
                                     @CookieValue(value = Constants.STAFF_TOKEN, required = false) String token) {
        try {
            LoginInfo loginInfo = tokenService.loadToken(token);
            String username = loginInfo == null ? "" : loginInfo.getUsername();
            int code = ipscService.addPhoneToConf(phone, confResId, username);
            if (code == 1) {
                return successRes("添加临时呼叫成功");
            } else if (code == 2) {
                return failureRes("添加临时呼叫错误");
            } else if (code == 3) {
                return failureRes("添加临时呼叫超时");
            } else if (code == 4) {
                return failureRes("会议资源不存在");
            } else {
                return failureRes("添加呼叫失败");
            }
        } catch (ServiceException e) {
            return failureRes(e.getMessage());
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("添加呼叫异常");
        }
    }

}

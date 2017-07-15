package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.utils.CommonUtils;
import cn.snzo.common.Constants;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.IAccountService;
import cn.snzo.service.ITokenService;
import cn.snzo.vo.AccountShow;
import cn.snzo.vo.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@RequestMapping("/api")
@RestController
public class LoginController extends BaseController{

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ITokenService<LoginInfo> tokenService;

    /**
     * 管理员密码登录
     * @param validateInfo
     * @param response
     * @return
     */
    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    public ObjectResult login(@RequestBody LoginInfo validateInfo,
                              HttpServletResponse response) {
        int ret = accountService.login(validateInfo);
        if (ret == 2) {
            return failureRes( "用户名错误");
        } else if (ret == 3) {
            return failureRes( "密码错误");
        } else {
            String token = tokenService.generateToken(validateInfo.getUsername());
            int expireTime = 24 * 60 * 60 * 7;  //7天
            Cookie cookie = CommonUtils.buildCookie(Constants.STAFF_TOKEN, token, expireTime, "/");
            response.addCookie(cookie);
            AccountShow accountShow = accountService.findByUsername(validateInfo.getUsername());
            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setUsername(accountShow.getUsername());
            loginInfo.setId(accountShow.getId());
            tokenService.saveToken(token, loginInfo, expireTime);
            return successRes(loginInfo);
        }
    }


    /**
     * 管理员通过token登录
     * @param token
     * @param response
     * @return
     */
    @RequestMapping(value = "/admin/login/token",method = RequestMethod.POST)
    public ObjectResult login(@CookieValue(Constants.STAFF_TOKEN)String token,
                              HttpServletResponse response){
        if(token == null || token.trim().equals("")){
            response.setStatus(401);
            return failureRes("令牌失效");
        }
        LoginInfo loginInfo = tokenService.loadToken(token);
        if(loginInfo != null){
            return  successRes( loginInfo);
        }
        return failureRes("您的令牌已失效，请重新登录");
    }




    /**
     * 管理员登出
     * @param token
     * @param response
     * @return
     */
    @RequestMapping(value = "/admin/logout/token",method = RequestMethod.POST)
    public ObjectResult logout(@CookieValue(Constants.STAFF_TOKEN)String token,
                               HttpServletRequest request,
                               HttpServletResponse response){
        if(token == null || token.trim().equals("")){
            response.setStatus(401);
            return failureRes("令牌失效");
        }
        int ret = tokenService.eraseToken(token);
        if(ret == 1){
            return  successRes( "登出成功");
        }
        return failureRes("登出失败");
    }
}

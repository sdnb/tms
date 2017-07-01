package cn.snzo.service.impl;

import cn.snzo.common.SessionRedis;
import cn.snzo.service.ITokenService;
import cn.snzo.utils.Md5Utils;
import cn.snzo.utils.RandomUtils;
import cn.snzo.vo.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chentao on 2017/3/21 0021.
 */
@Service
public class TokenService implements ITokenService<LoginInfo> {
    private SessionRedis sessionRedis = null;

    @Autowired
    public void setSessionRedis(SessionRedis sessionRedis) {
        this.sessionRedis = sessionRedis;
    }


    /**
     * 通过userName 生成一个唯一的token
     * @param userName 用户名
     * @return 加密后的唯一码
     */
    @Override
    public String generateToken(String userName) {
        //手机号码+时间戳+随机数 生成一个唯一的token
        return Md5Utils.MD5(userName + System.currentTimeMillis() + RandomUtils.getRandomNum(4));
    }

    /**
     * 通过token 解析 loginInfo
     * @param token 登陆令牌
     * @return loginInfo对象
     */
    @Override
    public LoginInfo loadToken(String token) {
        LoginInfo staffInfo = (LoginInfo)sessionRedis.getSessionOfList(token);
        return staffInfo;
    }

    /**
     * 保存token 及登陆信息
     * @param token         登陆令牌
     * @param loginInfo     登陆信息
     * @param expireSeconds 保存时间
     * @return 状态码 0 成功  1 失败
     */
    @Override
    public int saveToken(String token, LoginInfo loginInfo, long expireSeconds) {
        sessionRedis.saveSessionOfList(token, loginInfo, expireSeconds);
        return 0;
    }

    /**
     * 销毁token
     * @param token 登陆令牌
     * @return 状态码  0 成功  1 失败
     */
    @Override
    public int eraseToken(String token) {
        boolean res = sessionRedis.delSessionAllOfList(token);
        if(res){
            return 0;
        }
        return 1;
    }

    /**
     * 修改token信息
     *
     * @param token
     * @param loginInfo
     * @return
     */
    @Override
    public int updateToken(String token, LoginInfo loginInfo) {
        return 0;
    }
}

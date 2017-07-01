package cn.snzo.service;

/**
 * Created by chentao on 2017/3/21 0021.
 */
public interface ITokenService<T> {
    /**
     *  生成一个唯一的token
     * @param identity
     * @return  加密后的唯一码
     */
    String generateToken(String identity);


    /**
     * 通过token 解析 loginInfo
     * @param token  登陆令牌
     * @return  UserShow对象
     */
    T loadToken(String token);


    /**
     * 保存token 及登陆信息
     * @param token 登陆令牌
     * @param loginInfo  登陆信息
     * @param expireSeconds 保存时间
     * @return  状态码 0 成功  1 失败
     */
    int saveToken(String token, T loginInfo, long expireSeconds);


    /**
     * 销毁token
     * @param token  登陆令牌
     * @return  状态码  0 成功  1 失败
     */
    int eraseToken(String token);


    /**
     * 修改token信息
     * @param token
     * @param loginInfo
     * @return
     */
    int updateToken(String token, T loginInfo);
}

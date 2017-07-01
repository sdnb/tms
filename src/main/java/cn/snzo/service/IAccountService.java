package cn.snzo.service;

import cn.snzo.vo.AccountShow;
import cn.snzo.vo.LoginInfo;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
public interface IAccountService {

    AccountShow add(AccountShow accountShow);

    int login(LoginInfo loginInfo);

    AccountShow findByUsername(String username);

}

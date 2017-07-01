package cn.snzo.service.impl;

import cn.snzo.entity.Account;
import cn.snzo.repository.AccountRepository;
import cn.snzo.service.IAccountService;
import cn.snzo.utils.Md5Utils;
import cn.snzo.utils.RandomUtils;
import cn.snzo.vo.AccountShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
@Service
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AccountShow add(AccountShow accountShow) {
        String password = accountShow.getPassword();
        //生成密码盐 数字字母混合
        String salt = RandomUtils.getRandom(4, true);
        password = Md5Utils.MD5(password + salt);

        Account account = accountRepository.findByUsername(accountShow.getUsername());
        if (account != null) {
            throw new RuntimeException("用户名已存在");
        }
        account = new Account();
        account.setPassword(password);
        account.setSalt(salt);
        account.setUsername(accountShow.getUsername());
        accountRepository.save(account);
        return new AccountShow(account);
    }
}

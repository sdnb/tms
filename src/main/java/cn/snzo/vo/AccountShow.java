package cn.snzo.vo;

import cn.snzo.entity.Account;
import org.springframework.beans.BeanUtils;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
public class AccountShow extends Account {
    private String newPassword;

    public AccountShow() {
    }

    public AccountShow(Account account) {
        super();
        BeanUtils.copyProperties(account, this);
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

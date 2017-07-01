package cn.snzo.vo;

import cn.snzo.entity.Conductor;
import org.springframework.beans.BeanUtils;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
public class ConductorShow extends Conductor{

    public ConductorShow() {
    }

    public ConductorShow(Conductor conductor) {
        BeanUtils.copyProperties(conductor, this);
    }

    private AccountShow accountShow;


    public AccountShow getAccountShow() {
        return accountShow;
    }

    public void setAccountShow(AccountShow accountShow) {
        this.accountShow = accountShow;
    }
}

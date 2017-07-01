package cn.snzo.entity;

import cn.snzo.vo.ConductorShow;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
@Entity
@Table(name = "t_conductor")
public class Conductor extends BaseEntity{


    @NotBlank(message = "姓名不能为空")
    private String realname;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "电话不能为空")
    @Column(columnDefinition = "char")
    private String phone;

    private Integer accountId;

    public Conductor() {
    }

    public Conductor(ConductorShow conductorShow) {
        super();
        BeanUtils.copyProperties(conductorShow, this);
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}

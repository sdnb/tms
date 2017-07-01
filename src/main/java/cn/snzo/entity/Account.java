package cn.snzo.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ThomasC on 2017/6/28 0028.
 */
@Entity
@Table(name = "t_account")
public class Account extends BaseEntity{
    @NotBlank(message = "用户名不能为空")
    private String   username;

    @NotBlank(message = "密码不能为空")
    private String   password;
    private String   salt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}

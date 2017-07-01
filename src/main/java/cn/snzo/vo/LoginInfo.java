package cn.snzo.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public class LoginInfo implements Serializable{
    private static final long serialVersionUID = 1383967544244835260L;
    private Integer id;
    private String username;
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
}

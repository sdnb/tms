package cn.snzo.entity;

import cn.snzo.vo.ContactShow;
import org.springframework.beans.BeanUtils;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Entity
@Table(name = "t_contact")
public class Contact extends BaseEntity {
    private String phone;
    private String name;

    public Contact() {
    }

    public Contact(ContactShow contactShow) {
        BeanUtils.copyProperties(contactShow, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

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
    private Integer bookId;

    public Contact() {
    }

    public Contact(String phone, String name, Integer bookId) {
        this.phone = phone;
        this.name = name;
        this.bookId = bookId;
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

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
}

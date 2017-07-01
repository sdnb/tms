package cn.snzo.vo;

import cn.snzo.entity.Contact;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public class ContactShow {
    private Integer id;

    @NotBlank(message = "电话不能为空")
    private String phone;

    @NotBlank(message = "名字不能为空")
    private String name;

    @NotNull(message = "所属分组不能为空")
    private Integer groupId;

    @NotNull(message = "所属电话簿不能为空")
    private Integer bookId;

    public ContactShow() {

    }
    public ContactShow(Contact contact) {
        BeanUtils.copyProperties(contact, this);
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
}

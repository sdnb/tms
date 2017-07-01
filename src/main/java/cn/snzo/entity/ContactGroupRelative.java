package cn.snzo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Entity
@Table(name = "t_contact_group_relative")
public class ContactGroupRelative extends BaseEntity {

    private Integer groupId;
    private Integer contactId;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }
}

package cn.snzo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Entity
@Table(name = "t_phone_book")
public class PhoneBook extends BaseEntity {
    private  Integer  roomId;   //会议室id
    private  Integer  type;     // 类型 1 系统 2会议室

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

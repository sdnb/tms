package cn.snzo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/6/30 0030.
 */
@Entity
@Table(name = "t_room_conductor_relative")
public class RoomConductorRelative extends BaseEntity {

    private Integer roomId;
    private Integer conductorId;
    private String  conductorName;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getConductorId() {
        return conductorId;
    }

    public void setConductorId(Integer conductorId) {
        this.conductorId = conductorId;
    }

    public String getConductorName() {
        return conductorName;
    }

    public void setConductorName(String conductorName) {
        this.conductorName = conductorName;
    }
}

package cn.snzo.vo;

import java.util.List;

/**
 * Created by chentao on 2017/7/11 0011.
 */
public class ConferenceStartShow {
    private Integer roomId;
    private Integer conductorId;
    private List<String> phones;
    private boolean isRecordEnable;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public boolean isRecordEnable() {
        return isRecordEnable;
    }

    public void setIsRecordEnable(boolean isRecordEnable) {
        this.isRecordEnable = isRecordEnable;
    }

    public Integer getConductorId() {
        return conductorId;
    }

    public void setConductorId(Integer conductorId) {
        this.conductorId = conductorId;
    }
}

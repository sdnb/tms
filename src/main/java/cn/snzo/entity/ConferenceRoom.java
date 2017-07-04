package cn.snzo.entity;

import cn.snzo.vo.ConferenceRoomShow;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/6/30 0030.
 */
@Entity
@Table(name = "t_conference_room")
public class ConferenceRoom extends BaseEntity {

    private   String    number;         //编号
    private   Integer   isInUse;        //是否使用中   1 使用   0 空置
    private   Integer   isRecordEnable; //是否可录音   1 可录音 0 不可录音
    private   String    ivrPassword;    //会议参会密码
    private   Integer   maxParticipant; //最大参会人数
    private   Integer   conductorId;    //主持人id
    private   String    conductorName;  //主持人姓名

    public ConferenceRoom() {
    }

    public ConferenceRoom(ConferenceRoomShow conferenceRoomShow) {
        Assert.notNull(conferenceRoomShow);
        BeanUtils.copyProperties(conferenceRoomShow, this);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getIsInUse() {
        return isInUse;
    }

    public void setIsInUse(Integer isInUse) {
        this.isInUse = isInUse;
    }

    public Integer getIsRecordEnable() {
        return isRecordEnable;
    }

    public void setIsRecordEnable(Integer isRecordEnable) {
        this.isRecordEnable = isRecordEnable;
    }

    public String getIvrPassword() {
        return ivrPassword;
    }

    public void setIvrPassword(String ivrPassword) {
        this.ivrPassword = ivrPassword;
    }

    public Integer getMaxParticipant() {
        return maxParticipant;
    }

    public void setMaxParticipant(Integer maxParticipant) {
        this.maxParticipant = maxParticipant;
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

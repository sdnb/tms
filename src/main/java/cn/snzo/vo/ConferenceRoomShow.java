package cn.snzo.vo;

import cn.snzo.entity.ConferenceRoom;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public class ConferenceRoomShow {

    private   Integer   id;
    private   String    number;         //编号
    private   Integer   isInUse;        //是否使用中   1 使用   0 空置

    @NotNull(message = "是否可录音不能为空")
    private   Integer   isRecordEnable; //是否可录音   1 可录音 0 不可录音

    @Length(max = 4,
            message = "ivr密码最大长度为4位")
    private   String    ivrPassword;    //会议参会密码

    @NotNull(message = "请填写最大参会人数")
    private   Integer   maxParticipant; //最大参会人数
    private   Integer   conductorId;    //主持人id
    private   String    conductorName;  //主持人姓名

    public ConferenceRoomShow() {
    }

    public ConferenceRoomShow(ConferenceRoom conferenceRoom) {
        BeanUtils.copyProperties(conferenceRoom, this);
    }

    public String getConductorName() {
        return conductorName;
    }

    public void setConductorName(String conductorName) {
        this.conductorName = conductorName;
    }

    public Integer getConductorId() {
        return conductorId;
    }

    public void setConductorId(Integer conductorId) {
        this.conductorId = conductorId;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

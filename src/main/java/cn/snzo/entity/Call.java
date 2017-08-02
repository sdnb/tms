package cn.snzo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/25 0025.
 */
@Entity
@Table(name = "t_call")
public class Call extends BaseEntity{

    private String resId;               //呼叫资源id
    private String confResId;           //会议资源id
    private Integer derection;          //方向 1 呼入 2呼出
    private Integer roomId;             //会议室id
    private Integer conductorId;        //主持人id
    private Integer status;             //呼叫状态  1 振铃 2 参会中 3 已离会 4 未接听
    private Date startAt;               //开始时间
    private Date endAt;                 //结束时间
    private String name;                //名字
    private String phone;               //电话
    private Integer voiceMode;          //声音收放模式 1 放音+收音 2 收音 3 放音  4 无
    private Integer contactId;          //联系人id
    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getConfResId() {
        return confResId;
    }

    public void setConfResId(String confResId) {
        this.confResId = confResId;
    }

    public Integer getDerection() {
        return derection;
    }

    public void setDerection(Integer derection) {
        this.derection = derection;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
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

    public Integer getVoiceMode() {
        return voiceMode;
    }

    public void setVoiceMode(Integer voiceMode) {
        this.voiceMode = voiceMode;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }
}

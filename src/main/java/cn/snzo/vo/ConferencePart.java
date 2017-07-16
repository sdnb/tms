package cn.snzo.vo;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by chentao on 2017/7/15 0015.
 */
public class ConferencePart {
    private String phone;
    private String name;
    private String callId;
    private int voiceMode;

    public ConferencePart() {
    }

    public ConferencePart(JSONObject obj) {
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

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public int getVoiceMode() {
        return voiceMode;
    }

    public void setVoiceMode(int voiceMode) {
        this.voiceMode = voiceMode;
    }
}

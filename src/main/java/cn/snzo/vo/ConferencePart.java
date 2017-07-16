package cn.snzo.vo;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by chentao on 2017/7/15 0015.
 */
public class ConferencePart {
    private String phone;
    private String confId;
    private int voiceMode;

    public ConferencePart() {
    }

    public ConferencePart(JSONObject obj) {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public int getVoiceMode() {
        return voiceMode;
    }

    public void setVoiceMode(int voiceMode) {
        this.voiceMode = voiceMode;
    }
}

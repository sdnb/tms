package cn.snzo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@Entity
@Table(name = "t_sys_setting")
public class SysSetting extends BaseEntity{
    private String recordingPath;  //录音存储路径

    public String getRecordingPath() {
        return recordingPath;
    }

    public void setRecordingPath(String recordingPath) {
        this.recordingPath = recordingPath;
    }
}

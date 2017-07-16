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
    private String hostname;        //主机ip
    private Integer port;           //端口

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public  String getRecordingPath() {
        return recordingPath;
    }

    public void setRecordingPath(String recordingPath) {
        this.recordingPath = recordingPath;
    }
}

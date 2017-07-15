package cn.snzo.vo;

import cn.snzo.entity.Log;
import org.springframework.beans.BeanUtils;

/**
 * Created by chentao on 2017/7/15 0015.
 */
public class LogShow extends Log {
    public LogShow() {
    }

    public LogShow(Log log) {
        BeanUtils.copyProperties(log, this);
    }
}

package cn.snzo.vo;

import cn.snzo.entity.Conference;
import org.springframework.beans.BeanUtils;

/**
 * Created by chentao on 2017/7/15 0015.
 */
public class ConferenceShow extends Conference{

    public ConferenceShow() {
    }

    public ConferenceShow(Conference conference) {
        BeanUtils.copyProperties(conference, this);
    }
}

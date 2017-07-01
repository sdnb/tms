package cn.snzo.common;

import cn.snzo.utils.RandomUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public class NumberGenerator {
    public static String randomNumber() {
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
        String time = localTime.format(formatter);
        String random = RandomUtils.getRandomNum(4);
        return time + random;
    }
}

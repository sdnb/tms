package cn.snzo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chentao on 2017/7/14 0014.
 */
public class DateUtil {

    public static Date date = null;

    public static DateFormat dateFormat = null;

    public static Calendar calendar = null;

    /**
     * 功能描述：
     * @param date 日期
     * @return "yyyy/MM/dd hh:mm格式的时间
     */
    public static String format(Date date, String format) {
        String result = "";
        try {
            if (date != null) {
                dateFormat = new SimpleDateFormat(format);
                result = dateFormat.format(date);
            }
        } catch (Exception e) {
        }
        return result;
    }
}

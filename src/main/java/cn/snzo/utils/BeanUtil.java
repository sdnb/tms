package cn.snzo.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public class BeanUtil {

    private BeanUtil(){}

    public static <E, S> void entityToShow(E e, S s) {
        Assert.notNull(e);
        Assert.notNull(s);
        BeanUtils.copyProperties(e, s);
    }


    public static <S, E> void showToEntity(S s, E e) {
        Assert.notNull(s);
        Assert.notNull(e);
        BeanUtils.copyProperties(s, e);
    }


}

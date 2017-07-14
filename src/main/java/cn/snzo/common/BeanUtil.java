package cn.snzo.common;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Created by chentao on 2017/7/14 0014.
 */
public class BeanUtil {

    public static  <S, E> void showToEntity(S s, E e) {
        Assert.notNull(s);
        Assert.notNull(e);
        BeanUtils.copyProperties(s, e);
    }


    public static <S, E> void entityToShow(E e, S s) {
        Assert.notNull(s);
        Assert.notNull(e);
        BeanUtils.copyProperties(e, s);
    }
}

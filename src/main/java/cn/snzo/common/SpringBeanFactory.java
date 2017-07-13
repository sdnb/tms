package cn.snzo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by chentao on 2017/7/13 0013.
 */
public class SpringBeanFactory implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SpringBeanFactory.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取某个Bean的对象
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            logger.error("Spring getBean:" + clazz, e);
        }
        return null;
    }
}

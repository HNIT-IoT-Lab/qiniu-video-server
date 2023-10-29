package cn.hnit.utils.starter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Spring context帮助类
 *
 * @author king
 * @since 2022-09-22 21:45
 **/
@Slf4j
@Component
public class ApplicationContextHolder implements ApplicationContextAware, BeanFactoryPostProcessor {

    private static ApplicationContext applicationContext;

    private static Environment environment;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static  <T> T getBean (Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(clazz);
    }

    public static <T> Collection<T> getBeanByType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz).values();
    }

    public static <T extends Annotation> Collection<Object> getBeanByAnnotation(Class<T> clazz) {
        return applicationContext.getBeansWithAnnotation(clazz).values();
    }

    public static <T> T getBean (String name, Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(name, clazz);
    }

    public static Object getBean (String name) {
        assertContextInjected();
        return applicationContext.getBean(name);
    }

    /**
     * 获取属性配置信息
     * @param propertyKey
     * @return
     */
    public static String getProperty(String propertyKey){
        return environment.getProperty(propertyKey);
    }


    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        Validate.notNull(applicationContext, "applicationContext属性未注入");
    }

    /**
     * 获取当前环境
     * @return
     */
    public static String getActiveProfile(){
        return ApplicationContextHolder.environment.getActiveProfiles()[0];
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        ApplicationContextHolder.environment = configurableListableBeanFactory.getBean(Environment.class);
    }
}
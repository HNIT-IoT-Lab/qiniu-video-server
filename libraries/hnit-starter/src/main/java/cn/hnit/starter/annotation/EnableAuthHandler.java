package cn.hnit.starter.annotation;

import cn.hnit.starter.intercept.interceptor.SimpleAuthorizationInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 启用全局异常处理
 *
 * @author 梁峰源
 * @date 2022年11月12日21:10:20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SimpleAuthorizationInterceptor.class})
public @interface EnableAuthHandler {
}

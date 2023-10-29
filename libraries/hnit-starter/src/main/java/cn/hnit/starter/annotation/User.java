package cn.hnit.starter.annotation;

import cn.hnit.starter.intercept.argumentresolver.handle.UserHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.lang.annotation.*;


/**
 * 用来标记当前对象应该从用户上下文中拿到，并注入到controller的方法参数中
 *
 * @author 梁峰源
 * @see HandlerMethodArgumentResolver
 * @see UserHandlerMethodArgumentResolver
 * @since 2022年9月22日19:43:07
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface User {
}

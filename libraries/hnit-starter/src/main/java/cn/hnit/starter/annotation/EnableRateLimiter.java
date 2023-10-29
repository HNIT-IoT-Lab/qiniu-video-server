package cn.hnit.starter.annotation;

import cn.hnit.starter.intercept.aop.RateLimiterAop;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author xsm
 * @date 2021/11/16
 * @Description 开启限流处理
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(value = RateLimiterAop.class)
public @interface EnableRateLimiter {

}

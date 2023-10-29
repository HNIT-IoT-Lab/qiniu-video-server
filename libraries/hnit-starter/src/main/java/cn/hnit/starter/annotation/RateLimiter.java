package cn.hnit.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * @author xsm
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {
    /**
     * 分布式缓存key，默认为空取类名+方法名。建议自定义设置
     */
    String key() default "";

    /**
     * 每秒限流数，默认为10
     */
    int permitsPerSecond() default 10;

    /**
     * 每秒限流数环境变量名，默认为空则取permitsPerSecond；若非空，则优先取环境变量值（如buge:rate:limit）
     */
    String permitsPerSecondProperty() default "";

    /**
     * 达到限速条件时，可降级返回响应数据，默认不返回而是抛出异常返回错误。
     */
    String fallback() default "";

    /**
     * 当返回响应数据格式为泛型时，如Response<?>或List<?>，需指定泛型类型，以支持将fallback进行反序列号
     */
    Class<?> generic() default void.class;

    /**
     * 当抛出限流异常错误时，默认: "您的请求过于频繁，请稍候重试"，也可通过此项定制化响应信息
     */
    String errorInfo() default "";

    int errorCode() default 0;
}

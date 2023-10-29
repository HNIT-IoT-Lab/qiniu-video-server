package cn.hnit.starter.annotation;

import java.lang.annotation.*;

/**
 * 验证类型 加了注解的为忽略
 *
 * @author 梁峰源
 * @since 2022年9月16日11:07:40
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AuthIgnore {

    /**
     * ALL 表示所有请求均不进行校验<br/>
     * TOKEN 表示请求必须携带token，默认所有接口请求需要携带token<br/>
     * ENCRYPT 表示携带的token必须加密
     */
    enum Type {ALL, TOKEN, ENCRYPT}

    /**
     * 包含哪些类型,ALL、AUTH、ENCRYPT
     *
     * @return {@link AuthIgnore.Type}
     */
    Type type() default Type.ALL;

}
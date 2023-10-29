package cn.hnit.starter.annotation;

import java.lang.annotation.*;

/**
 * 不用包装返回
 *
 * @author 梁峰源
 * @since 2022年9月16日11:07:40
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ResponseIgnore {


}
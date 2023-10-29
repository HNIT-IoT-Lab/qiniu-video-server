package cn.hnit.starter.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存参数。结合{@link Cache}一起使用，自动将参数与Cache.key组合为动态key
 * @author xsm
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheParam {

    /**
     * 当修饰参数为复杂对象时，可指定为取值为对象属性。默认为空，直接取值对象本身
     */
    String value() default "";
}

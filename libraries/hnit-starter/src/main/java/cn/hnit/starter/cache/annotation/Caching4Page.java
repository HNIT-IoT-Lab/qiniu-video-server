package cn.hnit.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * @author: liujiwei (besnowegle@vip.qq.com)
 * @date: 2021/10/15
 * @description:
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Caching4Page {
    CacheEvict4Page[] evict() default {};
}

package cn.hnit.starter.cache.annotation;

import cn.hnit.starter.cache.aspect.Cacheable4PageSupport;

import java.lang.annotation.*;


/**
 * 功能：缓存清除
 *
 * @author 梁峰源
 * @since  2022年9月25日17:28:36
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict4Page {
    /**
     * 缓存的名字
     */
    String cacheName();

    /**
     * 分页key，不需要分页的num和size，但是需要分页的条件
     */
    String pageKey() default Cacheable4PageSupport.DEFAULT_PAGE_PARAM;

    /**
     * 是否需要进行异步处理
     */
    boolean async() default true;

    /**
     * 异步处理线程池的名字
     */
    String executor() default "";
}

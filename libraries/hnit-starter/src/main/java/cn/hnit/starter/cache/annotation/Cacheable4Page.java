package cn.hnit.starter.cache.annotation;

import cn.hnit.starter.cache.aspect.Cacheable4PageSupport;

import java.lang.annotation.*;

/**
 * 用来标记应该缓存
 *
 * @author 梁峰源
 * @since 2022年9月25日17:28:36
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable4Page {
    String cacheName();

    /**
     * keyField对应的缓存
     */
    String itemCacheName();

    /**
     * 缓存对应的key
     */
    String itemKeyField() default "id";

    /**
     * 分页key，不包括分页参数，包含查询条件
     */
    String pageKey() default "";

    /**
     * 分页参数
     */
    String pageParam() default Cacheable4PageSupport.DEFAULT_PAGE_PARAM;
}

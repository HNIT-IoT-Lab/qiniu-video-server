package cn.hnit.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 批量查询缓存.1.先遍历参数中的key并从缓存中查询，2.数据库中批量查询未命中的。3.缓存数据库中查询的结果。4.返回结果<br/>
 * 返回值暂只支持list，Iterable<key>  <br/>
 * ！！！使用注意：若注解方法中有过滤逻辑，不能使用该注解。
 *
 * @author 梁峰源
 * @since 2022年9月25日17:28:36
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheList {
    String itemCacheName();

    int listPos() default 0;

    String itemKeyField() default "id";
}

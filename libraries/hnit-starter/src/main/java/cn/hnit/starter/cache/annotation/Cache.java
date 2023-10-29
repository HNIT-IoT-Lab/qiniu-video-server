package cn.hnit.starter.cache.annotation;


import cn.hnit.starter.constant.CacheManagerEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 缓存注解: 添加了该注解的方法, 会自动将方法返回结果进行缓存, 如果当前方法有缓存则直接返回缓存数据, 目前仅支持Redis
 *
 * @author 梁峰源
 * @since 2022年9月16日11:18:05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    /**
     * 缓存key。推荐用xx:yy格式
     */
    String key() default "";

    /**
     * 缓存有效期，默认为30。时间单位{@link #ttlTimeUnit}
     * 5秒、30秒、60秒、5分钟、半小时、1小时, 默认30分钟
     */
    long ttl() default 30;

    /**
     * 启用二级缓存时此设置才有效，ttl0为ehCache一级缓存时长。通常一级缓存用于应对热点数据高并发请求，时长较短
     * todo 暂未实现
     */
    long ttl0() default 30;

    /**
     * 缓存有效期时间单位，默认为分钟。时间单位参考{@link TimeUnit java.util.concurrent.TimeUnit} ;如果cacheManager=ehCacheCacheManager时，该属性无效
     */
    TimeUnit ttlTimeUnit() default TimeUnit.MINUTES;

    /**
     * 启用二级缓存时此设置才有效，ttl0TimeUnit用于设置ehCache一级缓存有效期时间单位，默认为秒。
     */
    TimeUnit ttl0TimeUnit() default TimeUnit.SECONDS;

    /**
     * 设置为true时，不管缓存中是否存在，都会先执行目标函数，然后将执行结果存入缓存。默认为false
     */
    boolean alwaysPut() default false;

    /**
     * 目前暂未支持
     */
    boolean sync() default false;

    /**
     * 使用的缓存管理器，目前支持redisCacheManager, ehCacheCacheManager待实现, 默认redisCacheManager
     */
    CacheManagerEnum cacheManager() default CacheManagerEnum.Redis;

    /**
     * 是否删除缓存，默认为false。 当对数据库有更新操作时,直接使用remove功能来删除缓存
     */
    boolean remove() default false;

    /**
     * 数据刷新条件说明，无实际作用，仅方便了解
     */
    String refreshConditionOn() default "";

    /**
     * 对cacheParam使用md5加密，缩短缓存key，默认为false。
     */
    boolean paramMd5() default false;

    /**
     * null结果集（不含空集合）缓存时间（秒），0代表不缓存，默认为缓存600秒(${cache.empty.ttl})
     */
    int nullCacheSecond() default -1;

    /**
     * 当remove=true时，是否在业务方法前执行删除缓存操作
     */
    boolean removeBeforeInvocation() default true;

    /**
     * 是否防止缓存击穿
     */
    boolean preventCacheBreakdown() default false;
}

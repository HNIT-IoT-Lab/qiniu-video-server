package cn.hnit.starter.constant;

import cn.hnit.starter.cache.aspect.CacheAop;

/**
 * 缓存管理器类型，具体缓存实现请看{@link CacheAop}
 *
 * @author 梁峰源
 * @since 2022年9月16日11:13:02
 * @see CacheAop
 * @see CacheM
 */
public enum CacheManagerEnum {

    /**
     * Caffeine本地JVM缓存，用来做一级缓存
     */
    Caffeine("CASSEINE_CACHE_MANAGER"),

    /**
     * Redis缓存管理器
     */
    Redis("REDIS_CACHE_MANAGER"),


    // todo ehCache 待实现
    ;

    private final String value;

    CacheManagerEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

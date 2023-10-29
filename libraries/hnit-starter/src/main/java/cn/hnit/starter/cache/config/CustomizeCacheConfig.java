package cn.hnit.starter.cache.config;

import lombok.Data;

/**
 * 模板缓存配置
 *
 * @author 梁峰源
 * @since 2022/10/2 12:06
 */
@Data
public class CustomizeCacheConfig {
    /**
     * 自定义缓存有效期，单位分钟
     */
    private long ttlOfMinutes;
    /**
     * 是否启用一级缓存，默认启用
     */
    private boolean enableL1Cache = true;

    public CustomizeCacheConfig(long ttlOfMinutes){
        this.ttlOfMinutes = ttlOfMinutes;
    }

    public CustomizeCacheConfig(long ttlOfMinutes, boolean enableL1Cache){
        this.ttlOfMinutes = ttlOfMinutes;
        this.enableL1Cache = enableL1Cache;
    }
}

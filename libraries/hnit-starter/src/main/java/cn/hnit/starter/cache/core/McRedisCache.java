package cn.hnit.starter.cache.core;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author 梁峰源
 * @since 2022/10/2 10:54
 */
public class McRedisCache extends RedisCache {

    private final McRedisCacheWriter cacheWriter;

    public McRedisCache(String name, McRedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        this.cacheWriter = cacheWriter;
    }

    public void evictAll(Collection<Object> keys) {
        List<byte[]> collect = keys.stream().map(s -> super.serializeCacheKey(super.createCacheKey(s))).collect(Collectors.toList());
        this.cacheWriter.remove(super.getName(), collect);
    }

    public void putAll(Map<Object, Object> map) {
        Map<byte[], byte[]> keyValue = new HashMap<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            keyValue.put(super.serializeCacheKey(super.createCacheKey(entry.getKey())), super.serializeCacheValue(entry.getValue()));
        }
        this.cacheWriter.putAll(super.getName(), keyValue, super.getCacheConfiguration().getTtl());
    }

}

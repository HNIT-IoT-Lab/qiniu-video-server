package cn.hnit.starter.cache.manager;

import cn.hnit.starter.cache.L1CacheSynchronizer;
import cn.hnit.starter.cache.config.CustomizeCacheConfig;
import cn.hnit.starter.cache.core.McRedisCache;
import cn.hnit.starter.cache.core.McRedisCacheWriter;
import cn.hnit.starter.cache.core.RedisCaffeineCache;
import cn.hnit.starter.cache.synchronizer.L1CacheRedisSynchronizer;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis和caffeine实现的多级缓存
 *
 * @author 梁峰源
 * @since 2022/10/2 10:53
 */
@Slf4j
public class RedisCaffeineCacheManager implements CacheManager {
    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private final boolean allowNullValues = true;
    private final RedisCacheManager redisCacheManager;
    private final CaffeineCacheManager caffeineCacheManager;
    private final L1CacheSynchronizer l1CacheSynchronizer;
    private final Map<String, CustomizeCacheConfig> customizeCacheConfigMap;

    /**
     * 生成redis缓存配置
     *
     * @param ttl 有效时间
     */
    private RedisCacheConfiguration cacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .computePrefixWith(cacheName -> "caching:" + cacheName)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    public RedisCaffeineCacheManager(int initialCapacity, int maximumSize,
                                     int expireSeconds, StringRedisTemplate redisTemplate, String namespace,
                                     Map<String, CustomizeCacheConfig> customizeCacheConfigMap) {
        this.customizeCacheConfigMap = customizeCacheConfigMap;

        Set<String> initialCacheName = new HashSet<>();
        Map<String, RedisCacheConfiguration> initialCacheConfiguration = new HashMap<>();
        if (!CollectionUtils.isEmpty(customizeCacheConfigMap)) {
            for (Map.Entry<String, CustomizeCacheConfig> entry : customizeCacheConfigMap.entrySet()) {
                initialCacheConfiguration.put(entry.getKey(),
                        cacheConfiguration(Duration.ofMinutes(entry.getValue().getTtlOfMinutes())));
                initialCacheName.add(entry.getKey());
                log.info("==多级缓存系统== 自定义缓存配置 cacheName:{}, config:{}", entry.getKey(), JSONUtil.toJsonStr(entry.getValue()));
            }
        }

        McRedisCacheWriter cacheWriter = new McRedisCacheWriter(redisTemplate.getConnectionFactory());
        redisCacheManager = McRedisCacheManager.builder(cacheWriter)
                .initialCacheNames(initialCacheName)
                .withInitialCacheConfigurations(initialCacheConfiguration)
                .cacheDefaults(cacheConfiguration(Duration.ofSeconds(expireSeconds)))
                .build();
        redisCacheManager.afterPropertiesSet();

        caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setAllowNullValues(allowNullValues);
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS));

        l1CacheSynchronizer = new L1CacheRedisSynchronizer(this, redisTemplate, namespace);
        l1CacheSynchronizer.start();
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = this.createCache(name);
                    this.cacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableCollection(cacheMap.keySet());
    }

    private Cache createCache(String name) {
        // 构造二级缓存
        McRedisCache redisCache = (McRedisCache) redisCacheManager.getCache(name);

        // 默认开启一级缓存
        boolean enableL1Cache = true;
        if (!CollectionUtils.isEmpty(this.customizeCacheConfigMap) && this.customizeCacheConfigMap.containsKey(name)) {
            enableL1Cache = this.customizeCacheConfigMap.get(name).isEnableL1Cache();
        }

        CaffeineCache caffeineCache = null;
        if (enableL1Cache) {
            // 构造一级缓存
            caffeineCache = (CaffeineCache) caffeineCacheManager.getCache(name);
        }

        return new RedisCaffeineCache(name, caffeineCache, redisCache,
                allowNullValues, l1CacheSynchronizer);
    }
}

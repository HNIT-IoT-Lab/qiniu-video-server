package cn.hnit.starter.cache.core;

import cn.hnit.starter.cache.L1CacheSynchronizer;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.NullValue;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * 实现Cache接口，集成对redis和caffeine缓存的操作<br/>
 * 1. 查询：先从一级缓存Caffeine查询，没有的话再去二级缓存redis查询<br/>
 * 2. 缓存：先设置一级缓存，再设置二级缓存<br/>
 * 3. 删除：先删除二级缓存，再删除一级缓存，否则存在并发问题<br/>
 * 4. 删除：删除时通过redis pub/sub通知其他节点的一级缓存同步删除<br/>
 * 5. 一致性：redis的Pub/Sub（订阅发布）模式发送消息是无状态的，<br/>
 * 如果遇到网络等原因有可能导致一些应用服务器上的一级缓存没办法删除，可能会出现一段时间的缓存不一致问题<br/>
 * 如果对L1和L2数据同步要求较高的话，这里可以使用MQ来做，确保消息被消费
 *
 * @author 梁峰源
 * @since 2022/10/2 10:54
 */
@Slf4j
public class RedisCaffeineCache extends AbstractValueAdaptingCache {

    private final Gson gson = new Gson();
    private final String name;
    private final CaffeineCache caffeineCache;
    private final McRedisCache redisCache;
    private final L1CacheSynchronizer l1CacheSynchronizer;
    private final boolean enableL1Cache;


    public RedisCaffeineCache(String name, CaffeineCache caffeineCache, McRedisCache redisCache,
                              boolean allowNullValues, L1CacheSynchronizer l1CacheSynchronizer) {
        // 允许缓存空值，避免缓存穿透
        super(allowNullValues);
        this.name = name;
        this.caffeineCache = caffeineCache;
        this.redisCache = redisCache;
        this.l1CacheSynchronizer = l1CacheSynchronizer;
        this.enableL1Cache = caffeineCache != null;
    }

    public void evictL1Cache(String key) {
        if (enableL1Cache) {
            caffeineCache.evict(key);
            if (log.isDebugEnabled()) {
                log.debug("删除一级缓存 name:{}, key:{}", name, key);
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    protected Object lookup(Object keyObj) {
        String key = String.valueOf(keyObj);
        Object value = null;
        if (enableL1Cache) {
            value = caffeineCache.get(key);
            if (log.isDebugEnabled()) {
                log.debug("lookup查询一级缓存 name:{}, key:{},返回值是:{}", name, key, gson.toJson(value));
            }
        }
        if (value == null) {
            value = redisCache.get(key);
            log.debug("lookup查询二级缓存 name:{}, key:{},返回值是:{}", name, key, gson.toJson(value));
        }
        return value;
    }

    @Override
    public ValueWrapper get(Object keyObj) {
        String key = String.valueOf(keyObj);
        ValueWrapper wrapper = null;
        // 查询一级缓存
        if (enableL1Cache) {
            wrapper = caffeineCache.get(key);
            if (log.isDebugEnabled()) {
                log.debug("ValueWrapper get查询一级缓存 name:{}, key:{},返回值是:{}", name, key, gson.toJson(wrapper));
            }
        }

        if (wrapper == null) {
            // 查询二级缓存
            wrapper = redisCache.get(key);
            if (wrapper != null && enableL1Cache) {
                caffeineCache.put(key, wrapper.get());
            }
            if (log.isDebugEnabled()) {
                log.debug("ValueWrapper get查询二级缓存 name:{}, key:{},返回值是:{}", name, key, gson.toJson(wrapper));
            }
        }
        return wrapper;
    }

    @Override
    public <T> T get(Object keyObj, Class<T> type) {
        String key = String.valueOf(keyObj);
        T value = null;
        // 查询一级缓存
        if (enableL1Cache) {
            value = caffeineCache.get(key, type);
            if (log.isDebugEnabled()) {
                log.debug("T get查询一级缓存 name:{}, key:{},返回值是:{}", name, key, gson.toJson(value));
            }
        }

        if (value == null) {
            // 查询二级缓存
            value = redisCache.get(key, type);
            if (enableL1Cache) {
                caffeineCache.put(key, value);
            }
            log.debug("T get查询二级缓存 name:{}, key:{},返回值是:{}", name, key, gson.toJson(value));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object keyObj, Callable<T> valueLoader) {
        String key = String.valueOf(keyObj);
        T value = null;
        if (enableL1Cache) {
            // 查询一级缓存,如果一级缓存没有值则调用getForSecondaryCache(k, valueLoader)查询二级缓存
            value = (T) caffeineCache.getNativeCache().get(key, k -> getFromSecondaryCache(k, valueLoader));
        } else {
            value = (T) getFromSecondaryCache(key, valueLoader);
        }

        if (log.isDebugEnabled()) {
            log.debug("T get Callable查询一级缓存 name:{}, key:{},返回值是:{}", name, key, gson.toJson(value));
        }

        if (value instanceof NullValue) {
            return null;
        }
        return value;
    }

    @Override
    public void put(Object keyObj, Object value) {
        String key = String.valueOf(keyObj);
        if (log.isDebugEnabled()) {
            log.debug("void put 设置缓存 name:{}, key:{}, value:{}", name, key, gson.toJson(value));
        }
        redisCache.put(key, value);
        deleteL1Cache(key);
    }

    @Override
    public ValueWrapper putIfAbsent(Object keyObj, Object value) {
        String key = String.valueOf(keyObj);
        if (log.isDebugEnabled()) {
            log.debug("ValueWrapper putIfAbsent 设置缓存 name:{}, key:{}, value:{}", name, key, gson.toJson(value));
        }
        ValueWrapper ret = redisCache.putIfAbsent(key, value);
        deleteL1Cache(key);
        return ret;
    }

    private void deleteL1Cache(Object keyObj) {
        String key = String.valueOf(keyObj);
        Map<String, String> message = new HashMap<>();
        message.put("n", name);
        message.put("k", key);
        l1CacheSynchronizer.pushMsg(gson.toJson(message));

        if (log.isDebugEnabled()) {
            log.debug("通知删除一级缓存 name:{}, key:{}", name, key);
        }
    }

    @Override
    public void evict(Object keyObj) {
        String key = String.valueOf(keyObj);
        // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
        redisCache.evict(key);
        if (log.isDebugEnabled()) {
            log.debug("二级缓存已删除，开始通知删除一级缓存 name:{}, key:{}", name, key);
        }
        deleteL1Cache(key);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear操作会调用redis keys查询，可能引起redis性能问题，暂不支持！");
    }

    private <T> Object getFromSecondaryCache(Object keyObj, Callable<T> valueLoader) {
        String key = String.valueOf(keyObj);
        T value = redisCache.get(key, valueLoader);
        if (log.isDebugEnabled()) {
            log.debug("getFromSecondaryCache查询二级缓存 name:{}, key:{},返回值是:{}", name, key, gson.toJson(value));
        }
        return toStoreValue(value);
    }

    public void evictAll(Collection<Object> keys) {
        redisCache.evictAll(keys);

        //delete Level1 cache
        List<String> syncMsgs = new ArrayList<>();
        for (Object key : keys) {
            Map<String, String> message = new HashMap<>();
            message.put("n", name);
            message.put("k", String.valueOf(key));
            syncMsgs.add(gson.toJson(message));
        }
        l1CacheSynchronizer.pushMsg(syncMsgs);
    }

    public void putAll(Map<Object, Object> map) {
        redisCache.putAll(map);

        List<String> syncMsgs = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Map<String, String> message = new HashMap<>();
            message.put("n", name);
            message.put("k", String.valueOf(entry.getKey()));
            syncMsgs.add(gson.toJson(message));
        }
        l1CacheSynchronizer.pushMsg(syncMsgs);
    }
}

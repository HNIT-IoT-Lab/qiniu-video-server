package cn.hnit.starter.cache.synchronizer;

import cn.hnit.starter.cache.L1CacheSynchronizer;
import cn.hnit.starter.cache.core.RedisCaffeineCache;
import cn.hnit.starter.cache.listener.CacheQueueMessageListener;
import cn.hnit.starter.cache.manager.RedisCaffeineCacheManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 一级缓存（本地缓存）同步器，基于redis pub/sub + push/pop推拉结合实现的一级缓存（本地缓存）同步器
 *
 * @author 梁峰源
 * @since 2022/10/2 11:32
 */
@Slf4j
public class L1CacheRedisSynchronizer implements L1CacheSynchronizer {
    private RedisMessageListenerContainer container;
    private final Gson gson = new Gson();
    private final StringRedisTemplate redisTemplate;
    private final String topicKey;
    private final String queueKey;
    private final RedisCaffeineCacheManager cacheManager;
    private static final AtomicLong OFFSET = new AtomicLong(0);
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    public L1CacheRedisSynchronizer(RedisCaffeineCacheManager cacheManager, StringRedisTemplate redisTemplate, String namespace) {
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
        this.topicKey = "RedisCaffeineCache:Channel:" + namespace;
        this.queueKey = "RedisCaffeineCache:Queue:" + namespace;
    }

    @Override
    public void start() {
        // 同步最新offset
        syncOffset();
        // 启动监听
        setupListener();
        // 定时拉取
        startPullTask();
        resetQueueTask();
    }

    private void startPullTask() {
        executor.scheduleWithFixedDelay(() -> {
            try {
                pullMsg(false);
            } catch (Exception e) {
                log.error("pullMsg清除一级缓存异常", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void resetQueueTask() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long initialDelay = System.currentTimeMillis() - cal.getTimeInMillis();
        initialDelay = initialDelay > 0 ? initialDelay : 0;
        // 每天晚上凌晨3:00执行任务
        executor.scheduleWithFixedDelay(() -> {
            try {
                redisTemplate.delete(queueKey);
                OFFSET.getAndSet(-1);
            } catch (Exception e) {
                log.error("error@resetQueueTask", e);
            }
        }, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    private void setupListener() {
        container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(new MessageListenerAdapter(new CacheQueueMessageListener(this)),
                new ChannelTopic(topicKey));
        container.afterPropertiesSet();
        container.start();
    }

    @Override
    public void pushMsg(String msg) {
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.opsForList().leftPush(queueKey, msg);
                operations.expire(queueKey, 25, TimeUnit.HOURS);
                operations.convertAndSend(topicKey, "1");
                return null;
            }
        });
    }

    @Override
    public void pushMsg(List<String> msgs) {
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                for (String msg : msgs) {
                    operations.opsForList().leftPush(queueKey, msg);
                }
                operations.expire(queueKey, 25, TimeUnit.HOURS);
                operations.convertAndSend(topicKey, "1");
                return null;
            }
        });
    }

    @Override
    public void pullMsg(boolean realtime) {
        long maxOffset = redisTemplate.opsForList().size(queueKey);
        if (maxOffset == 0) {
            return;
        }
        long oldOffset = OFFSET.getAndSet(maxOffset);
        if (oldOffset >= maxOffset) {
            return;
        }
        List<String> msgs = redisTemplate.opsForList().range(queueKey, 0, -oldOffset - 1);
        if (CollectionUtils.isEmpty(msgs)) {
            return;
        }

        for (String msg : msgs) {
            try {
                JsonObject root = gson.fromJson(msg, JsonObject.class);
                if (root.has("n") && root.has("k")) {
                    String key = root.get("k").getAsString();
                    String name = root.get("n").getAsString();
                    if (log.isDebugEnabled()) {
                        log.debug("[{}同步]调用一级缓存删除消息：name:{}, key:{}", realtime ? "实时" : "定时", name, key);
                    }
                    ((RedisCaffeineCache) cacheManager.getCache(name)).evictL1Cache(key);
                }
            } catch (Exception e) {
                log.error("error@处理缓存队列消息:" + msg, e);
            }
        }
    }

    private void syncOffset() {
        long maxOffset = redisTemplate.opsForList().size(queueKey) - 1;
        if (maxOffset < 0) {
            return;
        }
        OFFSET.getAndSet(maxOffset > 0 ? maxOffset : 0);
    }

}

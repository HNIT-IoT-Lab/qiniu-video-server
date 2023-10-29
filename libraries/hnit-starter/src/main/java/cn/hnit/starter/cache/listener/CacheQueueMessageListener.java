package cn.hnit.starter.cache.listener;

import cn.hnit.starter.cache.L1CacheSynchronizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * Redis 发布订阅（ pub/sub ）消息监听实现类
 *
 * @author 梁峰源
 * @since 2022/10/2 12:01
 */
@Slf4j
public class CacheQueueMessageListener implements MessageListener {

    private final L1CacheSynchronizer synchronizer;

    public CacheQueueMessageListener(L1CacheSynchronizer synchronizer){
        this.synchronizer = synchronizer;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            synchronizer.pullMsg(true);
        }catch (Exception e){
            log.error("error@cacheManager.pullMsg", e);
        }
    }
}

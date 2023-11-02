package com.qiniu.video.service.common;

import cn.hnit.common.redis.operator.RedisOperator;
import cn.hnit.utils.context.UserContext;
import com.qiniu.video.dao.MessageDao;
import com.qiniu.video.entity.model.Message;
import com.qiniu.video.gpt.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/5/7 15:53
 */
@Slf4j
@Service
@EnableAsync
@EnableScheduling
public class MessageCache {

    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RedisOperator redisOperator;

    private static final String MESSAGE_SESSION_ID = "chatUserId:%d";

    private Map<Long, List<Message>> cacheMessage;


    public List<ChatMessage> allMessage(final Message message) {
        return cacheMessage.compute(UserContext.getUserId(), (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            v.add(message);
            return v;
        }).stream().map(ChatMessage::new).collect(Collectors.toList());
    }

    @Async
    @Scheduled(cron = "*/10 * * * * ?")
    public void synchronizeMessage2db() {
        cacheMessage.forEach((userId, messageList) -> {
            if (messageList.isEmpty()) {
                return;
            }
            long count = messageDao.count(Criteria.where(Message.Fields.userId).is(userId));
            if (messageList.size() - count <= 0) {
                // 说明没有数据需要缓存
                log.info("用户【{}】不需要缓存", userId);
                return;
            }
            messageDao.save(messageList.subList(Math.max((int) count, 0), messageList.size()));
            log.info("用户【{}】缓存【{}】条历史数据", userId, messageList.size() - (int) count);
        });
    }


    @PostConstruct
    public void initMessage() {
        cacheMessage = messageDao.findAllIncludeDeleted()
                .stream()
                .filter(e -> Objects.nonNull(e.getUserId()))
                .collect(Collectors.groupingBy(Message::getUserId, ConcurrentHashMap::new, Collectors.toList()));
    }

    public void put(Long userId) {
        redisOperator.set(String.format(MESSAGE_SESSION_ID, userId), userId.toString(), 60 * 5);
    }

    public boolean containsKey(Long userId) {
        return redisOperator.has(String.format(MESSAGE_SESSION_ID, userId));
    }

    public void remove(Long userId) {
        redisOperator.del(String.format(MESSAGE_SESSION_ID, userId));
    }
}

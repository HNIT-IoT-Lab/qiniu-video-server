package com.qiniu.video.dao;

import cn.hnit.common.redis.operator.RedisOperator;
import com.qiniu.video.BaseTest;
import com.qiniu.video.entity.model.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/5/8 23:16
 */
@Slf4j
public class TestSave extends BaseTest {

    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RedisOperator redisOperator;

    private final Object obj = new Object();


    @Test
    @SneakyThrows
    public void testSave() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                synchronized (obj) {
                    List<Message> list = new ArrayList<>();
                    for (int j = 0; j < 100; j++) {
                        Message message = Message.of("你好" + j);
                        message.setUserId((long) -1);
                        list.add(message);
                    }
                    messageDao.save(list);
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }
}

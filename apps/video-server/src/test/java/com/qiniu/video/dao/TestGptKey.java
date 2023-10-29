package com.qiniu.video.dao;

import com.qiniu.video.BaseTest;
import com.qiniu.video.entity.GptKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/30 19:48
 */
@Slf4j
public class TestGptKey extends BaseTest {
    @Autowired
    private GptKeyDao gptKeyDao;

    @Test
    public void test01() {
        GptKey save = gptKeyDao.save(GptKey.builder().key("xxx").build());
        log.info("{}", save);
    }
}

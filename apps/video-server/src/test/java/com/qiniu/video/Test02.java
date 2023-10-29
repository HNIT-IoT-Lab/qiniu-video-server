package com.qiniu.video;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.Test;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/30 18:40
 */
@Slf4j
public class Test02 {


    @Test
    public void test01() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        log.info("{}", client);
    }
}

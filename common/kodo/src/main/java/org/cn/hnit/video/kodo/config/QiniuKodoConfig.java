package org.cn.hnit.video.kodo.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QiniuKodoConfig implements InitializingBean {

    @Value("${qiniu.kodo.accessKey}")
    String accessKey;
    @Value("${qiniu.kodo.secretKey}")
    String secretKey;
    @Value("${qiniu.kodo.bucket}")
    String bucketName;
    @Value("${qiniu.kodo.domain}")
    String domain;

    public static String DOMAIN;
    public static String ACCESS_KEY;
    public static String SECRET_KEY;
    public static String BUCKET_NAME;

    // 在所有属性初始化后将执行
    @Override
    public void afterPropertiesSet() {
        DOMAIN = domain;
        ACCESS_KEY = accessKey;
        SECRET_KEY = secretKey;
        BUCKET_NAME = bucketName;
    }
}

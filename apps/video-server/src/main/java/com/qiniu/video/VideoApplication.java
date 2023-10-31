package com.qiniu.video;

import cn.hnit.starter.annotation.EnableWebFilter;
import com.qiniu.video.es.service.EsArticleService;
import com.spring4all.mongodb.EnableMongoPlus;
import org.cn.hnit.video.kodo.service.impl.QiniuKodoServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/27 23:10
 */
@SpringBootApplication
@EnableMongoPlus
@EnableWebFilter
@Import({QiniuKodoServiceImpl.class, EsArticleService.class})
public class VideoApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
    }
}

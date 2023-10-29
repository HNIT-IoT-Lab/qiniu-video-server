package com.qiniu.video;

import cn.hnit.starter.annotation.EnableWebFilter;
import com.spring4all.mongodb.EnableMongoPlus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/26 22:50
 */
@SpringBootApplication
@EnableMongoPlus
@EnableWebFilter
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}

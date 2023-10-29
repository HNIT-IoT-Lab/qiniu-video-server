package com.qiniu.video.config;

import cn.hnit.utils.threadutil.ThreadPoolUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collector;

/**
 * 线程池配置类
 *
 * @author VernHe
 * @date 2021年12月31日 22:28
 */
@Getter
@Setter
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    /**
     * 核心线程数（默认线程数）
     */
    @Value("#{T(java.lang.Runtime).getRuntime().availableProcessors() + 1}")
    private int corePoolSize;
    /**
     * 最大线程数
     */
    @Value("#{T(java.lang.Runtime).getRuntime().availableProcessors() * 2}")
    private int maxPoolSize;
    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    @Value("${task.pool.keepAliveSeconds}")
    private int keepAliveSeconds;
    /**
     * 缓冲队列大小
     */
    @Value("${task.pool.queueCapacity}")
    private int queueCapacity;
    /**
     * 线程池名前缀
     */
    @Value("${task.pool.threadNamePrefix}")
    private String threadNamePrefix;

    @Bean("taskExecutor")
    public ThreadPoolExecutor taskExecutor() {
        return ThreadPoolUtil.build(threadNamePrefix, corePoolSize, maxPoolSize, new ArrayBlockingQueue<>(queueCapacity));
    }
}

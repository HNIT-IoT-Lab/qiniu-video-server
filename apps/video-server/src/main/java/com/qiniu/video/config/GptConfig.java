package com.qiniu.video.config;

import cn.hutool.core.collection.CollUtil;
import com.qiniu.video.entity.model.GptKey;
import com.qiniu.video.gpt.ChatGPT;
import com.qiniu.video.gpt.ChatGPTStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;
import java.net.Proxy;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/30 15:25
 */
@Slf4j
@Configuration
public class GptConfig {

    @Value("${openAi.timeout}")
    private Integer GPT_TIME_OUT;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Proxy proxy = Proxy.NO_PROXY;
    private List<String> ketList;


    @PostConstruct
    public void setGPT_KEY() {
        List<GptKey> all = mongoTemplate.findAll(GptKey.class);
        if (CollUtil.isEmpty(all)) {
            log.error(">>>>>>>>>>>>>>>【have no gpt key】<<<<<<<<<<<<<<<<<<<");
            // System.exit(1);
        }
        ketList = all.stream().map(GptKey::getKey).collect(Collectors.toList());
    }

    /**
     * 流式输出的GPT
     */
    @Bean
    public ChatGPTStream gptStream() {
        return ChatGPTStream.builder()
                .apiKeyList(ketList)
                .proxy(proxy)
                .timeout(GPT_TIME_OUT)
                .build()
                .init();
    }

    /**
     * 问答式的gpt 注意可能会超时，需设置nginx等反向代理的超时时间
     */
    @Bean
    public ChatGPT chatGPT() {
        return ChatGPT.builder()
                .apiKeyList(ketList)
                .proxy(proxy)
                .timeout(GPT_TIME_OUT)
                .build()
                .init();
    }
}

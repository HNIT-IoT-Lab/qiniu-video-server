package com.qiniu.video;

import com.qiniu.video.gpt.ChatGPT;
import com.qiniu.video.gpt.billing.CreditGrantsResponse;
import com.qiniu.video.gpt.chat.ChatCompletion;
import com.qiniu.video.gpt.chat.ChatCompletionResponse;
import com.qiniu.video.entity.model.Message;
import com.qiniu.video.gpt.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;

import java.net.Proxy;
import java.util.Arrays;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/30 15:18
 */
@Slf4j
public class Test {
    private ChatGPT chatGPT;

    @Before
    public void before() {
        chatGPT = ChatGPT.builder()
                .apiKey("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")
                .timeout(900)
                .proxy(Proxy.NO_PROXY)
                .apiHost("https://api.openai.com/") //代理地址
                .build()
                .init();

        CreditGrantsResponse response = chatGPT.creditGrants();
        log.info("余额：{}", response.getTotalAvailable());
    }

    @org.junit.Test
    public void chat() {
        ChatMessage system = ChatMessage.ofSystem("你现在是一个诗人，专门写七言绝句");
        ChatMessage message = ChatMessage.of("写一段七言绝句诗，题目是：火锅！");

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .messages(Arrays.asList(system, message))
                .maxTokens(3000)
                .temperature(0.9)
                .build();
        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();
        System.out.println(res);
    }

    @org.junit.Test
    public void chatmsg() {
        String res = chatGPT.chat("写一段七言绝句诗，题目是：火锅！");
        System.out.println(res);
    }

}

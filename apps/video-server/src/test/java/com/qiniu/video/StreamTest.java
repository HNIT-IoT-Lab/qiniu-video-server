package com.qiniu.video;

import com.qiniu.video.gpt.ChatGPTStream;

import com.qiniu.video.gpt.chat.ChatCompletion;
import com.qiniu.video.gpt.chat.ChatMessage;
import com.qiniu.video.gpt.listener.ConsoleStreamListener;
import com.qiniu.video.gpt.listener.SseStreamListener;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.Proxy;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * 测试类
 *
 * @author plexpt
 */
public class StreamTest {

    private ChatGPTStream chatGPTStream;

    @Before
    public void before() {
        Proxy proxy = Proxy.NO_PROXY;

        chatGPTStream = ChatGPTStream.builder()
                .apiKey("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")
                .proxy(proxy)
                .timeout(600)
                .apiHost("https://api.openai.com/")
                .build()
                .init();

    }


    @Test
    public void chatCompletions() {
        ConsoleStreamListener listener = new ConsoleStreamListener();
        ChatMessage message = ChatMessage.of("写一段七言绝句诗，题目是：火锅！");
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(Arrays.asList(message))
                .build();
        chatGPTStream.streamChatCompletion(chatCompletion, listener);

        //卡住测试
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/chat/sse")
    @CrossOrigin
    public SseEmitter sseEmitter(String prompt) {

        SseEmitter sseEmitter = new SseEmitter(-1L);

        SseStreamListener listener = new SseStreamListener(sseEmitter);
        ChatMessage message = ChatMessage.of(prompt);

        listener.setOnComplate(msg -> {
            //回答完成，可以做一些事情
        });
        chatGPTStream.streamChatCompletion(Arrays.asList(message), listener);


        return sseEmitter;
    }

}

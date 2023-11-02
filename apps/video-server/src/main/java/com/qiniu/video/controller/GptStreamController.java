package com.qiniu.video.controller;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.starter.annotation.ResponseIgnore;
import cn.hnit.utils.context.UserContext;
import com.qiniu.video.gpt.ChatGPTStream;
import com.qiniu.video.entity.model.Message;
import com.qiniu.video.gpt.listener.SseStreamListener;
import com.qiniu.video.service.common.MessageCache;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;

import static cn.hutool.core.collection.CollUtil.newHashSet;

/**
 * 流式回答 @see springboot SseEmitter
 *
 * @author king
 * @since 2023/10/30 14:53
 */
@Slf4j
@Controller
@RequestMapping("/gpt/stream")
public class GptStreamController {

    @Autowired
    private ChatGPTStream chatGPTStream;
    @Autowired
    private MessageCache messageCache;



    @GetMapping(value = "/chat/ssechat", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @ResponseIgnore
    public SseEmitter sseEmitter(String prompt) {
        // 记录正在发送消息的用户，不允许同一时间有多个连接
        if (messageCache.containsKey(UserContext.getUserId())) {
            throw AppException.pop("一个账号不能同时聊天");
        }
        messageCache.put(UserContext.getUserId());
        // 不设置超时时间
        SseEmitter sseEmitter = new SseEmitter(-1L);
        SseStreamListener listener = new SseStreamListener(sseEmitter);
        Message message = Message.of(prompt);
        message.setUserId(UserContext.getUserId());
        listener.setOnComplate(msg -> {
            // 回答完成，可以做一些事情
            log.info("用户【{}】本次消息结束", UserContext.getUserId());
            messageCache.remove(UserContext.getUserId());
        });
        chatGPTStream.streamChatCompletion(messageCache.allMessage(message), listener);
        return sseEmitter;
    }

    private void test() {
        Set<Integer> setA = newHashSet(1, 2, 3, 4, 5);
        Set<Integer> setB = newHashSet(4, 5, 6, 7, 8);

        Sets.SetView<Integer> union = Sets.union(setA, setB);
        System.out.println("union:");
        for (Integer integer : union)
            System.out.println(integer);           //union 并集:12345867

        Sets.SetView<Integer> difference = Sets.difference(setA, setB);
        System.out.println("difference:");
        for (Integer integer : difference)
            System.out.println(integer);        //difference 差集:123

        Sets.SetView<Integer> intersection = Sets.intersection(setA, setB);
        System.out.println("intersection:");
        for (Integer integer : intersection)
            System.out.println(integer);  //intersection 交集:45

    }
}

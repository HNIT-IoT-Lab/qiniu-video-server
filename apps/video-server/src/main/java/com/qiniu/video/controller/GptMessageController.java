package com.qiniu.video.controller;

import com.qiniu.video.gpt.ChatGPT;
import com.qiniu.video.gpt.chat.ChatCompletion;
import com.qiniu.video.gpt.chat.ChatCompletionResponse;
import com.qiniu.video.entity.model.Message;
import com.qiniu.video.gpt.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 以消息的形式发送，可能会等待较长时间
 *
 * @author king
 * @since 2023/10/30 14:53
 */
@RestController
@RequestMapping("/gpt/message")
public class GptMessageController {
    @Autowired
    private ChatGPT chatGPT;

    @PostMapping("/chat")
    @CrossOrigin
    public Message chat(String prompt) {
        ChatMessage message = ChatMessage.of(prompt);
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .messages(Arrays.asList(message))
                .maxTokens(3000)
                .temperature(0.9)
                .build();
        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        return response.getChoices().get(0).getMessage();
    }
}

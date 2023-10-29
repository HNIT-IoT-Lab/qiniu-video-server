package com.qiniu.video.gpt.chat;

import com.qiniu.video.entity.Message;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/5/8 22:30
 */
@Data
public class ChatMessage {
    private String role;
    private String content;

    public ChatMessage(Message message) {
        this.role = message.getRole();
        this.content = message.getContent();
    }

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static ChatMessage of(String content) {
        return new ChatMessage(Message.Role.USER.getValue(), content);
    }

    public static ChatMessage ofSystem(String content) {

        return new ChatMessage(Message.Role.SYSTEM.getValue(), content);
    }
}

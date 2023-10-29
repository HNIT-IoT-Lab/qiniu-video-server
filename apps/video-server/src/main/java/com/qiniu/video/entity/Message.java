package com.qiniu.video.entity;

import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author plexpt
 */
@Data
@Builder
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = Message.COLLECTION_NAME)
public class Message  extends BaseEntity {
    private static final long serialVersionUID = -4628323123122124748L;
    public static final String COLLECTION_NAME = "history_message";


    /**
     * 目前支持三种角色参考官网，进行情景输入：<a href="https://platform.openai.com/docs/guides/chat/introduction">...</a>
     */
    private String role;
    private String content;
    private Long userId;
    private String chatRoom;
    private LocalDateTime sendTime;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
        this.sendTime = LocalDateTime.now();
    }

    public static Message of(String content) {

        return new Message(Role.USER.getValue(), content);
    }

    public static Message ofSystem(String content) {

        return new Message(Role.SYSTEM.getValue(), content);
    }

    public static Message ofAssistant(String content) {

        return new Message(Role.ASSISTANT.getValue(), content);
    }
    
    @Getter
    @AllArgsConstructor
    public enum Role {

        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        ;
        private String value;
    }

}

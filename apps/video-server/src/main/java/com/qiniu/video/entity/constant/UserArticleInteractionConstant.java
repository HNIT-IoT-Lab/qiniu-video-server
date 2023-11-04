package com.qiniu.video.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface UserArticleInteractionConstant {
    @AllArgsConstructor
    @Getter
    enum InteractionType {
        READ(0,"观看"),
        LIKE(1,"点赞"),
        COLLECTION(2,"收藏");

        private final Integer code;
        private final String name;
    }

}

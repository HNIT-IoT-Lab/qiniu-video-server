package com.qiniu.video.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InteractionTypeEnum {
    READ(0,"观看"),
    LIKE(1,"点赞"),
    COLLECTION(2,"收藏");

    private Integer code;
    private String name;
}

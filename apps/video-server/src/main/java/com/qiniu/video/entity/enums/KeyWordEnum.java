package com.qiniu.video.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KeyWordEnum {
    SCENERY(0,"风景"),
    MOVIE(1,"电影"),
    SPORT(2,"体育");

    private Integer code;
    private String name;

}

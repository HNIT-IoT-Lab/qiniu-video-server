package com.qiniu.video.entity.dto;

import lombok.Data;

/**
 * 关注、粉丝、点赞
 */
@Data
public class BehaviorDataDto {
    //关注
    private Integer attention;
    //粉丝
    private Integer fans;
    //获赞
    private Integer start;
}

package com.qiniu.video.entity.vo;

import com.qiniu.video.entity.dto.BehaviorDataDto;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/29 14:27
 */
@Data
public class UserVO {
    private String userName;
    private String avatar;
    private String description;
    private String gender;
    //关注、粉丝、获赞
    private BehaviorDataDto behaviorData;
}

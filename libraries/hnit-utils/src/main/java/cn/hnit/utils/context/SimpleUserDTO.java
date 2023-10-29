package cn.hnit.utils.context;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SimpleUserDTO implements Serializable {


    /**
     * 不鸽号
     */
    private String userNumber;


    /**
     * 用户名字
     */
    private String userName;


    /**
     * 用户头像
     */
    @Deprecated
    private String userIcon;

    /**
     * 用户头像
     */
    private String avatarUrl;


    /**
     * 额外信息
     */
    private String extra;


    /**
     * 用户id
     */
    private Long userId;


    /**
     * 是否在线
     */
    private boolean online;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;
    /**
     * 靓号id
     */
    private Long niceId;


    private Date createTime;

    /**
     * 魅力等级
     */
    private Integer charmLevel;

    /**
     * 魅力经验值
     */
    private Long charmVal;

    /**
     * 下一等级魅力经验值
     */
    private Long nextCharmLevelVal;
    /**
     * //是否萌新
     */
    private Integer newLabel;
    /**
     *
     */
    private Integer nobleLevel;
    /**
     * 财富等级隐藏开关
     */
    private Boolean wealth;

    private Integer wealthLevel;

    private String wealthName;
    /**
     *  财富等级
     */
    private Long richLevel;

    /**
     * 手机号
     */
    @JsonIgnore
    private String phone;
}

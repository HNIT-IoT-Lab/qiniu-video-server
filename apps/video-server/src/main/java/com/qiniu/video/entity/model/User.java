package com.qiniu.video.entity.model;

import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/27 1:01
 */
@Data
@Builder
@FieldNameConstants
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = User.COLLECTION_NAME)
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -46288008014124748L;
    public static final String COLLECTION_NAME = "user";

    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 手机号
     */
    private String phoneNumber;
}

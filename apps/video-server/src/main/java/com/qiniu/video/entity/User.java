package com.qiniu.video.entity;

import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

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
public class User extends BaseEntity {
    private static final long serialVersionUID = -46288008014124748L;
    public static final String COLLECTION_NAME = "user";

    private String userName;
    private String password;
    private String avatar;
    private String phoneNumber;
}

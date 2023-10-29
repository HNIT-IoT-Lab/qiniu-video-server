package com.qiniu.video.entity;

import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Document(collection = User.COLLECTION_NAME)
public class User extends BaseEntity {
    private static final long serialVersionUID = -46288008014124748L;
    public static final String COLLECTION_NAME = "user";

    private String userName;

    private String password;

}

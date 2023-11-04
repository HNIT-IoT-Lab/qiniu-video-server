package com.qiniu.video.entity.model;

import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Builder
@FieldNameConstants
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = Follow.COLLECTION_NAME)
public class Follow extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -46288008014124748L;
    public static final String COLLECTION_NAME = "follow";

    /**
     * 用户Id
     */
    private String userId;
    /**
     * 被关注用户Id
     */
    private String followUserId;
}

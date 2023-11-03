package com.qiniu.video.entity.model;

import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import com.qiniu.video.entity.enums.InteractionTypeEnum;
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
@Document(collection = User.COLLECTION_NAME)
public class UserArticleInteraction extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -46288008014124748L;
    public static final String COLLECTION_NAME = "userArticleInteraction";

    /**
     * 用户Id
     */
    private String userId;
    /**
     * 文章Id
     */
    private String articleId;
    /**
     * 交互类型（如阅读、点赞、收藏等）
     */
    private String interactionType;
    /**
     * 交互时间
     */
    private String interactionTime;

}

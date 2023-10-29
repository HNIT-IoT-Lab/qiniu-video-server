package com.qiniu.video.entity;

import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * gpt访问的key
 *
 * @author king
 * @since 2023/10/30 19:37
 */
@Data
@Builder
@FieldNameConstants
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = GptKey.COLLECTION_NAME)
public class GptKey extends BaseEntity {
    private static final long serialVersionUID = -462876968014124748L;
    public static final String COLLECTION_NAME = "gpt_key";

    private String key;
}

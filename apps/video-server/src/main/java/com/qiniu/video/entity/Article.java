package com.qiniu.video.entity;

import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import com.qiniu.video.entity.constant.UserFileConstant;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@FieldNameConstants
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = Article.COLLECTION_NAME)
public class Article extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4628802124748L;
    public static final String COLLECTION_NAME = "article";

    private String title;
    private String author;
    private Long uid;
    private String content;
    // 图片或者视频的url，视频只能有一个 图片可以多张
    private List<String> urlList;

    private UserFileConstant.UserFileKind articleKind;
}

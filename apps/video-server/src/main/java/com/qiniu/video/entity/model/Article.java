package com.qiniu.video.entity.model;

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

    /**
     * 标题
     */
    private String title;
    /**
     * 作者
     */
    private String author;
    /**
     * 关键词可能有多个
     */
    private String keyWord;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 内容
     */
    private String content;
    /**
     * 图片或者视频的url，视频只能有一个 图片可以多张
     */
    private List<String> urlList;
    /**
     * 视频封面
     */
    private String cover;
    /**
     * 种类
     */
    private UserFileConstant.UserFileKind articleKind;
    /**
     * 是否点赞
     */
    private Boolean isLike;
    /**
     * 点赞数量
     */
    private Integer likeCounts;
    /**
     * 是否收藏
     */
    private Boolean isCollect;
    /**
     * 收藏数量
     */
    private Integer collectionCounts;
}

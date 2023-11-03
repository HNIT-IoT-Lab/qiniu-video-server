package com.qiniu.video.entity.model;


import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import com.qiniu.video.entity.constant.UserFileConstant;
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
@Document(collection = UserFile.COLLECTION_NAME)
public class UserFile extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -46288008014124748L;
    public static final String COLLECTION_NAME = "user_files";

    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 如果是视频会有封面 url + ?vframe/jpg/offset/1
     */
    private String cover;
    /**
     * 文件类型
     */
    private UserFileConstant.UserFileKind fileKind;
}

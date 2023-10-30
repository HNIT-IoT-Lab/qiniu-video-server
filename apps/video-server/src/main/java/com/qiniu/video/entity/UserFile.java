package com.qiniu.video.entity;


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

    private Long userId;
    private String filePath;
    // 如果是视频会有封面 url + ?vframe/jpg/offset/1
    private String Cover;
    private UserFileConstant.UserFileKind fileKind;
}

package com.qiniu.video.entity.req;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class ArticleReq {

    @NotEmpty(message = "标题不能为空")
    private String title;
    @NotEmpty(message = "内容不能为空")
    private String content;
}

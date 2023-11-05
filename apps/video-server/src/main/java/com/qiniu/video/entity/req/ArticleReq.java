package com.qiniu.video.entity.req;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
public class ArticleReq {

    @NotEmpty(message = "标题不能为空")
    private String title;
    @NotEmpty(message = "内容不能为空")
    private String content;
    @NotEmpty(message = "关键词不能为空")
    private String keyWord;
}

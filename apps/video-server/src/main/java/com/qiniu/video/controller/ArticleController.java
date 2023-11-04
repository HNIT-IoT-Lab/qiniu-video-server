package com.qiniu.video.controller;


import cn.hnit.sdk.orm.mongodb.entity.PageVO;
import com.qiniu.video.entity.model.Article;
import com.qiniu.video.entity.req.ArticleReq;
import com.qiniu.video.es.entity.EsArticle;
import com.qiniu.video.service.ArticleService;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 视频文章类
 */
@Validated
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 上传文章或视频
     * @param file
     * @param title
     * @param content
     * @return
     */
    @PostMapping("/addArticle")
    public Article addArticle(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") @NotBlank(message = "标题不能为空") String title,
            @RequestParam("content") @NotBlank(message = "内容不能为空") String content) {

        return articleService.AddArticle(file, ArticleReq.builder()
                .content(content)
                .title(title)
                .build());
    }
    /**
     * 搜索文章或视频
     * @param keyword  关键词
     * @return
     */
    @GetMapping("/search")
    public SearchHits<EsArticle> searchArticle(@RequestParam("keyword") @NotBlank(message = "关键字不能为空") String keyword) {
        return articleService.search(keyword);
    }

    /**
     * 视频播放：获取视频URL
     */
    @GetMapping("/getVideoUrl")
    public Article getVideoUrl(){
        return articleService.getVideoUrl();
    }

    /**
     * @TODO
     * 视频截帧功能实现:
     * 视频播放时鼠标放在视频的进度条上，就会将视频的第几秒作为参数传过来，我们生成图片返回即可
     */

    /**
     *  获取文章内容：做分页
     *  只需要传当前页和每页大小就可
     */
    @GetMapping("/getArticleList")
    public List<Article> getArticleList(@RequestBody PageVO pageVo) {
        return articleService.getArticleList(pageVo);
    }

    /**
     * 热门视频推荐:
     * 1、协同过滤算法
     * 2、LRU最近最少使用算法
     * 做分页
     */
    @GetMapping("/getHotArticle")
    public List<Article> getHotArticle(){
        return articleService.getHotArticle();
    }

    /**
     *
     */

}

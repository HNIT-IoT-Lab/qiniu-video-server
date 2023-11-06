package com.qiniu.video.controller;


import cn.hnit.common.page.Page;
import cn.hnit.sdk.orm.mongodb.entity.PageVO;
import com.qiniu.video.entity.model.Article;
import com.qiniu.video.entity.model.UserArticleInteraction;
import com.qiniu.video.entity.req.ArticleReq;
import com.qiniu.video.es.entity.EsArticle;
import com.qiniu.video.service.ArticleService;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.http.POST;

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
            @RequestParam("content") @NotBlank(message = "内容不能为空") String content,
            @RequestParam("keyWord") @NotBlank(message = "关键词不能为空") String keyWord) {

        return articleService.AddArticle(file, ArticleReq.builder()
                .content(content)
                .title(title)
                .keyWord(keyWord)
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
     * 视频切换：获取视频URL
     * 每次返回3条数据
     */
    @GetMapping("/getVideoUrl")
    public List<Article> getVideoUrl(){
        return articleService.getVideoUrl();
    }


    /**
     *  获取文章内容：做分页
     *  只需要传当前页和每页大小就可
     */
    @PostMapping("/getArticleList")
    public Page<Article> getArticleList(@RequestBody PageVO pageVo) {
        return articleService.getArticleList(pageVo);
    }

    /**
     * 热门视频推荐:
     * 1、协同过滤算法
     * 2、LRU最近最少使用算法
     */
    @GetMapping("/getHotArticle")
    public List<Article> getHotArticle(){
        return articleService.getHotArticle();
    }

    /**
     * 点赞、收藏
     * 将当前视频id传过来就行，以及类型
     */
    @PostMapping("/starOrCollectArticle")
    public UserArticleInteraction starOrCollectArticle(@RequestParam @NotNull String articleId , String type){
         return articleService.starArticle(articleId,type);
    }

    /**
     * 获取收藏视频列表
     */
    @PostMapping("/getCollectArticle")
    public List<Article> getCollectArticle(){
        return articleService.getCollectArticle();
    }

    /**
     * 获取视频分类列表：比如体育频道
     * 根据关键词去过滤，前端只需要传过来一个tag标签，然后去做模糊查询即可
     */

}

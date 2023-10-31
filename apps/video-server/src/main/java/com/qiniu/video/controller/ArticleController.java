package com.qiniu.video.controller;


import com.qiniu.video.entity.Article;
import com.qiniu.video.entity.req.ArticleReq;
import com.qiniu.video.es.entity.EsArticle;
import com.qiniu.video.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Validated
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public Article addArticle(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") @NotBlank(message = "标题不能为空") String title,
            @RequestParam("content") @NotBlank(message = "内容不能为空") String content) {

        return articleService.AddArticle(file, ArticleReq.builder()
                .content(content)
                .title(title)
                .build());
    }

    @GetMapping("/search")
    public SearchHits<EsArticle> searchArticle(@RequestParam("keyword") @NotBlank(message = "关键字不能为空") String keyword) {
        return articleService.search(keyword);
    }
}

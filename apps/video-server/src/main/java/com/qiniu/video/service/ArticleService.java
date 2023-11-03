package com.qiniu.video.service;

import com.qiniu.video.entity.model.Article;
import com.qiniu.video.entity.req.ArticleReq;
import com.qiniu.video.es.entity.EsArticle;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {


    Article AddArticle(MultipartFile file, ArticleReq req);

    SearchHits<EsArticle> search(String keyword);

    Article getVideoUrl();

    List<Article> getArticleList();

}

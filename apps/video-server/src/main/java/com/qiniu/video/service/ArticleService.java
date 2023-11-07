package com.qiniu.video.service;

import cn.hnit.common.page.Page;
import cn.hnit.sdk.orm.mongodb.entity.PageVO;
import com.qiniu.video.entity.model.Article;
import com.qiniu.video.entity.model.UserArticleInteraction;
import com.qiniu.video.entity.req.ArticleReq;
import com.qiniu.video.es.entity.EsArticle;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {


    Article AddArticle(MultipartFile file, ArticleReq req);

    SearchHits<EsArticle> search(String keyword);

    List<Article> getVideoUrl(String tag);

    Page<Article> getArticleList(PageVO pageVo);

    List<Article> getHotArticle();

    UserArticleInteraction starArticle(String articleId, String type,Boolean flag);

    List<Article> getCollectArticle();
}

package com.qiniu.video.service.impl;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.utils.context.UserContext;
import com.qiniu.video.dao.ArticleDao;
import com.qiniu.video.entity.model.Article;
import com.qiniu.video.entity.constant.UserFileConstant;
import com.qiniu.video.entity.req.ArticleReq;
import com.qiniu.video.es.entity.EsArticle;
import com.qiniu.video.es.service.EsArticleService;
import com.qiniu.video.service.ArticleService;
import com.qiniu.video.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private UserService userService;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private EsArticleService esArticleService;
    @Autowired
    private ThreadPoolExecutor asyncExecutor;

    @Override
    public Article AddArticle(MultipartFile file, ArticleReq req) {
        String upload;
        if (file != null) {
            //上传到kodo，拿到url，存到mongo
            upload = userService.upload(file);
        } else {
            upload = "";
        }
        Future<Article> f1 = asyncExecutor.submit(() -> {
            // 保存到mongo
            return articleDao.save(Article.builder()
                    .uid(UserContext.getUserId())
                    .title(req.getTitle())
                    .content(req.getContent())
                    .articleKind(GenKind(upload))
                    .build());
        });
        Future<?> f2 = asyncExecutor.submit(() -> {
            // 保存到es
            esArticleService.addArticle(EsArticle.builder()
                    .author(userService.FindById(UserContext.getUserId()).getUserName())
                    .title(req.getTitle())
                    .content(req.getContent())
                    .createTime(LocalDateTime.now())
                    .build());
        });

        Article article;

        try {
            article = f1.get();
            f2.get();
        } catch (Exception e) {
            log.error("AddArticle fail {}", e.getMessage());
            throw AppException.pop("保存失败");
        }
        return article;
    }

    private UserFileConstant.UserFileKind GenKind(String filePath) {
        UserFileConstant.UserFileKind kind = UserFileConstant.UserFileKind.of(filePath);
        return kind == null ? UserFileConstant.UserFileKind.DOCUMENT : kind;
    }


    public SearchHits<EsArticle> search(String keyword) {
        return esArticleService.searchArticle(keyword);
    }
}

package com.qiniu.video.es.service;


import com.qiniu.video.es.entity.EsArticle;
import com.qiniu.video.es.repo.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@Service
public class EsArticleService {
    @Autowired
    private ArticleRepository articleRepo;

    /**
     * 保存文档
     */
    public void addArticle(EsArticle article) {
        try {
            articleRepo.save(article);
            log.info("Article saved: {}", article);
        } catch (Exception e) {
            log.error("Failed to add article: {}", article, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 更加关键字从标题和内容中查询
     * @param keyword 关键字
     * @return 查询到的文档
     */
    public SearchHits<EsArticle> searchArticle(String keyword) {
        return articleRepo.find(keyword);
    }
}

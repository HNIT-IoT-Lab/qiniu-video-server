package com.qiniu.video.dao;


import cn.hnit.sdk.orm.mongodb.dao.BaseDao;
import com.qiniu.video.entity.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ArticleDao extends BaseDao<Article> {
    /**
     * 子类初始化时 赋值泛型
     *
     * @param mongoTemplate mongoTemplate
     */
    protected ArticleDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}

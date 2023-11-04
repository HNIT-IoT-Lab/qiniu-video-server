package com.qiniu.video.dao;

import cn.hnit.sdk.orm.mongodb.dao.BaseDao;
import com.qiniu.video.entity.model.Article;
import com.qiniu.video.entity.model.Follow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class FollowDao extends BaseDao<Follow> {
    /**
     * 子类初始化时 赋值泛型
     *
     * @param mongoTemplate mongoTemplate
     */
    protected FollowDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}

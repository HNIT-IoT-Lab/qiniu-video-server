package com.qiniu.video.dao;

import cn.hnit.sdk.orm.mongodb.dao.BaseDao;
import com.qiniu.video.entity.model.UserArticleInteraction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/11/03 17:23
 */
@Slf4j
@Repository
public class UserArticleInteractionDao extends BaseDao<UserArticleInteraction> {
    /**
     * 子类初始化时 赋值泛型
     *
     * @param mongoTemplate mongoTemplate
     */
    protected UserArticleInteractionDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}

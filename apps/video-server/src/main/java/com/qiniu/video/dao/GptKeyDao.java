package com.qiniu.video.dao;

import cn.hnit.sdk.orm.mongodb.dao.BaseDao;
import com.qiniu.video.entity.model.GptKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/30 20:00
 */
@Slf4j
@Repository
public class GptKeyDao extends BaseDao<GptKey> {
    /**
     * 子类初始化时 赋值泛型
     *
     * @param mongoTemplate mongoTemplate
     */
    protected GptKeyDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}

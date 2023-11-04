package com.qiniu.video.dao;

import cn.hnit.sdk.orm.mongodb.dao.BaseDao;
import com.qiniu.video.entity.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/5/7 23:23
 */
@Slf4j
@Repository
public class MessageDao extends BaseDao<Message> {
    /**
     * 子类初始化时 赋值泛型
     *
     * @param mongoTemplate mongoTemplate
     */
    protected MessageDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}

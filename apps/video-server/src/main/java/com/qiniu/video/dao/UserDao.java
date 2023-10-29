package com.qiniu.video.dao;

import cn.hnit.sdk.orm.mongodb.dao.BaseDao;
import com.qiniu.video.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/27 1:02
 */
@Slf4j
@Repository
public class UserDao extends BaseDao<User> {
    /**
     * 子类初始化时 赋值泛型
     *
     * @param mongoTemplate mongoTemplate
     */
    protected UserDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}

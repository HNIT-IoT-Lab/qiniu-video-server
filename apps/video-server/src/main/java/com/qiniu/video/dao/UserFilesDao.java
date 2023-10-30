package com.qiniu.video.dao;


import cn.hnit.sdk.orm.mongodb.dao.BaseDao;
import com.qiniu.video.entity.UserFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserFilesDao extends BaseDao<UserFile> {
    /**
     * 子类初始化时 赋值泛型
     *
     * @param mongoTemplate mongoTemplate
     */
    protected UserFilesDao(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}

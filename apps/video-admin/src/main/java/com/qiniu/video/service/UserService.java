package com.qiniu.video.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hnit.common.exception.base.AppException;
import cn.hutool.core.util.ObjectUtil;
import com.qiniu.video.dao.UserDao;
import com.qiniu.video.entity.User;
import com.qiniu.video.entity.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/27 21:03
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;


    public User FindByUserName(String userName) {
        return userDao.findOne(Query.query(Criteria.where(User.Fields.userName).is(userName)));
    }

    public String Login(AccountVO accountVO) {
        User user = FindByUserName(accountVO.getUserName());
        if (ObjectUtil.isEmpty(user)) {
            throw new AppException("找不到指定账号");
        }
        if (user.getPassword().equals(accountVO.getPassword())) {
            // 登录成功
            StpUtil.login(user.getId());
            return StpUtil.getTokenValue();
        }
        throw new AppException("账号或密码错误");
    }

    public User Register(User user) {
        return userDao.save(user);
    }
}

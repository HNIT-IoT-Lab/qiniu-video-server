package com.hnit.video.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hnit.video.model.entity.auth.User;
import com.hnit.video.model.vo.auth.UserQueryCondition;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author King Gigi
 * @since 2022-07-21
 */
public interface UserService extends IService<User> {
    // 分页查询用户信息
    Page<User> pageQueryUser(Long index, Long limit, UserQueryCondition queryCondition);

    // 根据用户名获取用户
    User getByUsername(String username);

    // 根据角色id启用或者禁用某个用户
    void enableOrDisableUser(Long userId, Boolean isEnable);
}

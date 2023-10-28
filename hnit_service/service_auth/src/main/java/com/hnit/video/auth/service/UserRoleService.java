package com.hnit.video.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnit.video.model.entity.auth.UserRole;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author King Gigi
 * @since 2022-07-21
 */
public interface UserRoleService extends IService<UserRole> {

    // 启用或者禁用用户id为userId的角色
    void enableOrDisableUserRole(Long userId, Boolean isEnable);
}

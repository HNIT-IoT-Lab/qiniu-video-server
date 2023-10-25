package com.hnit.video.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnit.model.entity.auth.RolePermission;

/**
 * <p>
 * 角色权限 服务类
 * </p>
 *
 * @author King Gigi
 * @since 2022-07-21
 */
public interface RolePermissionService extends IService<RolePermission> {

    // 禁用或者启用用户权限
    void enableOrDisableRolePermission(Long roleId, Boolean isEnable);
}

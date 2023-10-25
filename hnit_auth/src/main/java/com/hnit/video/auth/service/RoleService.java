package com.hnit.video.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hnit.model.entity.auth.Role;
import com.hnit.model.vo.auth.RoleQueryCondition;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author King Gigi
 * @since 2022-07-21
 */
public interface RoleService extends IService<Role> {
    // 分页条件查询角色信息
    Page<Role> pageQueryRole(Long index, Long limit, RoleQueryCondition queryCondition);

    // 根据用户id获取用户角色
    Map<String, Object> getRoleByUserId(Long userId);

    // 根据用户id分配用户角色
    boolean assignRole(Long userId, Long[] roleIdList);

    // 获取角色列表
    List<Role> getRoleListByUserId(Long userId);

    // 根据角色id启用或者禁用某个角色
    void enableOrDisableRole(Long roleId, Boolean isEnable);
}

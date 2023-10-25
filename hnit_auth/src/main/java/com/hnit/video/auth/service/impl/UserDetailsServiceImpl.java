package com.hnit.video.auth.service.impl;

import com.hnit.video.common.security.entity.User;
import com.hnit.video.auth.service.PermissionService;
import com.hnit.video.auth.service.RoleService;
import com.hnit.video.auth.service.UserService;
import com.hnit.common.base.exception.OESException;
import com.hnit.video.common.security.entity.SecurityUser;
import com.hnit.model.entity.auth.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserService userService;

    private RoleService roleService;

    private PermissionService permissionService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
        User user = userService.getByUsername(username);

        // 判断用户是否存在
        if (null == user) {
            throw new UsernameNotFoundException("用户名不存在！");
        } else if (!user.getIsEnable()) {
            throw new OESException("用户被禁用");
        }
        // 返回UserDetails实现类
        User curUser = new User();
        BeanUtils.copyProperties(user, curUser);
        // 获取用户的权限列表(仅权限值,如果权限要加入hasRole的控制,得将角色加入且角色需要加入ROLE_前缀)
        List<String> authorities = permissionService.getPermissionValueByUserId(user.getId());
        SecurityUser securityUser = new SecurityUser(curUser);
        securityUser.setPermissionValueList(authorities);
        return securityUser;
    }
}

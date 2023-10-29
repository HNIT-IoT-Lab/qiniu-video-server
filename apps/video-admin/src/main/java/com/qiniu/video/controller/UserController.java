package com.qiniu.video.controller;

import cn.hnit.common.redis.operator.RedisOperator;
import cn.hnit.starter.annotation.AuthIgnore;
import com.qiniu.video.entity.User;
import com.qiniu.video.entity.vo.AccountVO;
import com.qiniu.video.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/26 23:02
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private UserService userService;

    @AuthIgnore
    @PostMapping("/login")
    public String Login(@RequestBody AccountVO accountVO) {
        return userService.Login(accountVO);
    }

    @AuthIgnore
    @PostMapping("/register")
    public User Register(@Validated @RequestBody User user) {
        return userService.Register(user);
    }

}

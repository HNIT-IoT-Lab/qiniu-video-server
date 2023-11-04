package com.qiniu.video.controller;

import cn.hnit.entity.LoginVO;
import cn.hnit.starter.annotation.AuthIgnore;
import cn.hnit.utils.BeanUtil;
import cn.hnit.utils.context.UserContext;
import com.qiniu.video.entity.model.User;
import com.qiniu.video.entity.vo.PhoneVO;
import com.qiniu.video.entity.vo.UserVO;
import com.qiniu.video.service.UserService;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/28 20:57
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @AuthIgnore
    @PostMapping("/login")
    public LoginVO Login(@RequestBody Map<String, Object> paramMap) {
        return userService.Login(paramMap);
    }

    @PostMapping("/logOut")
    public void LoginOut(@RequestBody Map<String, Object> paramMap) {
        userService.LogOut(paramMap);
    }

    @AuthIgnore
    @PostMapping("/sendSmsCode")
    public String SendSmsCode(@Validated @RequestBody PhoneVO vo) {
        return userService.SendSmsCode(vo.getPhoneNumber());
    }

    /**
     * 通过账号密码注册，一般不用，用户直接登录，如果没有注册就帮用户注册
     */
    @AuthIgnore
    @PostMapping("/register")
    public UserVO Register(@Validated @RequestBody User user) {
        return BeanUtil.copyProperties(userService.Register(user), UserVO.class);
    }

    /**
     * 用户信息
     * @return
     */
    @PostMapping("/userInfo")
    public UserVO UserInfo() {
        return BeanUtil.copyProperties(userService.FindById(UserContext.getUserId()), UserVO.class);
    }

    @PostMapping("/uploadImage")
    public UserVO UploadImage(@RequestPart("file") MultipartFile file) {
        return BeanUtil.copyProperties(userService.updateAvatar(file), UserVO.class);
    }

    /**
     * 通用上传接口
     */
    @PostMapping("/upload")
    public String Upload(@RequestPart("file") MultipartFile file) {
        return userService.upload(file);
    }


    @PostMapping("/updateUserName")
    public UserVO UpdateUserName(@RequestBody User user) {
        return BeanUtil.copyProperties(userService.updateUserName(user.getUserName()), UserVO.class);
    }

    /**
     * 关注用户
     * 需要将被关注的用户id传过来
     */
    @PostMapping("/followUser")
    public void followUser(@RequestParam("followUserId") @NotNull String followUserId){
        userService.followUser(followUserId);
    }

    /**
     * 获取关注用户列表
     */
    @GetMapping("/getFollowUserList")
    public List<User> getFollowUserList(){
        return  userService.getFollowUserList();
    }
}

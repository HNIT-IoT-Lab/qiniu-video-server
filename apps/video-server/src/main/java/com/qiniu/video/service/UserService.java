package com.qiniu.video.service;

import cn.hnit.entity.LoginVO;
import com.qiniu.video.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/29 14:39
 */
public interface UserService {
    LoginVO Login(Map<String, Object> paramMap);
    LoginVO LogOut(Map<String, Object> paramMap);
    /**
     * 通过手机号获取用户 没有就生成用户并返回
     */
    User AddOrDefault(String phoneNumber);
    User Register(User user);
    User RegisterByPhoneNumber(String phoneNumber);
    User UpdateUserName(String userName);
    String SendSmsCode(String phoneNumber);
    User FindByPhoneNumber(String phoneNumber);
    User FindByUserName(String userName);
    User FindById(Long id);
    User updateAvatar(MultipartFile file);
    String upload(MultipartFile file);
    User updateUserName(String userName);
}

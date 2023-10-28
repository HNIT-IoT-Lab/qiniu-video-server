package com.hnit.video.common.security.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// 密码处理,BCrypt加密
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder passwordEncoder;

    public DefaultPasswordEncoder() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // 把未进行加密的密码进行BCrypt加密
    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 将未加密密码和数据库中的密码进行比对
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
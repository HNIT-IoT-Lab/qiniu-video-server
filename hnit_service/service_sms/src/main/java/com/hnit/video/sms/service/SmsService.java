package com.hnit.video.sms.service;

public interface SmsService {
    // 根据手机号获取验证码
    boolean getVerificationCode(String s, String[] params);
}

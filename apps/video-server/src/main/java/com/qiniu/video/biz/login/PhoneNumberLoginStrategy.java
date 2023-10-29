package com.qiniu.video.biz.login;

import cn.dev33.satoken.stp.StpUtil;
import cn.hnit.common.redis.operator.RedisOperator;
import cn.hnit.core.LoginFactory;
import cn.hnit.entity.LoginVO;
import cn.hnit.handle.LoginHandle;
import cn.hnit.utils.AssertUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.qiniu.video.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 使用电话号码登录具体策略
 *
 * @author king
 * @since 2022/10/4 12:02
 */
@Slf4j
@Component
public class PhoneNumberLoginStrategy implements LoginHandle {

    private static final String PHONE_NUMBER_LOGIN_STRATEGY = "PHONE_NUMBER_LOGIN_STRATEGY";

    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private UserServiceImpl userService;


    @Override
    public LoginVO login(Map<String, Object> params) {
        // 放具体的登录策略
        if (log.isDebugEnabled()) {
            log.debug("用户通过手机号码登录，参数为：{}", params);
        }
        Object phoneNumber = params.get("phoneNumber");
        AssertUtil.notNull(phoneNumber, "手机号不能为空");
        String stringPhoneNumber = String.valueOf(phoneNumber);
        String code = redisOperator.get(DigestUtil.md5Hex(stringPhoneNumber));
        AssertUtil.isTrue(Objects.equals(code, String.valueOf(params.get("code"))), "验证码有误");
        // 如果没有该用户 则默认注册
        StpUtil.login(userService.AddOrDefault(stringPhoneNumber).getId());
        return LoginVO.builder().token(StpUtil.getTokenValue()).build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将自己注册到我们自己定义的工厂中
        LoginFactory.register(PHONE_NUMBER_LOGIN_STRATEGY, this);
    }

}

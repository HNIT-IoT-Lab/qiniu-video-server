package com.qiniu.video.biz.login;


import cn.hnit.core.LoginFactory;
import cn.hnit.entity.LoginVO;
import cn.hnit.handle.LoginHandle;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 账号密码登录
 *
 * @author king
 * @since 2023/10/26 23:09
 */
@Slf4j
public class AccountLoginStrategy implements LoginHandle {

    private static final String ACCOUNT_LOGIN_STRATEGY = "ACCOUNT_LOGIN_STRATEGY";

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将自己注册到我们自己定义的工厂中
        LoginFactory.register(ACCOUNT_LOGIN_STRATEGY, this);
    }

    @Override
    public LoginVO login(Map<String, Object> params) {
        // 放具体的登录策略
        if (log.isDebugEnabled()) {
            log.debug("用户通过账号密登录");
        }
//        AccountVO accountVO;
//        try {
//            accountVO = BeanMapUtil.mapToBean(params, AccountVO.class);
//        } catch (IllegalAccessException | InstantiationException e) {
//            throw AppException.pop("参数异常");
//        }
        return new LoginVO();
    }

}

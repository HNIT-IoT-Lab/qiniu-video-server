package com.qiniu.video.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/28 0:10
 */
public interface LoginConstant {

    @AllArgsConstructor
    @Getter
    enum LOGIN_STRATEGY {
        ACCOUNT_LOGIN_STRATEGY,
        PHONE_NUMBER_LOGIN_STRATEGY
    }
}

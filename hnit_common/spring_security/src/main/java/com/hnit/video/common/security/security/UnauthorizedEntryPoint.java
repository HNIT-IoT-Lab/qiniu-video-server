package com.hnit.video.common.security.security;

import com.hnit.video.common.utils.ResponseUtil;
import com.hnit.video.common.utils.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 未授权的统一处理方式
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        ResponseUtil.out(response, Result.failure().message("您没有相关权限"));
    }
}

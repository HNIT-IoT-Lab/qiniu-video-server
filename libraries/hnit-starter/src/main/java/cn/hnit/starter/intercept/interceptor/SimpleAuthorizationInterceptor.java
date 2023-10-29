package cn.hnit.starter.intercept.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hnit.common.exception.base.AppException;
import cn.hnit.common.resultx.constant.SysCode;
import cn.hnit.starter.annotation.AuthIgnore;
import cn.hnit.utils.IpUtil;
import cn.hnit.utils.LocalThreadUtil;
import cn.hnit.utils.common.bean.HeaderParam;
import cn.hnit.utils.common.bean.TradeRecord;
import cn.hnit.utils.logutil.LogUtils;
import cn.hutool.core.date.SystemClock;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Objects;

/**
 * 用户请求接口鉴权
 *
 * @author king
 * @since 2023/10/27 21:49
 */
@Slf4j
@Configuration
@Order(-100)
public class SimpleAuthorizationInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    private static final String USERID = "userId";
    public static final String TIME_KEY = "speed";

    @Value("${spring.profiles.active}")
    private String profile;

    /**
     * 注册拦截器
     *
     * @param registry 注册机
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        setHeader(request);
        // 本地环境不校验
        if (StringUtils.isNotBlank(profile) && profile.contains("local") && StringUtils.isEmpty(LocalThreadUtil.getHeader().getUserToken())) {
            String uid = request.getHeader("uid");
            request.setAttribute(USERID, request.getHeader("uid"));
            if (!StringUtils.isBlank(uid)) {
                LocalThreadUtil.setUid(Long.parseLong(request.getHeader("uid")));
            }
            return true;
        }

        if (checkAuthAnnotation(request, response, handler)) {
            return Boolean.TRUE;
        }
        throw new AppException(SysCode.FORBIDDEN, "请登录后再操作");
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String str = MDC.get(TIME_KEY);
        if (StringUtils.isNotEmpty(str)) {
            log.info("\n响应时间: {}ms - url: {} ",
                    SystemClock.now() - Long.parseLong(str),
                    request.getRequestURI());
        }
        // 清空日志信息
        LocalThreadUtil.remove();
        LogUtils.clear();
    }

    /**
     * 根据注解判断是否鉴权
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private boolean checkAuthAnnotation(HttpServletRequest request, HttpServletResponse response, Object handler) throws NoSuchAlgorithmException, IOException {
        if (handler instanceof HandlerMethod) {
            AuthIgnore annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthIgnore.class);
            // log.info("handler 的值为：{}", handler);
            if (Objects.nonNull(annotation)) {
                // 不需要校验
                return Boolean.TRUE;
            }
        }
        // 权限校验
//        log.info("判断是否进入 checkAuthAnnotation 方法中的 if中");
        if (StpUtil.isLogin()) {
            // setUserContext
            setUserContext();
            return true;
        }
        return false;
    }

    private void setUserContext() {
        LocalThreadUtil.setUid(StpUtil.getLoginIdAsLong());
    }

    /**
     * 设置请求头到当前线程
     *
     * @param request HttpServletRequest
     */
    private void setHeader(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        JSONObject header = new JSONObject();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            header.put(key, request.getHeader(key));
        }
        // 设置请求的信息
        setTraceInfo(request);
        HeaderParam headerParam = header.toJavaObject(HeaderParam.class);
        headerParam.setIp(IpUtil.getIpAddr(request));
        headerParam.setPort(IpUtil.getPort(request));
        LocalThreadUtil.setLocalObj(headerParam);
        TradeRecord tradeRecord = new TradeRecord();
        tradeRecord.setMethod(request.getMethod());
        // TODO 流只能读取一次，这里读取了SpringMVC读取就会抛异常
//        tradeRecord.setBody(HttpHelper.getBodyString(request));
        tradeRecord.setUrl(request.getRequestURL().toString());
        LocalThreadUtil.setLocalObj(tradeRecord);
        MDC.put(TIME_KEY, SystemClock.now() + "");
//        log.info("\n请求Id: {} \n请求url: {} \n请求头信息为: {} \n请求类型: {} \n",
//                LogUtils.getTraceId(), tradeRecord.getUrl(), headerParam, tradeRecord.getMethod());
        LocalThreadUtil.setLocalObj(tradeRecord);
    }

    private void setTraceInfo(HttpServletRequest request) {
        LogUtils.setTraceId();
        LogUtils.setUrl(request != null ? request.getRequestURI() : "");
        LogUtils.setPort(IpUtil.getPort(request));
        LogUtils.setUserIp(IpUtil.getIpAddr(request));
        LogUtils.setDeviceId(IpUtil.getDeviceId(request));
        LogUtils.setPlatform(IpUtil.getPlatform(request));
        LogUtils.setComeFrom(IpUtil.getComeFrom(request));
        LogUtils.setPcVersion(request.getHeader("pcVersion"));
        LogUtils.setAppVersion(request.getHeader("versionName"));
    }
}

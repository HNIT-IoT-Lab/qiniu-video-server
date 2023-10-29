//package cn.hnit.starter.intercept.interceptor;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chinaesport.mq.sdk.MessageService;
//import com.xhhd.buge.combiz.common.entity.AppInfo;
//import com.xhhd.buge.combiz.common.keys.Keys;
//import com.xhhd.utils.RedisUtils;
//import com.xhhd.utils.common.bean.HeaderParam;
//import com.xhhd.utils.context.UserContext;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//
///**
// * app信息更新逻辑
// */
//@Service
//@Slf4j
//public class AppInterceptor extends HandlerInterceptorAdapter {
//
//
//    /**
//     * (required = false) 表示并不一定需要发送这个心跳,当引用start 包的服务没有配置message的实例化的时候,不会报错
//     */
//    @Autowired(required = false)
//    private MessageService messageService;
//
//
//    @Autowired
//    private RedisUtils redisUtils;
//
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (messageService == null) {
//            return true;
//        }
//        Long userId = UserContext.getUserId();
//        if (userId == null) {
//            return true;
//        }
//        String apps = (String) redisUtils.hget(Keys.APP_INFO_KEY, userId + "");
//        HeaderParam headerParam = UserContext.getHeader();
//        AppInfo appInfo = StringUtils.isNotBlank(apps) ? JSONObject.parseObject(apps, AppInfo.class) : null;
//        if (existDifference(headerParam, appInfo)) {
//            log.info("[AppInterceptor] [preHandle] appInfo is update , userId is {}", userId);
//            cacheAppInfo(buildAppInfo(headerParam), userId);
//        }
//        return true;
//    }
//
//    private void cacheAppInfo(AppInfo appInfo, Long userId) {
//        if (appInfo == null) {
//            return;
//        }
//        log.info("[cacheAppInfo] new appInfo is {},user is {}", appInfo, userId);
//        redisUtils.hset(Keys.APP_INFO_KEY, userId + "", JSONObject.toJSONString(appInfo));
//    }
//
//    private AppInfo buildAppInfo(HeaderParam headerParam) {
//        AppInfo appInfo = new AppInfo();
//        if (headerParam == null) {
//            return appInfo;
//        }
//        appInfo.setDeviceId(headerParam.getDeviceId());
//        appInfo.setComeFrom(headerParam.getComeFrom());
//        appInfo.setVersion(headerParam.getVersion());
//        appInfo.setPlatform(headerParam.getPlatform());
//        appInfo.setVersionName(headerParam.getVersionName());
//        return appInfo;
//    }
//
//
//    private boolean existDifference(HeaderParam headerParam, AppInfo appInfo) {
//        if (headerParam == null) {
//            return false;
//        }
//        if (appInfo == null && headerParam != null) {
//            return true;
//        }
//        if (existFieldDifference(headerParam.getDeviceId(), appInfo.getDeviceId())) {
//            return true;
//        }
//        if (existFieldDifference(headerParam.getVersion(), appInfo.getVersion())) {
//            return true;
//        }
//        if (existFieldDifference(headerParam.getPlatform(), appInfo.getPlatform())) {
//            return true;
//        }
//        return existFieldDifference(headerParam.getComeFrom(), appInfo.getComeFrom());
//    }
//
//    private boolean existFieldDifference(String value, String oldValue) {
//        if (StringUtils.isBlank(value) || StringUtils.isBlank(oldValue)) {
//            return false;
//        }
//        if (StringUtils.isBlank(oldValue) && StringUtils.isNotBlank(value)) {
//            return true;
//        }
//        return !oldValue.equals(value);
//    }
//
//}

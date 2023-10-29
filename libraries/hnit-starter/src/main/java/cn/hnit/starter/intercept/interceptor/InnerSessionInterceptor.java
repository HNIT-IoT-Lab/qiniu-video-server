//package cn.hnit.starter.intercept.interceptor;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//import starter.buge.annotation.Inner;
//import starter.buge.intercept.InnerSessionService;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Objects;
//
//
///**
// * 临时方案,需要弄成网关的逻辑
// * 内部系统的登陆态
// */
//@Service
//@Slf4j
//public class InnerSessionInterceptor extends HandlerInterceptorAdapter {
//
//    @Value("${spring.profiles.active:dev}")
//    private String profile;
//
//
//    @Autowired(required = false)
//    private InnerSessionService innerSessionService;
//
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (innerSessionService == null || checkInner(handler)) {
//            return true;
//        }
//        return innerSessionService.isLogin(request);
//    }
//
//
//    private boolean checkInner(Object handler) {
//        Inner annotation = ((HandlerMethod) handler).getMethodAnnotation(Inner.class);
//        return Objects.isNull(annotation);
//    }
//
//
//}

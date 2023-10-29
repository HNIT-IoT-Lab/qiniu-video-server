//package cn.hnit.starter.config;
//
//import cn.hnit.starter.intercept.interceptor.AppInterceptor;
//import cn.hnit.starter.intercept.interceptor.AuthorizationInterceptor;
//import cn.hnit.starter.intercept.interceptor.HeartInterceptor;
//import cn.hnit.starter.intercept.interceptor.InnerSessionInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import javax.annotation.Resource;
//
//@Configuration
//@Import(AuthorizationInterceptor.class)
//public class VerifyAutoConfigure implements WebMvcConfigurer {
//
//    @Autowired
//    private AuthorizationInterceptor authorizationInterceptor;
//
//
//    @Autowired
//    private HeartInterceptor heartInterceptor;
//
//
//    @Autowired
//    private AppInterceptor appInterceptor;
//
//
//    @Resource
//    private InnerSessionInterceptor innerSessionInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**");
//        registry.addInterceptor(heartInterceptor).addPathPatterns("/**");
//        registry.addInterceptor(appInterceptor).addPathPatterns("/**");
//        registry.addInterceptor(innerSessionInterceptor).addPathPatterns("/**");
//    }
//
//}

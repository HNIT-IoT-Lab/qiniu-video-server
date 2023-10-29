package cn.hnit.starter.intercept.argumentresolver.config;

import cn.hnit.starter.intercept.argumentresolver.handle.UserHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 向IOC容器中注册自己的参数解析器
 *
 * @author 梁峰源
 * @since 2022/10/2 15:18
 */
@Configuration
public class ArgumentHandleConfig implements WebMvcConfigurer {

    @Autowired
    private UserHandlerMethodArgumentResolver userHandlerMethodArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 添加自己的拦截器
        resolvers.add(userHandlerMethodArgumentResolver);
    }
}

package cn.hnit.starter.intercept.argumentresolver.handle;

import cn.hnit.starter.annotation.User;
import cn.hnit.starter.intercept.argumentresolver.config.ArgumentHandleConfig;
import cn.hnit.utils.LocalThreadUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 自定义拦截器，用于依赖注入<br/>
 * 具体使用，只需要在被IOC容器托管的bean中注入User，就可以获得当前用户<br/>
 * example：
 * <pre>
 * GetMapping({"/userInfo", "/consumerInfomation"})
 * public ResponseEntity<ChaUser> userInfo(@User ChaUser user) {
 *     return ResponseEntity.ok(user);
 * }
 * </pre>
 *
 * @author 梁峰源
 * @see User
 * @see HandlerMethodArgumentResolver
 * @see ArgumentHandleConfig
 * @since 2022/9/25 15:42
 */
@Component
public class UserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 当参数上有@comment时才使用该解析器解析
        return parameter.hasParameterAnnotation(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // TODO 要在UserContext提供拿到当前用户信息的接口，替换下面的实现
        return LocalThreadUtil.getUid();
    }
}

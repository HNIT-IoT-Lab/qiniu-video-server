package cn.hnit.starter.annotation;


import java.lang.annotation.*;


/**
 * 启用web拦截 鉴权 异常处理，过滤等
 *
 * @author Admin
 * <p>
 * 2020年6月5日
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableExceptionHandler
@EnableAuthHandler
//@Import(VerifyAutoConfigure.class) 会顶替原本的
public @interface EnableWebFilter {

}

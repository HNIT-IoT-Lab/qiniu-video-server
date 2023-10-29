package cn.hnit.starter.annotation;

import cn.hnit.starter.intercept.GlobalExceptionHandler;
import cn.hnit.starter.intercept.crypt.DecryptRequestBodyAdvice;
import cn.hnit.starter.intercept.crypt.EncryptResponseBodyAdvice;
import cn.hnit.starter.intercept.interceptor.ResponseAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 启用全局异常处理
 *
 * @author 梁峰源
 * @date 2022年11月12日21:10:20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({GlobalExceptionHandler.class,
        DecryptRequestBodyAdvice.class,
        EncryptResponseBodyAdvice.class,
        ResponseAdvice.class})
public @interface EnableExceptionHandler {
}

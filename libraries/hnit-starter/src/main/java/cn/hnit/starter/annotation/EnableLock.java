package cn.hnit.starter.annotation;

import cn.hnit.starter.intercept.aop.LockAop;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启分布式锁使用, 基于Redisson实现分布式锁
 *
 * @author 梁峰源
 * @since  2022年9月25日17:28:36
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(LockAop.class)
public @interface EnableLock {

}

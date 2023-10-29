package cn.hnit.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁, 根据类名+方法名+参数值进行锁
 *
 *
 * @author 梁峰源
 * @since 2022-08-22 21:54
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 自定义锁名，请使用字母、数字和冒号组合，不要使用特殊字符<br/>
     * 默认为空字符串。为空时，将自动取被注解<类名:方法名>作为默认key<br/>
     * 此key（或默认key）将结合LockParam注解生成最终锁名
     */
    String lockKey() default "";

    /**
     * 获取锁时发现当前处于锁状态时采取的策略。默认为WAIT（等待）
     */
    LockStrategy lockStrategy() default LockStrategy.WAIT;

    /**
     * 采取REJECT策略时，拒绝服务的异常提示语。默认为“您的请求过于频繁，请稍后再提交。”
     */
    String rejectMessage() default "您的请求过于频繁，请稍后再提交。";

    /**
     * 抛出异常错误码
     * @return
     */
    int errorCode() default -1;

    /**
     * 锁时长，单位为秒，用于防止已获取锁的处理者意外crash导致无法正常释放锁。默认为10秒
     */
    int ttl() default 10;

    /**
     * 采取WAIT策略时，尝试获取锁的时间间隔，单位毫秒。默认为10
     */
    int retryInterval() default 10;

    /**
     * 重试最大次数，防止无限重试。默认为60000次（默认10ms一次，60000次最少耗时600秒）
     */
    int maxRetry() default 60000;

    /**
     * 采取WAIT策略时，当前阻塞等待的请求数上线。默认为10<br/>
     * 超过上限时，即使采用WAIT策略，新的请求也会马上reject
     */
    int maxWaiting() default 10;

    /**
     * 锁策略
     */
    enum LockStrategy {
        /** 直接拒绝*/
        REJECT,
        /** 等待锁释放*/
        WAIT
    }
}

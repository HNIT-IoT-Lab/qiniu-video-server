package cn.hnit.starter.intercept.aop;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.common.resultx.ResponseMsg;
import cn.hnit.starter.annotation.RateLimiter;
import cn.hnit.starter.annotation.RateLimiterParam;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 * @author xsm
 */
@Aspect
@Slf4j
public class RateLimiterAop extends BaseAop {

    private static final String KEY_PREFIX = "buge:rate:limit:";

    @Autowired(required = false)
    private StringRedisTemplate redis;

    @Autowired
    private Environment env;

    /**
     * 切面
     * @param rateLimiter
     */
    @Pointcut(value = "@annotation(rateLimiter)", argNames = "rateLimiter")
    public void pointcut(RateLimiter rateLimiter) {}

    /**
     * 处理方式, 环绕
     * @param pjp
     * @param rateLimiter
     * @return
     * @throws Throwable
     */
    @Around(value = "pointcut(rateLimiter)")
    public Object around(final ProceedingJoinPoint pjp, RateLimiter rateLimiter) throws Throwable {
        Method method = getTargetMethod(pjp);
        log.debug("pointcut @{}", method.getName());

        // 构造redis当前时间(精确到秒)key并计数
        String key = generateKey(rateLimiter, pjp, method);
        Long total = redis.opsForValue().increment(key);

        // 保留一定时长方便追查数据
        redis.expire(key, 30, TimeUnit.MINUTES);
        log.debug("key: {}, -> total: {}", key, total);

        // 限流处理
        int permitsPerSecond = StringUtils.isBlank(rateLimiter.permitsPerSecondProperty())
            ? rateLimiter.permitsPerSecond()
            : env.getProperty(rateLimiter.permitsPerSecondProperty(), Integer.class, rateLimiter.permitsPerSecond());
        log.debug("permitsPerSecond: {}", permitsPerSecond);

        boolean limit = total > permitsPerSecond;
        if (limit) {
            log.warn("rate limited, key: {}, total: {}", key, total);
            return onDisallow(rateLimiter, method.getReturnType());
        }

        return pjp.proceed();
    }

    /**
     * 转换响应信息
     * @param rateLimiter
     * @param returnType
     * @return
     */
    private Object onDisallow(RateLimiter rateLimiter, Class<?> returnType) {
        if (StringUtils.isNotBlank(rateLimiter.fallback())) {
            log.debug("return type: {}", returnType.getName());

            if (returnType == ResponseMsg.class) {
                // ResponseInfo<?>泛型需要特别处理
                if (Objects.equals(rateLimiter.generic(), String.class) || Objects.equals(rateLimiter.generic(), void.class)) {
                    return ResponseMsg.success(rateLimiter.fallback());
                }
                return ResponseMsg.success(JSON.parseObject(rateLimiter.fallback(), rateLimiter.generic()));
            } else if (returnType == List.class) {
                // List<?>泛型需要特别处理
                return JSON.parseArray(rateLimiter.fallback(), rateLimiter.generic());
            } else {
                return JSON.parseObject(rateLimiter.fallback(), returnType);
            }
        }
        throw StringUtils.isBlank(rateLimiter.errorInfo()) ? new AppException(rateLimiter.errorCode(),"您的请求过于频繁，请稍候重试")
            : new AppException(rateLimiter.errorCode(),rateLimiter.errorInfo());
    }

    /**
     * 根据类名+方法名+参数值生成锁key
     * @param pjp
     * @param method
     * @return
     */
    private String generateKey(RateLimiter rateLimiter, ProceedingJoinPoint pjp, Method method) {
        StringBuilder keyBuffer = new StringBuilder(KEY_PREFIX);

        // 自定义锁名
        if (StringUtils.isNotBlank(rateLimiter.key())) {
            keyBuffer.append(rateLimiter.key());
        }
        // 默认取类名+方法名
        else {
            keyBuffer.append(method.getDeclaringClass().getSimpleName()).append(":").append(method.getName());
        }

        List<Pair<RateLimiterParam, Object>> pairs =
            getMethodAnnotationAndParametersByAnnotation(pjp, method, RateLimiterParam.class);

        // 获取LockParam组合成锁key
        for (Pair<RateLimiterParam, Object> pair : pairs) {
            RateLimiterParam rateLimiterParam = pair.getKey();
            // 默认直接取参数值，设置了LockParam值时，支持按SpEL取属性值
            packParamKey(keyBuffer, pair.getValue(), rateLimiterParam.value());
        }
        // 添加当前时间(分秒)
        keyBuffer.append(":").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("mmss")));
        log.debug("rate limiter key: {}", keyBuffer);
        return keyBuffer.toString();
    }

}

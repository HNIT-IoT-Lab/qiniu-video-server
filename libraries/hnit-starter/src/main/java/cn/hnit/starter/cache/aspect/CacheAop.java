package cn.hnit.starter.cache.aspect;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.starter.cache.annotation.Cache;
import cn.hnit.starter.cache.annotation.CacheParam;
import cn.hnit.starter.intercept.aop.BaseAop;
import cn.hnit.utils.AssertUtil;
import cn.hnit.utils.SpelUtils;
import cn.hnit.utils.constant.SysCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 缓存切面
 *
 * @author 梁峰源
 * @see CacheProperties.Caffeine
 * @see RedisTemplate
 * @see RedissonClient
 * @since 2022年9月22日19:43:07
 */
@Aspect
@Slf4j
public class CacheAop extends BaseAop {

    /**
     * NULL替换符，防止无数据时一直回源查询
     */
    @Value("${cache.empty.placeholder:*.}")
    private final String cacheNullPlaceholder = "*.";

    /**
     * NULL替换符TTL时长
     */
    @Value("${cache.empty.ttl:5}")
    private final long cacheNullTtlInMinutes = 5;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(cn.hnit.starter.cache.annotation.Cache)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        Method method = getTargetMethod(pjp);
        log.debug("pointcut at {}.{}()", method.getDeclaringClass().getName(), method.getName());

        Cache cache = getAnnotation(method, Cache.class);
        String cacheKey = generateCacheKey(pjp, method, cache);

        // remove cache
        if (cache.remove()) {
            if (cache.removeBeforeInvocation()) {
                removeCache(cache, cacheKey);

                return pjp.proceed();
            } else {
                Object result = pjp.proceed();

                removeCache(cache, cacheKey);

                return result;
            }
        }

        Object value = null;
        // get from cache first
        if (!cache.alwaysPut()) {
            value = getCache(cache, cacheKey);
        }

        // fall back to source
        if (value == null) {
            if (cache.preventCacheBreakdown()) {
                String lockKey = "LOCK:" + cacheKey;
                RLock lock = redissonClient.getLock(lockKey);
                try {
                    if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                        value = getCache(cache, cacheKey);
                        if (Objects.isNull(value)) {
                            value = pjp.proceed();
                        }
                    } else {
                        // TODO 文案待跟产品确认
                        throw new AppException(SysCode.WARN_NO_SHOW, "查询超时，请稍后再试！");
                    }
                } catch (Throwable e) {
                    if (e instanceof AppException) {
                        throw e;
                    }
                    log.error("获取锁异常", e);
                    throw new AppException(SysCode.WARN_NO_SHOW, "查询超时，请稍后再试！");
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                value = pjp.proceed();
            }
            setCache(cache, cacheKey, value);
        }

        return isNull(value) ? null : value;
    }

    private String generateCacheKey(ProceedingJoinPoint pjp, Method method, Cache cache) {
        // 需要在@Cacheable指定缓存key的名称前缀, 后续改善没有指定key默认取方法全限定名作为key
        AssertUtil.hasText(cache.key(), "Cacheable.key is empty in method[" + method.getName() + "].");
        List<Pair<CacheParam, Object>> pairs =
                getMethodAnnotationAndParametersByAnnotation(pjp, method, CacheParam.class);
        StringBuilder keyBuffer = new StringBuilder();
        for (Pair<CacheParam, Object> pair : pairs) {
            CacheParam cacheParam = pair.getKey();
            Object param = pair.getValue();
            // 支持表达式获取属性值
            if (param != null && StringUtils.isNotBlank(cacheParam.value())) {
                param = SpelUtils.getValue(param, cacheParam.value());
            }
            if (param == null) {
                keyBuffer.append(":-");
            } else {
                String tmp = param.toString();
                keyBuffer.append(":").append("".equals(tmp) ? "-" : (param.toString().replace(":", "-")));
            }
        }

        // 生成缓存key
        String cacheKey;
        if (cache.paramMd5()) {
            String key = DigestUtils.md5DigestAsHex(keyBuffer.toString().getBytes());
            cacheKey = String.join(":", cache.key(), key);
        } else {
            keyBuffer.insert(0, cache.key());
            cacheKey = keyBuffer.toString();
        }
        log.debug("cache key={}", cacheKey);

        return cacheKey;
    }

    private boolean isNull(Object value) {
        return value == null || ((value instanceof String) && (cacheNullPlaceholder.equals(value)));
    }

    private void setCache(Cache cache, String key, Object value) {
        // 查询不到数据，设置NULL替换符
        if (value == null) {
            setNullCache(key, cache);
            return;
        }
        setNotNullCache(key, value, cache);
    }

    private void setNotNullCache(String key, Object value, Cache cache) {
        long timeout = cache.ttl();
        TimeUnit timeUnit = cache.ttlTimeUnit();
        switch (cache.cacheManager()) {
            case Redis:
                if (timeout == 0) {
                    redisTemplate.opsForValue().set(key, value);
                } else {
                    redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
                }
                log.debug("set redis, {}={}", key, value);
                break;
            // TODO ehcache 待实现
            default:
                break;
        }
    }

    private void setNullCache(String key, Cache cache) {
        int nullCacheSecond = cache.nullCacheSecond();
        switch (cache.cacheManager()) {
            case Redis:
                if (nullCacheSecond != 0) {
                    if (nullCacheSecond > 0) {
                        redisTemplate.opsForValue().set(key, cacheNullPlaceholder, nullCacheSecond, TimeUnit.SECONDS);
                    } else {
                        redisTemplate.opsForValue().set(key, cacheNullPlaceholder, cacheNullTtlInMinutes, TimeUnit.MINUTES);
                    }
                    log.debug("set redis, {}=NULL", key);
                }
                break;
            // TODO ehcache 待实现
            default:
                break;
        }
    }

    private Object getCache(Cache cache, String key) {
        Object value = null;
        switch (cache.cacheManager()) {
            case Redis:
                value = redisTemplate.opsForValue().get(key);
                log.debug("get redis, {}={}", key, JSON.toJSONString(value));
                break;
            // TODO ehcache 待实现
            default:
                break;
        }
        return value;
    }

    private void removeCache(Cache cache, String key) {
        try {
            switch (cache.cacheManager()) {
                case Redis:
                    redisTemplate.delete(key);
                    log.debug("remove redis cache {}", key);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

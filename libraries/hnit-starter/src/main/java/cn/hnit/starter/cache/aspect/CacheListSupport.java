package cn.hnit.starter.cache.aspect;

import cn.hnit.starter.cache.annotation.CacheList;
import cn.hnit.starter.cache.core.RedisCaffeineCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CacheList注解切面实现类
 *
 * @author 梁峰源
 * @since 2022/10/2 10:53
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(CacheManager.class)
public class CacheListSupport {

    @Autowired
    private CacheManager cacheManager;

    @Pointcut("@annotation(cn.hnit.starter.cache.annotation.CacheList)")
    public void cacheListAspect() {
    }

    @Around("cacheListAspect()")
    public Object cacheListAround(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        CacheList annotation = method.getAnnotation(CacheList.class);
        String cacheName = annotation.itemCacheName();
        Cache cache = cacheManager.getCache(cacheName);
        String keyField = annotation.itemKeyField();
        int keyPos = annotation.listPos();
        Object[] args = point.getArgs();
        if (!(args[keyPos] instanceof Iterable)) {
            throw new UnsupportedOperationException("Args[keyPos] is not Iterable");
        }
        if (!List.class.isAssignableFrom(method.getReturnType())) {
            throw new UnsupportedOperationException("Unsupport return-type , only List");
        }

        List<Object> noCache = new ArrayList<>();
        Map<Object, Object> map = new LinkedHashMap<>();

        Iterable<Object> arg = (Iterable<Object>) args[keyPos];
        for (Object o : arg) {
            Cache.ValueWrapper val = cache.get(o);
            if (val == null) {
                map.put(o, null);
                noCache.add(o);
            } else {
                map.put(o, val.get());
            }
        }

        if (!noCache.isEmpty()) {
            args[keyPos] = noCache;
            List<Object> ret = (List<Object>) point.proceed(args);
            if (!CollectionUtils.isEmpty(ret)) {
                Map<Object, Object> kv = new HashMap<>();
                for (Object obj : ret) {
                    Field field = ReflectionUtils.findField(obj.getClass(), keyField);
                    field.setAccessible(true);
                    Object key = ReflectionUtils.getField(field, obj);
                    map.put(key, obj);
                    kv.put(key, obj);
                }
                if (!CollectionUtils.isEmpty(kv)) {
                    ((RedisCaffeineCache) cache).putAll(kv);
                }
            }
        }
        args[keyPos] = arg;
        return map.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}

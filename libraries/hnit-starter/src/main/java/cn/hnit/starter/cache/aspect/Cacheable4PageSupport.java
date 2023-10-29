package cn.hnit.starter.cache.aspect;

import cn.hnit.starter.cache.annotation.CacheEvict4Page;
import cn.hnit.starter.cache.annotation.Cacheable4Page;
import cn.hnit.starter.cache.annotation.Caching4Page;
import cn.hnit.starter.cache.core.RedisCaffeineCache;
import cn.hutool.core.convert.NumberChineseFormatter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * 分页缓存中只缓存id列表,分页条件condition 与 分页参数(size,offset)需分开指定<br/>
 * 1.根据（condition,offset,size）生成某一页的pagekey，并分页缓存 中找到id 列表，若未找到，则执行sql操作得到结果 集dblist<br/>
 * 2.若找到id列表，则在id为key的缓存中遍历，若其中一个未命中则执行sql操作得到 结果 集dblist<br/>
 * 3.若全部id都命中，则返回<br/>
 * 4.将结果集dblist中的id缓存 到分页列表，并且每个entry缓存到id为key的缓存中<br/>
 * 5.将pagekey放到一个集合中，该集合以condition作为key。通过注解CacheEvict4Page使用缓存失效，并根据condition找到集合中的所有key并全部失效<br/>
 * 假如：分页逻辑为按热度排序 第一页缓存 1，2，3，4，5.此时来了评论6且热度到了第1，但是由于缓存，该缓存评论无法在缓存失效前到达首页位置。<br/>
 * 因此必须实现缓存失效机制
 *
 * @author 梁峰源
 * @since 2022年9月25日17:28:36
 */
@Aspect
@Component
@Slf4j
@ConditionalOnBean(CacheManager.class)
public class Cacheable4PageSupport implements ApplicationContextAware {
    public static final String DEFAULT_PAGE_PARAM = "#offset+':'+#size";
    private static final String ALPHA = "@";
    private static final String PAGE_KEY_SET = "pageParamSet";
    private ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (CONTEXT == null) {
            CONTEXT = applicationContext;
        }
    }

    @Autowired
    private CacheManager cacheManager;
    private Executor asyncExecutor;

    @Pointcut("@annotation(cn.hnit.starter.cache.annotation.Cacheable4Page)")
    public void cachePageAspect() {
    }

    @Pointcut("@annotation(cn.hnit.starter.cache.annotation.CacheEvict4Page)")
    public void cacheEvictPageAspect() {
    }

    @Pointcut("@annotation(cn.hnit.starter.cache.annotation.Caching4Page)")
    public void caching4PageAspect() {
    }

    @Around("cachePageAspect()")
    public Object cachePageAround(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Cacheable4Page annotation = method.getAnnotation(Cacheable4Page.class);

        String keyExp = annotation.pageKey();
        String pageParam = annotation.pageParam();
        if (annotation.cacheName().equals(annotation.itemCacheName())) {
            throw new IllegalArgumentException("@Cacheable4Page cacheName could not be same with itemCacheName");
        }
        Cache pageCache = cacheManager.getCache(annotation.cacheName());
        Cache itemCache = cacheManager.getCache(annotation.itemCacheName());

        Object[] args = point.getArgs();

        String keyField = annotation.itemKeyField();
        if (!Iterable.class.isAssignableFrom(method.getReturnType())) {
            throw new UnsupportedOperationException("Unsupported return-type , only Iterable");
        }

        LocalVariableTableParameterNameDiscoverer localVariableTable = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = localVariableTable.getParameterNames(method);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        String pageKey = getPageKey(keyExp, parser, context);

        String paramKey = parser.parseExpression(pageParam).getValue(context, String.class);
        String actualKey = pageKey + ALPHA + paramKey;

        //按id到缓存中查找，只要一个未命中，就从数据库中查找
        List<Object> result = new ArrayList<>();
        Cache.ValueWrapper ids = pageCache.get(actualKey);
        if (ids != null) {
            Iterable it = (Iterable) ids.get();
            Iterator iterator = it.iterator();
            while (iterator.hasNext()) {
                Cache.ValueWrapper itemVal = itemCache.get(iterator.next());
                if (itemVal == null) {
                    break;
                }
                result.add(itemVal.get());
            }
            return result;
        }

        Object ret = point.proceed(args);
        Iterable notCacheRet = (Iterable) ret;
        if (notCacheRet != null) {
            Iterator iterator = notCacheRet.iterator();
            List<Object> keys = new ArrayList<>();
            Map<Object, Object> kv = new HashMap<>();
            while (iterator.hasNext()) {
                Object item = iterator.next();
                Field field = ReflectionUtils.findField(item.getClass(), keyField);
                field.setAccessible(true);
                Object key = ReflectionUtils.getField(field, item);
                kv.put(key, item);
                keys.add(key);
            }
            if (!CollectionUtils.isEmpty(kv)) {
                ((RedisCaffeineCache) itemCache).putAll(kv);
            }

            Cache keySetCache = cacheManager.getCache(annotation.cacheName() + ALPHA + PAGE_KEY_SET);
            Cache.ValueWrapper valueWrapper = keySetCache.get(pageKey);
            if (valueWrapper != null) {
                HashSet set = (HashSet) valueWrapper.get();
                set.add(actualKey);
                keySetCache.put(pageKey, set);
            } else {
                Set<String> set = new HashSet();
                set.add(actualKey);
                keySetCache.put(pageKey, set);
            }

            pageCache.put(actualKey, keys);
        }
        return ret;
    }

    @Around("cacheEvictPageAspect()")
    public Object cacheEvictPageAround(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        CacheEvict4Page annotation = method.getAnnotation(CacheEvict4Page.class);
        if (annotation.async()) {
            getAsyncExecutor(annotation.executor()).execute(() -> doEvictPageKeys(method, point, annotation));
        } else {
            doEvictPageKeys(method, point, annotation);
        }

        return point.proceed();
    }

    @Around("caching4PageAspect()")
    public Object caching4PageAround(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Caching4Page annotation = method.getAnnotation(Caching4Page.class);
        CacheEvict4Page[] cacheEvict4Pages = annotation.evict();
        for (CacheEvict4Page evict4Page : cacheEvict4Pages) {
            if (evict4Page.async()) {
                getAsyncExecutor(evict4Page.executor()).execute(() -> doEvictPageKeys(method, point, evict4Page));
            } else {
                doEvictPageKeys(method, point, evict4Page);
            }
        }

        return point.proceed();
    }

    private Executor getAsyncExecutor(String executor) {
        if (asyncExecutor != null) {
            return asyncExecutor;
        }
        if (StringUtils.hasText(executor)) {
            asyncExecutor = getBean(executor, Executor.class);
        } else {
            asyncExecutor = getBean(Executor.class);
        }
        if (asyncExecutor == null) {
            throw new UnsupportedOperationException("@CacheEvict4Page annotation need an executor bean for async");
        }
        return asyncExecutor;
    }

    private void doEvictPageKeys(Method method, ProceedingJoinPoint point, CacheEvict4Page annotation) {
        String keyExp = annotation.pageKey();
        LocalVariableTableParameterNameDiscoverer localVariableTable = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = localVariableTable.getParameterNames(method);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = point.getArgs();

        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }

        String pageKey = getPageKey(keyExp, parser, context);

        Cache keySetCache = cacheManager.getCache(annotation.cacheName() + ALPHA + PAGE_KEY_SET);
        Cache.ValueWrapper valueWrapper = keySetCache.get(pageKey);
        if (valueWrapper != null) {
            HashSet set = (HashSet) valueWrapper.get();
            if (log.isDebugEnabled()) {
                log.debug("分页缓存中每个分页的 key : {}", set);
            }
            if (set != null && !set.isEmpty()) {
                Cache cache = cacheManager.getCache(annotation.cacheName());
                ((RedisCaffeineCache) cache).evictAll(set);
                keySetCache.evict(pageKey);
            }
        }
    }

    private String getPageKey(String keyExp, ExpressionParser parser, StandardEvaluationContext context) {
        String pageKey = "";
        if (StringUtils.hasText(keyExp)) {
            try {
                pageKey = parser.parseExpression(keyExp).getValue(context, String.class);
            } catch (Exception ex) {
                log.debug("非el表达式");
                pageKey = keyExp;
            }
        }
        return pageKey;
    }

    private <T> T getBean(Class<T> clazz) {
        if (CONTEXT == null) {
            log.warn("context is null, please init the context first");
        }
        try {
            return CONTEXT.getBean(clazz);
        } catch (Exception e) {
            log.error("error@getBean:" + clazz.getName(), e);
            return null;
        }
    }

    private <T> T getBean(String name, Class<T> clazz) {
        if (CONTEXT == null) {
            log.warn("context is null, please init the context first");
        }
        try {
            return CONTEXT.getBean(name, clazz);
        } catch (Exception e) {
            log.error("error@getBean:" + name, e);
            return null;
        }
    }
}


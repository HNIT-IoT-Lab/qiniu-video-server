package cn.hnit.starter.intercept.aop;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.starter.annotation.Lock;
import cn.hnit.starter.annotation.LockParam;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.util.concurrent.AtomicLongMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面，具体逻辑实现处
 *
 * @author 梁峰源
 * @since  2022年9月25日17:28:36
 */
@Aspect
@Slf4j
public class LockAop extends BaseAop {

    /**
     * 锁key前缀
     */
    private static final String LOCK_KEY_PREFIX = "buge:lock:";

    /**
     * 等待队列数
     */
    private static final AtomicLongMap<String> WAITING_MAP = AtomicLongMap.create();

    /**
     * 存放RLock线程副本
     */
    private static final ThreadLocal<Map<String, RLock>> R_LOCK_THREAD_LOCAL = new ThreadLocal<>();

    @Autowired(required = false)
    private Redisson redisson;
    
    /**
     * 切面
     *
     * @param lock
     */
    @Pointcut(value = "@annotation(lock)", argNames = "lock")
    public void lockPointcut(Lock lock) {
    }

    /**
     * 在业务方法执行中加入获取分布式锁操作
     *
     * @param pjp  pjp
     * @param lock lock 注解
     * @return 方法执行结果
     * @throws Throwable
     */
    @Around(value = "lockPointcut(lock)")
    public Object around(final ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        Method method = getTargetMethod(pjp);
        log.debug("pointcut @{}", method.getName());
        String lockKey = generateLockKey(lock, pjp, method);
        try {
            // 尝试获取锁
            lock(lockKey, lock);
            return pjp.proceed();
        } finally {
            // 释放锁
            unlock(lockKey);
        }
    }

    /**
     * 根据类名+方法名+参数值生成锁key
     *
     * @param lock   lock注解
     * @param pjp    pjp
     * @param method method
     * @return key
     */
    private String generateLockKey(Lock lock, ProceedingJoinPoint pjp, Method method) {
        StringBuilder keyBuffer = new StringBuilder(LOCK_KEY_PREFIX);

        // 自定义锁名
        if (StringUtils.isNotBlank(lock.lockKey())) {
            keyBuffer.append(lock.lockKey());
        } else {
            // 默认锁名取类目+方法名
            keyBuffer.append(method.getDeclaringClass().getSimpleName()).append(":").append(method.getName());
        }

        // 获取LockParam组合成锁key
        List<Pair<LockParam, Object>> pairs =
                getMethodAnnotationAndParametersByAnnotation(pjp, method, LockParam.class);
        for (Pair<LockParam, Object> pair : pairs) {
            LockParam lockParam = pair.getKey();
            // 默认直接取参数值，设置了LockParam值时，支持按SpEL取属性值
            Object param = pair.getValue();
            packParamKey(keyBuffer, param, lockParam.value());
        }

        log.debug("lock key={}", keyBuffer);
        return keyBuffer.toString();
    }

    /**
     * 执行加锁操作
     *
     * @param lockKey lockKey
     * @param lock    lock
     */
    private void lock(String lockKey, Lock lock) {
        // 1. 尝试获取锁
        boolean isLocked = tryLock(lockKey, lock);
        if (isLocked) {
            log.debug("lock success, lockKey: {}", lockKey);
            return;
        }

        // 2. 锁不成功时, 判断下锁的策略
        // 2.1 直接拒绝
        if (lock.lockStrategy() == Lock.LockStrategy.REJECT) {
            log.debug("try to lock {} failed and rejected directly", lockKey);
            throw new AppException(lock.errorCode(), lock.rejectMessage());
        }

        // 2.2 等待锁释放, 重新尝试获取锁, 等待队列多了一个
        long waitingCount = WAITING_MAP.incrementAndGet(lockKey);
        log.debug("waiting queue size for [{}] is {}, max is {}.", lockKey, waitingCount, lock.maxWaiting());
        try {
            // 等待队列已满, 直接拒绝
            if (waitingCount > lock.maxWaiting()) {
                throw new AppException(lock.errorCode(), lock.rejectMessage());
            }
            // 等待过程尝试获取锁, 直至获取成功或到达最大重试次数
            isLocked = retryLock(lockKey, lock);
            if (!isLocked) {
                throw new AppException(lock.errorCode(), lock.rejectMessage());
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            // 排队数-1
            WAITING_MAP.decrementAndGet(lockKey);
        }
    }

    /**
     * 尝试加锁处理
     *
     * @param lockKey lockKey
     * @param lock    lock
     * @return 是否加锁成功
     */
    private boolean tryLock(String lockKey, Lock lock) {
        Map<String, RLock> lockCache = R_LOCK_THREAD_LOCAL.get();
        if (lockCache == null) {
            lockCache = new HashMap<>();
            R_LOCK_THREAD_LOCAL.set(lockCache);
        }

        RLock rLock = lockCache.computeIfAbsent(lockKey, redisson::getLock);

        boolean isLocked = false;
        try {
            isLocked = rLock.tryLock(lock.ttl(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("获取锁超时，请重试", e);
            Thread.currentThread().interrupt();
        }
        return isLocked;
    }

    /**
     * 重试加锁
     *
     * @param lockKey lockKey
     * @param lock    lock
     * @return 是否加锁成功
     * @throws InterruptedException
     */
    private boolean retryLock(String lockKey, Lock lock) throws InterruptedException {
        boolean isLocked = false;
        // 根据定义的重试次数不断重试
        for (int i = 0; i < lock.maxRetry(); i++) {
            // 睡一会, 再尝试
            Thread.sleep(lock.retryInterval());
            isLocked = tryLock(lockKey, lock);
            // 获取锁成功，退出重试
            if (isLocked) {
                break;
            }
        }
        return isLocked;
    }

    /**
     * 解锁
     */
    private void unlock(String key) {
        Map<String, RLock> rLockCache = R_LOCK_THREAD_LOCAL.get();
        if (rLockCache != null) {
            RLock rLock = R_LOCK_THREAD_LOCAL.get().remove(key);
            if (rLock != null) {
                rLock.unlock();
            }
            if (CollectionUtil.isEmpty(rLockCache)) {
                R_LOCK_THREAD_LOCAL.remove();
            }
        }
    }
}

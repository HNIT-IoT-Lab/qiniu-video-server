package cn.hnit.common.redis.operator;

import cn.hnit.common.exception.base.AppException;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * redis操作类，集成redission分布式锁
 *
 * @author king
 * @since 2022-10-08 20:22
 **/
@Slf4j
@Component
public class RedisOperator {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private Redisson redisson;

    /**
     * 获取redis锁
     *
     * @param key key
     * @return 锁
     */
    public RLock getLock(String key) {
        return redisson.getLock(key);
    }

    /**
     * redis上锁
     *
     * @param key key
     * @return 锁
     */
    public RLock lock(String key) {
        RLock lock = getLock(key);
        try {
            lock.lockInterruptibly();
            return lock;
        } catch (InterruptedException e) {
            log.error("获取锁失败:\n", e);
        }
        return null;
    }

    /**
     * redis上锁
     *
     * @param key key
     * @return 锁
     */
    public RLock lock(String key, long time, TimeUnit unit) {
        RLock lock = getLock(key);
        try {
            // 默认5倍时长尝试
            if (lock.tryLock(time, time * 5, unit)) {
                return lock;
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败:\n", e);
        }
        return null;
    }

    /**
     * redis解锁
     *
     * @param lock 锁
     */
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * redis解锁
     *
     * @param locks 锁
     */
    public void unlock(Collection<RLock> locks) {
        if (CollUtil.isEmpty(locks)) {
            return;
        }
        locks.forEach(Lock::unlock);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 是否
     */
    public boolean expire(String key, long time, TimeUnit unit) {
        if (CharSequenceUtil.isEmpty(key) || time < 1) {
            return false;
        }
        try {
            Boolean b = redisTemplate.expire(key, time, unit);
            log.info("redis设置过期时间: key = {}, time = {}, unit = {}", key, time, unit);
            return b != null && b;
        } catch (Exception e) {
            log.error("redis 设置过期时间异常=", e);
            return false;
        }
    }

    /**
     * 指定一批key缓存失效时间
     *
     * @param keys 键
     * @param time 时间(秒)
     * @return 是否
     */
    public boolean expire(long time, TimeUnit unit, String... keys) {
        try {
            Arrays.stream(keys).forEach(key -> expire(key, time, unit));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean has(String key) {
        Boolean b = redisTemplate.hasKey(key);
        return b != null && b;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public long getExpire(String key) {
        return Optional.ofNullable(redisTemplate.getExpire(key)).orElse(0L);
    }

    /**
     * 根据键删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    public void del(String... keys) {
        if (ArrayUtil.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(Arrays.asList(keys));
    }

    /**
     * 根据键删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    public void del(Collection<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public <T> boolean set(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置key异常=", e);
            return false;
        }
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public <T> boolean set(String key, T value, long expire) {
        try {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("设置key异常=", e);
            return false;
        }
    }

    /**
     * 批量获取普通缓存
     * <p>
     * 有序返回值，没有的key值为null
     *
     * @param keys key
     * @param <T>  泛型T
     * @return 对象T
     */
    public <T> List<T> list(Collection<String> keys, Class<T> t) {
        List<Object> list = redisTemplate.opsForValue().multiGet(keys);
        if (log.isDebugEnabled()) {
            log.debug("通过keys={}在Redis中批量获取到的缓存value={}", keys, list);
        }
        return CollUtil.isEmpty(list) ? Collections.emptyList() : list.stream().map(o -> t.isInstance(o) ? (T) o : null).collect(Collectors.toList());
    }

    /**
     * 普通缓存获取
     *
     * @param key key
     * @return 对象T
     */
    public String get(String key) {
        return get(key, String.class);
    }

    /**
     * 普通缓存获取
     *
     * @param key key
     * @param t   类
     * @param <T> 泛型T
     * @return 对象T
     */
    public <T> T get(String key, Class<T> t) {
        Object o = redisTemplate.opsForValue().get(key);
        if (log.isDebugEnabled()) {
            log.debug("通过key={}在Redis中获取到的缓存value={}", key, o);
        }
        return t.isInstance(o) ? (T) o : null;
    }

    /**
     * 普通缓存获取
     *
     * @param key key
     * @return 对象T
     */
    public Long getLong(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        return o == null ? 0L : NumberUtil.isNumber(o.toString()) ? Long.parseLong(o.toString()) : 0L;
    }

    /**
     * 获取整数
     *
     * @param key key
     * @return int
     */
    public Integer getInt(String key) {
        return get(key, Integer.class);
    }

    /**
     * 设置普通缓存，返回旧值
     *
     * @param key   key
     * @param value 类
     * @param <T>   泛型T
     * @return 对象T
     */
    public <T> T put(String key, T value) {
        Object o = redisTemplate.opsForValue().getAndSet(key, value);
        if (log.isDebugEnabled()) {
            log.debug("通过key={}在Redis中获取到的缓存value={}", key, o);
        }
        return o == null ? null : (T) o;
    }

    /**
     * 自增方法
     *
     * @param key   key
     * @param delta 增量
     */
    public Long inc(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 列表方法
     */
    public abstract class ListOp {

        /**
         * 重置列表
         * <p>
         * 此方法调用后，只锁该方法，即调用该方法的地方会等待，其他地方依然会有并发问题
         *
         * @param key    key
         * @param values 数据集
         */
        public void reset(String key, Collection<?> values) {
            RLock lock = lock(key);
            if (lock == null) {
                log.warn("redis重置列表失败, 锁获取失败: key = {}, values = {}", key, values);
                throw AppException.pop("重置列表失败");
            }
            try {
                // 删除key
                del(key);
                // 加入
                rightPushAll(key, values);
            } finally {
                unlock(lock);
            }
        }

        /**
         * 从右保存所有列表数据至list
         *
         * @param key    key
         * @param values 数据集
         */
        public void rightPushAll(String key, Collection<?> values) {
            if (CharSequenceUtil.isEmpty(key) || CollUtil.isEmpty(values)) {
                log.warn("列表保存错误：key: {} -> value: {}", key, values);
                return;
            }
            try {
                redisTemplate.opsForList().rightPushAll(key, values.toArray());
            } catch (Exception ex) {
                log.error("redis push all 异常!\n", ex);
            }
        }

        /**
         * 范围获取list
         *
         * @param key key
         * @param idx 下标
         * @param len 长度
         * @param t   泛型类
         * @param <T> 泛型
         * @return 结果集
         */
        public <T> List<T> range(String key, long idx, long len, Class<T> t) {
            List<Object> range = redisTemplate.opsForList().range(key, idx, idx + len - 1);
            if (CollUtil.isEmpty(range)) {
                return Collections.emptyList();
            }
            if (t.isInstance(range.get(0))) {
                return range.stream().map(o -> {
                    if (!t.isInstance(o)) {
                        log.warn("列表中获取的{}与泛型{}不一致", o, t.getName());
                        throw AppException.pop("redis列表值转换错误");
                    }
                    return (T) o;
                }).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        /**
         * 获取list长度
         *
         * @param key key
         * @return 结果集
         */
        public long size(String key) {
            return Optional.ofNullable(redisTemplate.opsForList().size(key)).orElse(0L);
        }

        /**
         * 获取列表所有数据
         *
         * @param key key
         * @param <T> 泛型
         * @return 结果集
         */
        public <T> List<T> all(String key) {
            return Optional.ofNullable(redisTemplate.opsForList().range(key, 0, -1)).orElse(Collections.emptyList())
                    .stream().map(o -> (T) o).collect(Collectors.toList());
        }
    }

    /**
     * Set方法
     */
    public abstract class SetOp {

        /**
         * 两个集合的交集
         *
         * @param key   key
         * @param other otherKey
         * @param <T>   T
         * @return 是否
         */
        public <T> Set<T> inter(String key, String other) {
            return Optional.ofNullable(redisTemplate.opsForSet().intersect(key, other))
                    .orElse(Collections.emptySet()).stream().map(m -> (T) m).collect(Collectors.toSet());
        }

        /**
         * member是否存在set中
         *
         * @param key    key
         * @param member member
         * @return 是否
         */
        public boolean has(String key, Object member) {
            return Optional.ofNullable(redisTemplate.opsForSet().isMember(key, member)).orElse(Boolean.FALSE);
        }

        /**
         * 将members存入set中
         *
         * @param key     key
         * @param members members
         */
        public long putAll(String key, Collection<?> members) {
            return Optional.ofNullable(redisTemplate.opsForSet().add(key, members.toArray())).orElse(0L);
        }

        /**
         * 将member存入set中
         *
         * @param key    key
         * @param member member
         */
        public long put(String key, Object... member) {
            return Optional.ofNullable(redisTemplate.opsForSet().add(key, member)).orElse(0L);
        }

        /**
         * 将members移除set中
         *
         * @param key     key
         * @param members members
         */
        public long remove(String key, Collection<?> members) {
            return Optional.ofNullable(redisTemplate.opsForSet().remove(key, members.toArray())).orElse(0L);
        }

        /**
         * 将member移除set中
         *
         * @param key     key
         * @param members members
         */
        public long remove(String key, Object... members) {
            return Optional.ofNullable(redisTemplate.opsForSet().remove(key, members)).orElse(0L);
        }

        /**
         * 获取集合大小
         *
         * @param key key
         */
        public long size(String key) {
            return Optional.ofNullable(redisTemplate.opsForSet().size(key)).orElse(0L);
        }

        /**
         * 获取集合的所有元素
         *
         * @param key key
         * @param <T> 泛型
         * @return 结果集
         */
        public <T> Set<T> all(String key) {
            return Optional.ofNullable(redisTemplate.opsForSet().members(key)).orElse(Collections.emptySet())
                    .stream().map(m -> (T) m).collect(Collectors.toSet());
        }
    }

    /**
     * ZSet方法
     */
    public abstract class ZSetOp {

        /**
         * ZSet的member分数更新或添加
         *
         * @param key    key
         * @param member 成员
         * @param score  分数
         */
        public void add(String key, Object member, double score) {
            redisTemplate.opsForZSet().add(key, member, score);
        }

        /**
         * ZSet的members分数更新或添加
         *
         * @param key     key
         * @param members members
         */
        public void add(String key, Set<ZSetOperations.TypedTuple<Object>> members) {
            if (CollUtil.isEmpty(members)) {
                return;
            }
            redisTemplate.opsForZSet().add(key, members);
        }

        /**
         * ZSet的member分数自增
         *
         * @param key    key
         * @param member member
         * @param inc    inc
         */
        public Double inc(String key, Object member, double inc) {
            return redisTemplate.opsForZSet().incrementScore(key, member, inc);
        }

        /**
         * 返回ZSet的member分数
         *
         * @param key    key
         * @param member member
         */
        public Double score(String key, Object member) {
            return redisTemplate.opsForZSet().score(key, member);
        }

        /**
         * 返回ZSet的满足(min<= score <= max)的member总数
         *
         * @param key key
         * @param min 最小分数
         * @param max 最大分数
         */
        public long count(String key, double min, double max) {
            return Optional.ofNullable(redisTemplate.opsForZSet().count(key, min, max)).orElse(0L);
        }

        /**
         * 获取ZSet中，分数在(min<= score <= max)的成员，排序：min -> max
         *
         * @param key key
         * @param min 最小分数
         * @param max 最大分数
         */
        public Set<ZSetOperations.TypedTuple<Object>> rangeByScoreWithScores(String key, double min, double max) {
            return Optional.ofNullable(redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max)).orElse(Collections.emptySet());
        }

        /**
         * 获取ZSet中，排名在(min<= rank <= max)的成员，排序：max -> min
         *
         * @param key key
         * @param min 最小下标
         * @param max 最大下标
         */
        public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String key, long min, long max) {
            return Optional.ofNullable(redisTemplate.opsForZSet().reverseRangeWithScores(key, min, max)).orElse(Collections.emptySet());
        }

        /**
         * 移除ZSet中，分数在(min<= score <= max)的成员
         *
         * @param key key
         * @param min 最小分数
         * @param max 最大分数
         */
        public long removeRangeByScore(String key, double min, double max) {
            return Optional.ofNullable(redisTemplate.opsForZSet().removeRangeByScore(key, min, max)).orElse(0L);
        }

        /**
         * 批量移除成员
         *
         * @param key     key
         * @param members 成员
         */
        public long remove(String key, Object... members) {
            return Optional.ofNullable(redisTemplate.opsForZSet().remove(key, members)).orElse(0L);
        }
    }

    /**
     * hash方法
     */
    public abstract class HashOp {

        /**
         * 获取值
         *
         * @param key key
         * @param k   键
         * @return 值
         */
        public Object get(String key, String k) {
            return redisTemplate.opsForHash().get(key, k);
        }

        /**
         * 获取值
         *
         * @param key key
         * @param k   键
         * @param v   值
         * @param <V> 值类型
         * @return 值
         */
        public <V> V get(String key, String k, Class<V> v) {
            Object o = redisTemplate.opsForHash().get(key, k);
            return v.isInstance(o) ? (V) o : null;
        }

        /**
         * 获取所有hash键值对
         *
         * @param key key
         */
        public Map<Object, Object> all(String key) {
            return redisTemplate.opsForHash().entries(key);
        }

        /**
         * 获取所有hash键值对，强转泛型
         *
         * @param key    key
         * @param vClass 值类型
         * @return map(k - > v)
         */
        public <V> Map<String, V> all(String key, Class<V> vClass) {
            return all(key).entrySet().stream()
                    .filter(e -> vClass.isInstance(e.getValue()))
                    .collect(Collectors.toMap(e -> e.getKey().toString(), e -> (V) e.getValue(), (v1, v2) -> v2));
        }

        /**
         * 获取所有hash键值对
         *
         * @param key key
         * @return map(k - > long)
         */
        public Map<String, Long> allLong(String key) {
            return all(key).entrySet().stream()
                    .filter(e -> e.getValue() != null && NumberUtil.isNumber(e.getValue().toString()))
                    .collect(Collectors.toMap(e -> e.getKey().toString(), e -> Long.parseLong(e.getValue().toString()), (v1, v2) -> v2));
        }

        /**
         * 获取相关的v值 --- 有序列表返回
         *
         * @param key    key
         * @param hashes 键列表
         * @param <V>    值泛型
         * @return 值列表
         */
        public <V> List<V> list(String key, List<Object> hashes) {
            return redisTemplate.opsForHash().multiGet(key, hashes).stream().map(o -> (V) o).collect(Collectors.toList());
        }

        /**
         * 设置键值对
         *
         * @param key key
         * @param k   键
         * @param v   值
         * @param <V> 值泛型
         */
        public <V> void put(String key, String k, V v) {
            redisTemplate.opsForHash().put(key, k, v);
        }

        /**
         * 设置所有的键值对
         *
         * @param key key
         * @param map 键值对
         */
        public void put(String key, Map<String, ?> map) {
            redisTemplate.opsForHash().putAll(key, map);
        }

        /**
         * 自增值
         *
         * @param key key
         * @param k   键
         * @param inc 增量
         * @return 新增后的量
         */
        public long inc(String key, String k, long inc) {
            return redisTemplate.opsForHash().increment(key, k, inc);
        }

        /**
         * 自增值
         *
         * @param key key
         * @param k   键
         * @param inc 增量
         * @return 新增后的量
         */
        public double inc(String key, String k, double inc) {
            return redisTemplate.opsForHash().increment(key, k, inc);
        }

        /**
         * 删除key中的某一个元素
         *
         * @param key 键
         * @param k   字段名
         */
        public Long del(String key, String k) {
            return redisTemplate.opsForHash().delete(key, k);
        }

        /**
         * 批量删除key中的元素
         *
         * @param key 键
         * @param k   字段名
         */
        public Long del(String key, Collection<String> k) {
            return redisTemplate.opsForHash().delete(key, k.toArray());
        }
    }
}

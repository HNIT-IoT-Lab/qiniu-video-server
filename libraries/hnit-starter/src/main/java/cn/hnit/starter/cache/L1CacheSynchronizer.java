package cn.hnit.starter.cache;

import java.util.List;

/**
 * 一级缓存（本地缓存）同步器
 *
 * @author 梁峰源
 * @since 2022年10月2日01:10:09
 */
public interface L1CacheSynchronizer {
    /**
     * 推送一条缓存进行同步
     */
    void pushMsg(String msg);

    /**
     * 批量推送缓存进行同步
     */
    void pushMsg(List<String> msgs);

    /**
     * 是否需要实时更新一级缓存
     *
     * @param realtime true 实时 false定时
     */
    void pullMsg(boolean realtime);

    /**
     * 开启缓存同步
     */
    void start();

}

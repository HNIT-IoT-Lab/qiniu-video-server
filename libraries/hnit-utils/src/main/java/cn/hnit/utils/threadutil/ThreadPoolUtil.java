package cn.hnit.utils.threadutil;

import cn.hnit.utils.logutil.LogUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.*;

/**
 * 线程池工具,对Runnable, Callable透传日志链路处理
 *
 * @author 梁峰源
 * @since 2022-08-22 21:54
 **/
@Slf4j
public final class ThreadPoolUtil {

    private ThreadPoolUtil() {
        throw new RuntimeException();
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   空闲线程存活时间
     * @param unit            时间单位
     * @param workQueue       堵塞队列
     * @param threadFactory   线程工程
     * @param handler         拒绝策略
     * @return {@link ThreadPoolExecutor}
     */
    public static ThreadPoolExecutor build(int corePoolSize,
                                           int maximumPoolSize,
                                           long keepAliveTime,
                                           TimeUnit unit,
                                           BlockingQueue<Runnable> workQueue,
                                           CustomizableThreadFactory threadFactory,
                                           RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler) {
            @Override
            public void execute(Runnable task) {
                showThreadPoolInfo(this, workQueue.size());
                super.execute(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
            }

            @Override
            public Future<?> submit(Runnable task) {
                showThreadPoolInfo(this, workQueue.size());
                return super.submit(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                showThreadPoolInfo(this, workQueue.size());
                return super.submit(LogUtils.wrap(task, MDC.getCopyOfContextMap()), result);
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                showThreadPoolInfo(this, workQueue.size());
                return super.submit(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
            }
        };
    }


    private static void showThreadPoolInfo(ThreadPoolExecutor threadPoolExecutor, Integer maxTaskNum) {
        if (null == threadPoolExecutor) {
            return;
        }

        if (maxTaskNum == null || maxTaskNum < 0) {
            maxTaskNum = 1;
        }

        String threadName = "";
        if (threadPoolExecutor.getThreadFactory() instanceof CustomizableThreadFactory) {
            threadName = ((CustomizableThreadFactory) threadPoolExecutor.getThreadFactory()).getThreadNamePrefix();
        } else {
            threadName = threadPoolExecutor.getThreadFactory().getClass().getName();
        }
        if (ThreadLocalRandom.current().nextInt(100) <= 10) { // 10%采样输出
            log.info("线程池状态监控 ThreadNamePrefix:{}, taskCount [{}], completedTaskCount [{}], activeCount [{}], queueSize [{}]",
                    threadName,
                    threadPoolExecutor.getTaskCount(),
                    threadPoolExecutor.getCompletedTaskCount(),
                    threadPoolExecutor.getActiveCount(),
                    threadPoolExecutor.getQueue().size()
            );
            if (threadPoolExecutor.getQueue().size() >= maxTaskNum) {
                log.info("线程池状态监控, 队列已满 ThreadNamePrefix:{}, taskCount [{}], completedTaskCount [{}], activeCount [{}], queueSize [{}]",
                        threadName,
                        threadPoolExecutor.getTaskCount(),
                        threadPoolExecutor.getCompletedTaskCount(),
                        threadPoolExecutor.getActiveCount(),
                        threadPoolExecutor.getQueue().size()
                );
            }
        }
    }


    /**
     * 创建线程池
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param handler
     * @param name
     * @return
     */
    public static ThreadPoolExecutor build(int corePoolSize,
                                           int maximumPoolSize,
                                           long keepAliveTime,
                                           TimeUnit unit,
                                           BlockingQueue<Runnable> workQueue,
                                           RejectedExecutionHandler handler,
                                           String name) {
        return build(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new CustomizableThreadFactory(name), handler);
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param name
     * @return
     */
    public static ThreadPoolExecutor build(Integer corePoolSize,
                                           Integer maximumPoolSize,
                                           Long keepAliveTime,
                                           TimeUnit unit,
                                           String name) {
        return build(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>(),
                new CustomizableThreadFactory(name), new ThreadPoolExecutor.AbortPolicy());

    }

    /**
     * 创建线程池
     */
    public static ThreadPoolExecutor build(String name,
                                           Integer corePoolSize,
                                           Integer maximumPoolSize,
                                           BlockingQueue<Runnable> workQueue) {
        return build(corePoolSize,
                maximumPoolSize,
                5L,
                TimeUnit.SECONDS,
                workQueue,
                new CustomizableThreadFactory(name),
                // 由调用者自己执行
                new ThreadPoolExecutor.CallerRunsPolicy());
    }


    /**
     * 创建线程池
     *
     * @param name
     * @param thread
     * @return
     */
    public static ThreadPoolExecutor build(String name, Integer thread) {
        return build(thread, thread, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new CustomizableThreadFactory(name), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 创建 ScheduledExecutorService
     *
     * @param corePoolSize 核心线程数
     * @return
     */
    public static ScheduledExecutorService buildScheduledExecutorService(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize) {
            @Override
            public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
                return super.schedule(LogUtils.wrap(command, MDC.getCopyOfContextMap()), delay, unit);
            }

            @Override
            public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
                return super.schedule(LogUtils.wrap(callable, MDC.getCopyOfContextMap()), delay, unit);
            }

            @Override
            public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
                return super.scheduleAtFixedRate(LogUtils.wrap(command, MDC.getCopyOfContextMap()), initialDelay, period, unit);
            }

            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
                return super.scheduleWithFixedDelay(LogUtils.wrap(command, MDC.getCopyOfContextMap()), initialDelay, delay, unit);
            }

            @Override
            public void execute(Runnable command) {
                super.execute(LogUtils.wrap(command, MDC.getCopyOfContextMap()));
            }

            @Override
            public Future<?> submit(Runnable task) {
                return super.submit(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                return super.submit(LogUtils.wrap(task, MDC.getCopyOfContextMap()), result);
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return super.submit(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
            }
        };
    }


    /**
     * 创建Spring自带线程池
     *
     * @param corePoolSize                     核心线程
     * @param maxPoolSize                      最大线程
     * @param queueCapacity                    队列数
     * @param threadNamePrefix                 线程池名称
     * @param waitForTasksToCompleteOnShutdown
     * @return
     */
    public static ThreadPoolTaskExecutor buildThreadPoolTaskExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String threadNamePrefix, boolean waitForTasksToCompleteOnShutdown) {
        MyThreadPoolTaskExecutor executor = new MyThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.initialize();
        return executor;
    }

    @Data
    public static class MyThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

        @Override
        public void execute(Runnable task) {
            super.execute(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
        }

        @Override
        public void execute(Runnable task, long startTimeout) {
            super.execute(LogUtils.wrap(task, MDC.getCopyOfContextMap()), startTimeout);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return super.submit(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return super.submit(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
        }

        @Override
        public ListenableFuture<?> submitListenable(Runnable task) {
            return super.submitListenable(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
        }

        @Override
        public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
            return super.submitListenable(LogUtils.wrap(task, MDC.getCopyOfContextMap()));
        }
    }


}

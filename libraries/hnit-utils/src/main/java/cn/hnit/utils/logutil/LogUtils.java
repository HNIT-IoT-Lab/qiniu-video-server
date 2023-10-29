package cn.hnit.utils.logutil;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 日志工具, 用于存储整个调用链路的链路id(traceId), 用户id(userId), 用户ip(userIp)
 *
 * @author 梁峰源
 * @since 2022-08-22 21:54
 **/
@Slf4j
public final class LogUtils {

    /**
     * 链路id
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 用户id
     */
    public static final String USER_ID = "userId";

    /**
     * 用户ip
     */
    public static final String USER_IP = "userIp";

    /**
     * 平台信息
     */
    public static final String PLATFORM = "platform";

    /**
     * imei 唯一标识
     */
    public static final String DEVICE_ID = "deviceId";

    /**
     * 渠道来源: oppo / vivo等
     */
    public static final String COME_FROM = "comeFrom";


    /**
     * 访问端口
     */
    public static final String PORT = "port";


    /**
     * 用户号
     */
    public static final String USER_NUMBER = "userNumber";


    /**
     * 用户token
     */
    public static final String TOKEN = "token";


    /**
     * 请求url
     */
    public static final String URL = "url";

    /** PC版本号*/
    public static final String PC_VERSION = "pcVersion";

    public static final String APP_VERSION = "appVersion";

    /**
     * 给当前线程设置traceId
     */
    public static void setTraceId() {
        MDC.put(TRACE_ID, randomTraceId());
    }

    /**
     * 给当前线程设置traceId --- 建议传递时使用
     *
     * @param traceId 指定traceId
     */
    public static void setTraceId(String traceId) {
        MDC.put(LogUtils.TRACE_ID, traceId);
    }

    /**
     * 获取当前线程设置traceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 获取当前线程设置userId
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }

    /**
     * 获取当前线程设置userIp
     */
    public static String getUserIp() {
        return MDC.get(USER_IP);
    }

    /**
     * 获取当前线程的平台platFrom
     */
    public static String getPlatform() {
        return MDC.get(PLATFORM);
    }

    /**
     * 获取当前线程的imei 唯一标识
     */
    public static String getDeviceId() {
        return MDC.get(DEVICE_ID);
    }

    /**
     * 获取当前线程的平台platFrom
     */
    public static String getComeFrom() {
        return MDC.get(COME_FROM);
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public static void setUserId(String userId) {
        MDC.put(USER_ID, userId);
    }


    /**
     * 设置端口号
     *
     * @param port
     */
    public static void setPort(String port) {
        MDC.put(PORT, port);
    }


    /**
     * 设置端口号
     *
     * @param url
     */
    public static void setUrl(String url) {
        MDC.put(URL, url);
    }


    /**
     * 设置token
     *
     * @param token
     */
    public static void setToken(String token) {
        MDC.put(TOKEN, token);
    }


    /**
     * 设置token
     *
     * @param userNumber
     */
    public static void setUserNumber(String userNumber) {
        MDC.put(USER_NUMBER, userNumber);
    }

    public static String getUserNumber() {
        return MDC.get(USER_NUMBER);
    }

    /**
     * 用户ip
     *
     * @param userIp 用户ip
     */
    public static void setUserIp(String userIp) {
        MDC.put(USER_IP, userIp);
    }

    /**
     * 设置平台信息
     *
     * @param platform
     */
    public static void setPlatform(String platform) {
        MDC.put(PLATFORM, platform);
    }

    public static void setDeviceId(String deviceId) {
        MDC.put(DEVICE_ID, deviceId);
    }

    public static void setComeFrom(String comeForm) {
        MDC.put(COME_FROM, comeForm);
    }

    public static void setPcVersion(String pcVersion) {
        MDC.put(PC_VERSION, pcVersion);
    }

    public static void setAppVersion(String appVersion) {
        MDC.put(APP_VERSION, appVersion);
    }

    /**
     * 获取一个traceId
     */
    private static String randomTraceId() {

        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 清空traceId
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID);
    }

    /**
     * 清除调用链路保存信息
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * 对 runnable封装一层, 将调用线程的MDC traceId 透传到异步线程中
     *
     * @param runnable
     * @param context
     * @return
     */
    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("ThreadPoolExecuteError", e);
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 对callable封装一层, 将调用线程的MDC traceId 透传到异步线程中
     *
     * @param callable
     * @param context
     * @param <T>
     * @return
     */
    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                return callable.call();
            } catch (InterruptedException | ExecutionException e) {
                log.error("ThreadPoolExecuteError", e);
                return null;
            } finally {
                MDC.clear();
            }
        };
    }

    public static void setTraceIdIfAbsent() {
        if (MDC.get(TRACE_ID) == null) {
            MDC.put(TRACE_ID, randomTraceId());
        }
    }
}

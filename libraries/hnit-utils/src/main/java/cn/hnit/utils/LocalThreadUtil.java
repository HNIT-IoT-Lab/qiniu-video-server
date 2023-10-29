package cn.hnit.utils;

import cn.hnit.utils.common.bean.HeaderParam;
import cn.hnit.utils.common.bean.TradeRecord;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * 线程工具类
 */
@Slf4j
public class LocalThreadUtil {

    private static final ThreadLocal<Map<String, Object>> LOCAL_MAP = new InheritableThreadLocal<>();
    private static final ThreadLocal<Long> LOCAL_UID = new InheritableThreadLocal<>();

    private LocalThreadUtil() {

    }

    public static void setUid(Long uid) {
        LOCAL_UID.set(uid);
    }

    public static Long getUid() {
        return LOCAL_UID.get();
    }
    
    public static void setLocalMap(Map<String, Object> map) {
        LOCAL_MAP.set(map);
    }

    public static Map<String, Object> getLocalMap() {
        return LOCAL_MAP.get();
    }

    public static void remove() {
        LOCAL_MAP.remove();
        LOCAL_UID.remove();
    }

    public static void setLocalObj(Object object) {
        String key = object.getClass().getName();
        Map<String, Object> map = LOCAL_MAP.get();
        if (map == null) map = Maps.newHashMap();
        map.put(key, object);
        LOCAL_MAP.set(map);
    }

    public static <T> T getLocalObj(Class<T> tClass) {
        Map<String, Object> map = getLocalMap();
        if (Objects.nonNull(map)) {
            Object obj = map.get(tClass.getName());
            if (Objects.nonNull(obj)) {
                return tClass.cast(obj);
            }
        }
        return null;
    }

    public static <T> T getTrade(Class<T> tClass) {
        T trade = getLocalObj(tClass);
        if (trade == null) {
            try {
                trade = tClass.newInstance();
                setLocalObj(trade);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("newInstance error:{}", tClass.getName());
            }
        }
        return getLocalObj(tClass);
    }

    public static TradeRecord getTrade() {
        return getLocalObj(TradeRecord.class);
    }

    public static HeaderParam getHeader() {
        return getLocalObj(HeaderParam.class);
    }

    /**
     * 获取请求头版本号
     *
     * @return
     */
    public static Long getVersion() {
        HeaderParam header = getLocalObj(HeaderParam.class);
        String version = header.getVersion();
        Long versionLong = null;
        if (StringUtils.isNotBlank(version)) {
            try {
                version = version.replace(".", "");
                versionLong = Long.parseLong(version);
            } catch (Exception exception) {
                log.error("版本号解析错误:::", exception);
            }
        }
        return versionLong;
    }


}

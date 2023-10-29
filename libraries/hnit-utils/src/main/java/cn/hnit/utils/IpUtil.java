package cn.hnit.utils;

import cn.hnit.utils.common.bean.IpLocationResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liangfengyuan
 */
@Slf4j
public final class IpUtil {

    private IpUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final String UNK = "unknown";

    private static final String COMMA = ",";
    /**
     * 创蓝IP位置查询
     */
    private static final String SHUMEI_URL = "http://api-skynet-bj.fengkongcloud.com/v4/event";

    private static final String SHUMEI_ACCESS_KEY = "VscqhyPzvAVjT5s8X6V0";


    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isNotBlank(ip) && !UNK.equalsIgnoreCase(ip) && ip.contains(COMMA)) {
            ip = ip.split(COMMA)[0];
        }
        if (checkIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (checkIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (checkIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (checkIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (checkIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (checkIp(ip)) {
            ip = request.getRemoteAddr();
        }
        log.debug("获取客户端ip:{},用户设备deviceId:{},用户设备platform:{}", ip, request.getHeader("deviceId"), request.getHeader("platform"));
        return ip;
    }


    public static String getPort(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        int port = request.getRemotePort();
        return port + "";
    }

    private static boolean checkIp(String ip) {
        return StringUtils.isBlank(ip) || UNK.equalsIgnoreCase(ip);
    }

    /**
     * 获取平台信息: 1-Android, 2-IOS
     *
     * @param request
     * @return
     */
    public static String getPlatform(HttpServletRequest request) {
        String platform = request.getHeader("platform");
        return StringUtils.isNotBlank(platform) ? platform : StringUtils.EMPTY;
    }

    /**
     * 获取设置imei 唯一标识
     *
     * @return
     */
    public static String getDeviceId(HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        return StringUtils.isNotBlank(deviceId) ? deviceId : StringUtils.EMPTY;
    }

    /**
     * 获取渠道来源: oppo / vivo
     *
     * @param request
     * @return
     */
    public static String getComeFrom(HttpServletRequest request) {
        String comeFrom = request.getHeader("comeFrom");
        return StringUtils.isNotBlank(comeFrom) ? comeFrom : StringUtils.EMPTY;
    }

    /**
     * 获取一个IP的位置信息
     * @param ip ipv4地址
     * @return 获取失败返回null
     */
    public static IpLocationResp.LocationDetail getLocationByIp(String ip){
        JSONObject params = new JSONObject();
        params.put("accessKey",SHUMEI_ACCESS_KEY);
        params.put("eventId","task");
        params.put("appId","test");
        JSONObject data = new JSONObject();
        data.put("ip",ip);
        data.put("tokenId",String.valueOf(ThreadLocalRandom.current().nextInt()));
        data.put("timestamp",System.currentTimeMillis());
        data.put("deviceId",String.valueOf(ThreadLocalRandom.current().nextInt()));
        data.put("isTokenSeperate",1);
        params.put("data",data);
        String result = null;
        try {
            result = OkHttpUtil.postJsonParams(SHUMEI_URL, params.toJSONString());
            log.info("查询ip结果："+result);
        }catch (Exception e){
            log.info("查询IP错误："+e.getMessage());
        }

        return result==null ? null : JSON.parseObject(result,IpLocationResp.class).getDetail();
    }
}

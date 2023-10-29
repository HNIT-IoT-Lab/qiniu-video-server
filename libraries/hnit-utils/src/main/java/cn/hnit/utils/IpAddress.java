package cn.hnit.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * @Desc: 根据ip地址查询国家，城市
 * https://github.com/wp-statistics/GeoLite2-City.git
 * @Author：yezhongqiang
 * @CreateTime 2022/10/28 11:28
 */
public class IpAddress {

    private static final Logger log = LoggerFactory.getLogger(IpAddress.class);

    private static final String DEFAULT_COUNTRY_NAME = "中国";

    static DatabaseReader reader = null;

    /**
     * 创蓝查询IP归属地appId
     */
    private static final String APP_ID = "3MkPnL0d";

    /**
     * 创蓝查询IP归属地appKey
     */
    private static final String APP_KEY = "fZvJyiEh";

    /**
     * 创蓝查询IP归属地URL
     */
    private static final String CHUANGLAN_IP_AREA_URL = "http://api.253.com/open/ipgsdcx/ipgsd";

    /**
     * 内地省份直辖市信息
     */
    private static final List<String> DEFAULT_PROVINCE = Lists.newArrayList(
            "北京", "天津", "上海", "重庆",
            "内蒙古", "新疆", "西藏", "宁夏", "广西",
            "黑龙江", "吉林", "辽宁", "河北", "山西", "青海", "山东",
            "河南", "江苏", "安徽", "浙江", "福建", "江西", "湖南", "湖北", "广东", "海南", "甘肃", "四川", "贵州", "云南"
    );

    private static final Random random = new Random();

    private static String getRandomIpAddressLocation() {
        return DEFAULT_PROVINCE.get(random.nextInt(DEFAULT_PROVINCE.size()));
    }


    static {
        try {
            // 1. 定义资源匹配规则，会在所有的JAR包的根目录下搜索指定文件
            String matchPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "GeoLite2-City.mmdb";
            log.info("matchPattern: {}", matchPattern);
            // 2. 返回指定路径下所有的资源对象（子目录下的资源对象）
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources(matchPattern);
            log.info("resources: {}", resources.length);
            if (resources.length > 0) {
                String path = resources[0].getURL().getPath();
                log.info("path: {}", path);
                reader = new DatabaseReader.Builder(resources[0].getInputStream()).build();
                log.info("reader: {}", reader);
            }
            log.info("reader: {}", reader);
        } catch (IOException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }

    }

    public static String getCity(String ip) throws IOException, GeoIp2Exception {
        if (reader != null) {
            // 读取数据库内容
            InetAddress ipAddress = InetAddress.getByName(ip);
            // 获取查询结果
            CityResponse response = reader.city(ipAddress);
            //获取City
            City city = response.getCity();
            return city.getNames().get("zh-CN");
        }
        return null;
    }

    public static String getProvince(String ip) throws IOException, GeoIp2Exception {
        if (reader != null) {
            InetAddress ipAddress = InetAddress.getByName(ip);
            // 获取查询结果
            CityResponse response = reader.city(ipAddress);
            //获取Province
            Subdivision subdivision = response.getMostSpecificSubdivision();
            return subdivision.getNames().get("zh-CN");
        }
        return null;
    }


    public static String getCountry(String ip) throws IOException, GeoIp2Exception {
        if (reader != null) {
            InetAddress ipAddress = InetAddress.getByName(ip);
            // 获取查询结果
            CityResponse response = reader.city(ipAddress);
            //获取country
            Country country = response.getCountry();
            return country.getNames().get("zh-CN");
        }
        return null;
    }

    public static Map<String, String> getArea(String ip) throws IOException, GeoIp2Exception {
        if (reader != null) {
            InetAddress ipAddress = InetAddress.getByName(ip);
            // 获取查询结果
            CityResponse response = reader.city(ipAddress);
            //获取country
            String countryZh = response.getCountry().getNames().get("zh-CN");
            String provinceZh = response.getMostSpecificSubdivision().getNames().get("zh-CN");
            String cityZh = response.getCity().getNames().get("zh-CN");
            Map<String, String> map = new HashMap<>();
            map.put("country", countryZh);
            map.put("province", provinceZh);
            map.put("city", cityZh);
            return map;
        }
        return null;
    }

    /**
     * 获取ip对应的归属地
     * 国外IP返回国名
     * 国内IP返回到省一级
     */
    public static String getIpAddressLocation(String ip) {
        if (StringUtils.isBlank(ip) || ip.startsWith("127.0.0.1") || ip.startsWith("0.0.0.0")) {
            log.info("ip异常, 返回随机ip归属地, ip: {}", ip);
            return getRandomIpAddressLocation();
        }
        Map<String, String> areaMap = null;
        try {
            areaMap = getArea(ip);
        } catch (IOException | GeoIp2Exception e) {
            log.error("本地获取ip归属地错误, {}", e.getMessage(), e);
        }
        String area = null;
        if (areaMap != null) {
            area = getIpAddressLocation(areaMap);
        }
        if (area == null) {
            areaMap = getAreaByApi(ip);
            area = getIpAddressLocation(areaMap);
        }
        if (StringUtils.isNotBlank(area)) {
            return area;
        } else {
            log.info("未能获取到ip归属地, 返回随机ip归属地, ip: {}", ip);
            return getRandomIpAddressLocation();
        }
    }

    private static String getIpAddressLocation(Map<String, String> area) {
        String country = area.get("country");
        if (StringUtils.isNotBlank(country) && !DEFAULT_COUNTRY_NAME.equals(country)) {
            return country;
        }
        String province = area.get("province");
        if (StringUtils.isNotBlank(province)) {
            return province;
        }
        return null;
    }

    public static Map<String, String> getAreaByApi(String ip) {
        Map<String, String> params = Maps.newHashMap();
        params.put("appId", APP_ID);
        params.put("appKey", APP_KEY);
        params.put("IP", ip);
        String result = OkHttpUtil.post(CHUANGLAN_IP_AREA_URL, params);
        log.info(result);
        IpAddrRep ipAddrRep = JSON.parseObject(result, IpAddrRep.class);
        IpAddrRep.IpAddrData data = ipAddrRep.getData();
        Map<String, String> map = new HashMap<>();
        if (data != null) {
            map.put("country", data.getCountry());
            map.put("province", data.getProvince());
            map.put("city", data.getArea());
        }
        return map;
    }


    public static void main(String[] args) throws Exception {
        String ip = "5.35.199.255";
        String ipArea = getIpAddressLocation(ip);
        // 获取国家信息
        String country = getCountry(ip);
        System.out.println("country:" + country);
        // 获取省份
        String province = getProvince(ip);
        System.out.println("province:" + province);
        // 获取城市
        String city = getCity(ip);
        System.out.println("city:" + city);
        // 获取区域
        Map<String, String> area = getAreaByApi(ip);
        System.out.println("area:" + JSONObject.toJSONString(area));

        for (int i = 0; i < 1000; i++) {
            String randomIpAddressLocation = getRandomIpAddressLocation();
            System.out.println(randomIpAddressLocation);
        }
    }

    public static class IpAddrRep {

        private String tradeNo;

        private Integer chargeStatus;

        private String message;

        private String code;

        private IpAddrData data;

        public String getTradeNo() {
            return tradeNo;
        }

        public void setTradeNo(String tradeNo) {
            this.tradeNo = tradeNo;
        }

        public Integer getChargeStatus() {
            return chargeStatus;
        }

        public void setChargeStatus(Integer chargeStatus) {
            this.chargeStatus = chargeStatus;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public IpAddrData getData() {
            return data;
        }

        public void setData(IpAddrData data) {
            this.data = data;
        }

        public static class IpAddrData {

            private String result;

            private String area;

            private String country;

            private String orderNo;

            private String handleTime;

            private String province;

            public String getResult() {
                return result;
            }

            public void setResult(String result) {
                this.result = result;
            }

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getOrderNo() {
                return orderNo;
            }

            public void setOrderNo(String orderNo) {
                this.orderNo = orderNo;
            }

            public String getHandleTime() {
                return handleTime;
            }

            public void setHandleTime(String handleTime) {
                this.handleTime = handleTime;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getIpAddr() {
                return ipAddr;
            }

            public void setIpAddr(String ipAddr) {
                this.ipAddr = ipAddr;
            }

            private String ipAddr;
        }
    }


}

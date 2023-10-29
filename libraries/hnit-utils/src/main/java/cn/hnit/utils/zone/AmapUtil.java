//package cn.hnit.utils.zone;
//
//import cn.hnit.utils.OkHttpUtil;
//import com.alibaba.fastjson.JSON;
//import com.xhhd.utils.OkHttpUtil;
//import com.xhhd.utils.common.bean.AmapLocationInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.logging.log4j.util.Strings;
//import org.springframework.lang.NonNull;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//
///**
// * @Description 高德地图工具类
// * @author fanghuan
// * @create 2020-12-08 20:59
// **/
//@Slf4j
//public class AmapUtil {
//    private static final String key = "e0c3b832e8a49cb34dba1e01340456fd";
//
//    private static final String locationToCityUrl = "https://restapi.amap.com/v3/geocode/regeo";
//
//    private static final String city2LocationUrl = "https://restapi.amap.com/v3/geocode/geo";
//
//
//    /**
//     * 通过经纬度查询城市
//     *
//     * @param location
//     *            经纬度
//     * @return 城市名
//     */
//    public static String locationToCity(List<Double> location) {
//        if (CollectionUtils.isEmpty(location)) {
//            return Strings.EMPTY;
//        }
//        StringBuilder append = new StringBuilder(locationToCityUrl).append("?key=" + key + "&location=")
//            .append(location.get(0)).append(",").append(location.get(1));
//
//        try {
//            String response = OkHttpUtil.get(append.toString(), null);
//            AmapLocationInfo info = JSON.parseObject(response, AmapLocationInfo.class);
//            String city = info.getRegeocode().getAddressComponent().getCity();
//            if ("1".equals(info.getStatus())) {
//                if ("[]".equals(city)) {
//                    return info.getRegeocode().getAddressComponent().getProvince();
//                } else {
//                    return city;
//                }
//            } else {
//                return Strings.EMPTY;
//            }
//        } catch (Exception exception) {
//            log.info("AmapUtil locationToCity error:", exception);
//        }
//        return Strings.EMPTY;
//    }
//
//    /**
//     * 根据地址查询金纬度
//     * @param address
//     * @return
//     */
//    public static String city2Location(@NonNull String address) {
//        StringBuilder append = new StringBuilder(city2LocationUrl).append("?key=").append(key).append("&address=")
//                .append(address).append("&output=json");
//        try {
//            String repsonse = OkHttpUtil.get(append.toString(), null);
//            AmapLocationInfo info = JSON.parseObject(repsonse, AmapLocationInfo.class);
//            if ("1".equals(info.getStatus())) {
//                return info.getGeocodes().get(0).getLocation();
//            } else {
//                return Strings.EMPTY;
//            }
//        } catch (Exception exception) {
//            log.info("AmapUtil locationToCity error:", exception);
//        }
//        return Strings.EMPTY;
//    }
//}

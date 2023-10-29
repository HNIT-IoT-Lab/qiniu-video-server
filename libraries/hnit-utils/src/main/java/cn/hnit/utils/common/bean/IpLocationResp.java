package cn.hnit.utils.common.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 高德地图 IP查询位置（国内）
 */
@Data
public class IpLocationResp {
    private Integer code;
    private String message;
    private String requestId;
    private String riskLevel;
    private LocationDetail detail;
    /**
     *    "detail":{
     *         "ip_province":"广东",
     *         "ip_city":"深圳",
     *         "hits":[
     *
     *         ],
     *         "model":"M1000",
     *         "ip_country":"中国",
     *         "description":"正常"
     *     },
     */
    @Data
    public static class LocationDetail{
        @JSONField(name = "ip_country")
        private String country;
        @JSONField(name = "ip_province")
        private String province;
        @JSONField(name = "ip_city")
        private String city;
    }
}

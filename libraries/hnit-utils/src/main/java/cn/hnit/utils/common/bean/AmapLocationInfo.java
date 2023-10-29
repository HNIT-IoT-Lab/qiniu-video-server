package cn.hnit.utils.common.bean;

import lombok.Data;

import java.util.List;

/**
 * @Description 高德城市信息
 * @author fanghuan
 * @create 2020-12-08 21:15
 **/
@Data
public class AmapLocationInfo {
    private String status;
    private String info;
    private String infocode;
    private Regeocode regeocode;
    private List<Geocodes> geocodes;
    @Data
    public static class Regeocode {
        private String formatted_address;
        private AddressComponent addressComponent;
    }

    @Data
    public static class AddressComponent {
        private String city;
        private String province;
        private String adcode;
        private String district;
        private String towncode;
    }

    @Data
    public static class Geocodes {
        private String city;
        private String province;
        private String country;
        private String citycode;
        private String location;
        private String level;
    }
}

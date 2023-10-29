//package cn.hnit.starter.intercept.ban.entity;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.time.LocalDateTime;
//
///**
// * 白名单
// */
//@Document("whitelist_config_item")
//@Data
//public class WhitelistConfigItem {
//    @Id
//    private String id;
////    {
////        "type":"类型"，// user、ip、device、、、
////        "eigenvalue":""，// 特征值
////    }
//    private String type;
//    private String eigenvalue;
//    private String modifyUser;
//    private LocalDateTime createTime;
//
//    public enum WhitelistType{
//        DEVICE("设备"),
//        USER("用户"),
//        IP("ip"),;
//        private final String displayName;
//        WhitelistType(String displayName){
//            this.displayName = displayName;
//        }
//
//        public String getDisplayName() {
//            return displayName;
//        }
//    }
//
//}
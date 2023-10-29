//package cn.hnit.starter.intercept.ban.entity;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.time.LocalDateTime;
//
//@Document("ban_config_item")
//@Data
//public class BanConfigItem {
//    @Id
//    private String id;
////    {
////        "type":"类型"，// user、ip、device、、、
////        "eigenvalue":""，// 特征值
////        "resource":""，// 资源名，这里是路径
////        "resourceType":""，// 资源类型，目前是 url
////        "status":""，//状态 lock,free
////        "endtime":"2099-99-10 00:00:00" //解封时间
////    }
//    private String type;
//    private String eigenvalue;
//    private String resource;
//    private String resourceType;
//    private String status;
//    private LocalDateTime endtime;
//    private String modifyUser;
//
//    public enum BanType{
//        DEVICE,
//        USER,
//        IP,
//    }
//
//    public enum ResourceType{
//        URI,
//    }
//
//    public enum BanStatus{
//        LOCK,FREE,
//    }
//
//
//}

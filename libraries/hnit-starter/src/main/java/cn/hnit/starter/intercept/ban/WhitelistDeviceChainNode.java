//package cn.hnit.starter.intercept.ban;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import starter.buge.intercept.ban.entity.WhitelistConfigItem;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
//@Component
//@Slf4j
//public class WhitelistDeviceChainNode implements WhitelistChainNode{
//
//    private static final String type= WhitelistConfigItem.WhitelistType.DEVICE.name();
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    public boolean identifyWhitelist(HttpServletRequest request){
//        // 获取设备号
//        String deviceId = request.getHeader("deviceId");
//        // 前端未提供设备号
//        if (StringUtils.isBlank(deviceId)) {
//            return false;
//        }
//        // 根据特征查询白名单信息信息
//        Criteria condition = Criteria.where("type").is(type)
//                .and("eigenvalue").is(deviceId);
//        Query query = Query.query(condition);
//        List<WhitelistConfigItem> items = mongoTemplate.find(query, WhitelistConfigItem.class);
//        boolean result = !CollectionUtils.isEmpty(items);
//        if(result){
//            log.info("命中白名单设备号规则：deviceId= "+deviceId);
//        }
//        return result;
//    }
//
//}

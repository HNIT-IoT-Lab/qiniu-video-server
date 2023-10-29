//package cn.hnit.starter.intercept.ban;
//
//import com.xhhd.utils.IpUtil;
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
//public class WhitelistIpChainNode implements WhitelistChainNode{
//
//    private static final String type= WhitelistConfigItem.WhitelistType.IP.name();
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    public boolean identifyWhitelist(HttpServletRequest request){
//        // 提取用户ID
//        String ip = IpUtil.getIpAddr(request);
//        // 无法获取IP
//        if (StringUtils.isBlank(ip)) {
//            return false;
//        }
//        // 根据特征查询白名单信息信息
//        Criteria condition = Criteria.where("type").is(type)
//                .and("eigenvalue").is(ip);
//        Query query = Query.query(condition);
//        List<WhitelistConfigItem> items = mongoTemplate.find(query, WhitelistConfigItem.class);
//        boolean result = !CollectionUtils.isEmpty(items);
//        if(result){
//            log.info("命中白名单IP规则：ip= "+ip);
//        }
//        return result;
//    }
//
//}

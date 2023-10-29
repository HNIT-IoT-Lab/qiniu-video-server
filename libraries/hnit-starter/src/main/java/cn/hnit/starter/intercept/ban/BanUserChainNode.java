//package cn.hnit.starter.intercept.ban;
//
//import com.xhhd.buge.combiz.common.util.LogUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import starter.buge.intercept.ban.entity.BanConfigItem;
//import starter.buge.intercept.ban.exeception.BanException;
//
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//@Slf4j
//public class BanUserChainNode implements BanChainNode{
//    @Autowired
//    private MongoTemplate mongoTemplate;
//    @Override
//    public void ban(HttpServletRequest request, Object handler) {
//        String userId = extractUserIdFromRequest(request);
//        // 前端未提供设备号
//        if (StringUtils.isBlank(userId)) {
//            return;
//        }
//        String uri = request.getRequestURI();
//        LocalDateTime now = LocalDateTime.now();
//        // 根据特征，资源地址，过期时间和封禁状态查询相关的封禁信息
//        Criteria condition = Criteria.where("type").is(BanConfigItem.BanType.USER.name())
//                .and("eigenvalue").is(userId)
//                .and("resource").is(uri)
//                .and("resourceType").is(BanConfigItem.ResourceType.URI.name())
//                .and("status").is(BanConfigItem.BanStatus.LOCK.name())
//                .and("endtime").gte(now);
//        Query query = Query.query(condition);
//        List<BanConfigItem> items = mongoTemplate.find(query, BanConfigItem.class);
//        if(CollectionUtils.isEmpty(items)){
//            return;
//        }
//        log.info("请求命中黑名单用户,已拦截。userId="+ userId +"; uri="+uri);
//        throw new BanException(400,"您已被限制访问此功能");
//    }
//
//    // 提取用户id
//    private String extractUserIdFromRequest(HttpServletRequest request){
//        String userNumber = LogUtils.getUserNumber()!=null ? LogUtils.getUserNumber() : request.getHeader("userNumber");
//        log.info("usernumber:"+userNumber);
//        return userNumber;
//    }
//}

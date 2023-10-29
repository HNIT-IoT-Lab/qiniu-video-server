//package cn.hnit.starter.intercept.interceptor;
//
//import com.chinaesport.buge.AppHeartEvent;
//import com.chinaesport.buge.kafka.Topic;
//import com.chinaesport.mq.sdk.MessageService;
//import com.xhhd.utils.context.UserContext;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//import starter.buge.HeartAsyncUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//
///**
// * 此类用于做用户的心跳拦截,隔2分钟发送消息到消息队列
// */
//@Service
//@Slf4j
//public class HeartInterceptor extends HandlerInterceptorAdapter {
//
//
//    /**
//     * (required = false) 表示并不一定需要发送这个心跳,当引用start 包的服务没有配置message的实例化的时候,不会报错
//     */
//    @Autowired(required = false)
//    private MessageService messageService;
//
//
//    private static final String KEY = "app:user:heart";
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (messageService == null) {
//            return true;
//        }
//        try {
//            Long userId = UserContext.getUserId();
//            if (userId == null) {
//                return true;
//            }
//            Object time = redisTemplate.opsForHash().get(KEY, userId + "");
//            /**
//             * 上一次发送事件的时间与现在的差值
//             */
//            long differ = 0L;
//            if (time != null) {
//                Long lastTime = (Long) time;
//                differ = (System.currentTimeMillis() - lastTime) / 1000 / 60;
//            }
//            if (differ >= 2 || time == null) {
//                HeartAsyncUtils.async(new Runnable() {
//                    @Override
//                    public void run() {
//                        long time = System.currentTimeMillis();
//                        messageService.publishMsg(Topic.APP_HEART_EVENT, AppHeartEvent.builder().userId(userId).action(request.getRequestURL().toString()).eventTime(time).build());
//                        redisTemplate.opsForHash().put(KEY, userId + "", time);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            log.error("interceptor heart beat is error ={}", e);
//        }
//        return true;
//    }
//
//}

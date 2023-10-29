//package cn.hnit.starter.intercept.ban;
//
//import cn.hnit.common.enums.AppSourceEnums;
//import cn.hnit.starter.intercept.ban.exeception.BanException;
//import cn.hnit.utils.IpAddress;
//import cn.hnit.utils.IpUtil;
//import cn.hnit.utils.LocalThreadUtil;
//import cn.hnit.utils.context.UserContext;
//import cn.hutool.core.util.NumberUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
//
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//
//@Component
//@Slf4j
//public class BanForeignIpChainNode implements BanChainNode {
//
//    @Autowired
//    private SwitchConfigService switchConfigService;
//    @Autowired
//    private UserContextService userContextService;
//
//    @Override
//    public void ban(HttpServletRequest request, Object handler) {
//        // 获取资源路径
//        String uri = request.getRequestURI();
//        String platformKey = "platform";
//        Integer platform = NumberUtil.isNumber(request.getHeader(platformKey)) ? Integer.parseInt(request.getHeader(platformKey)) : null;
//        String appPayUrl = "/pay/app";
//        if (AppSourceEnums.IOS.getCode().equals(platform) && uri.contains(appPayUrl)) {
//            return;
//        }
//        if (handler instanceof HandlerMethod) {
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//            Method method = handlerMethod.getMethod();
//            PayPath checkParams = method.getAnnotation(PayPath.class);
//            if (checkParams == null) {
//                // 未命中直接放行
//                log.debug("放行资源：" + uri);
//                return;
//            }
//        }
//        long userId = getUid();
//        String regex = ",";
//        if (userId > 0) {
//            String rechargeBlackList = switchConfigService.getSwitchStringByControlId(
//                    ConstantHelper.SwitchConst.RECHARGE_UID_BLACKLIST, "");
//            if (Arrays.asList(rechargeBlackList.split(regex)).contains(String.valueOf(userId))) {
//                throw new BanException(400, "您的充值涉嫌违规，请联系客服");
//            }
//        }
//        // 识别IP位置
//        String ip = IpUtil.getIpAddr(request);
//        if (isChina(ip)) {
//            // 国内用户
//            log.debug("放行国内ip：" + ip);
//            return;
//        }
//        if (userId > 0) {
//            String whiteList = switchConfigService.getSwitchStringByControlId(
//                    ConstantHelper.SwitchConst.FOREIGN_IP_PAY_WHITELIST, "");
//            if (Arrays.asList(whiteList.split(regex)).contains(String.valueOf(userId))) {
//                log.info("放行海外ip白名单用户充值 userId:{}, ip:{}", userId, ip);
//                return;
//            }
//        }
//
//        // 非国内用户
//        log.error("请求命中国外IP,已拦截。ip=" + ip + "; uri=" + uri);
//        throw new BanException(400, "该服务不支持国外用户");
//    }
//
//    private static final String CHINA = "中国";
//
//    private boolean isChina(String ip) {
//        try {
//            String country = IpAddress.getCountry(ip);
//            log.info("BanForeignIpChainNode ip:{} country:{}", ip, country);
//            if (StringUtils.isBlank(country) || CHINA.equals(country)) {
//                return true;
//            } else {
//                log.info("BanForeignIpChainNode ip:{} country:{} forbid to pay!", ip, country);
//                return false;
//            }
//        } catch (Exception e) {
//            log.warn("识别IP失败：" + e.getMessage());
//            return true;
//        }
//    }
//
//    /**
//     * 获取当前用户uid
//     * @return 用户uid
//     */
//    private long getUid(){
//        // 需要登录态校验的接口，可以通过UserContext.getUserId()拿到
//        Long userId = UserContext.getUserId();
//        if(userId != null && userId > 0L) {
//            return userId;
//        }
//        // 没有登录态校验的，比如h5充值，需要从header中拿到userNumber反查
//        String userNumber = LocalThreadUtil.getHeader().getUserNumber();
//        if(StringUtils.isEmpty(userNumber)) {
//            return 0;
//        }
//        ChaUser user = userContextService.getByUserNo(userNumber);
//        if(user != null && user.getUserId() > 0){
//            return user.getUserId();
//        }
//        return 0;
//    }
//}

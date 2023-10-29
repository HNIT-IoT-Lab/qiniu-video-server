//package cn.hnit.starter.intercept.interceptor;
//
//import cn.hnit.common.enums.AppSourceEnums;
//import cn.hnit.common.resultx.ResponseMsg;
//import cn.hnit.starter.annotation.AuthIgnore;
//import cn.hnit.starter.constant.AuthRedisEnum;
//import cn.hnit.utils.DateUtil;
//import cn.hnit.utils.IpUtil;
//import cn.hnit.utils.LocalThreadUtil;
//import cn.hnit.utils.RSAUtils;
//import cn.hnit.utils.common.bean.HeaderParam;
//import cn.hnit.utils.common.bean.TradeRecord;
//import cn.hnit.utils.context.SimpleUserDTO;
//import cn.hnit.utils.context.UserContext;
//import cn.hnit.utils.encrypt.MD5Util;
//import cn.hnit.utils.encrypt.SHAUtil;
//import cn.hnit.utils.logutil.LogUtils;
//import cn.hnit.utils.web.HttpHelper;
//import cn.hutool.core.date.DatePattern;
//import cn.hutool.core.date.SystemClock;
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.security.NoSuchAlgorithmException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
///**
// * 拦截器
// * 验签
// *
// * @author tangyan
// */
//@Slf4j
//@Component
//public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
//
//    public static final DateTimeFormatter YYMMDDHH = DateTimeFormatter.ofPattern("yyMMddHH");
//
//    @Value(value = "${limit.sign.switch:true}")
//    private boolean limitSign;
//
//    @Value(value = "${limit.sign.blackUrls:/voi2/voiRoomLeader/pKBoard}")
//    private String blackUrls;
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//    @Autowired
//    private MongoTemplate mongoTemplate;
//    @Value(value = "${forece.update.pointUrls:oooooooooo}")
//    private String pointUrls;
//    private static final String USERID = "userId";
//    @Value("${spring.profiles.active}")
//    private String profile;
//
//    @DubboReference(check = false, timeout = 200)
//    private IRiskRpcService riskRpcService;
//
//    /**
//     * 简单加密用到的参数
//     */
//    public static final String KEY_PRE = "111111";
//    public static final String KEY_SUF = "222222";
//
//    public static final String R_ID = "r_id";
//    public static final String PROJECT_NAME_EN = "champion";
//    public static final String TIME_KEY = "speed";
//
//    private static final String USER_TOKEN_PREFIX = RedisEnum.USER_TOKEN_PREFIX.getKey();
//
//   private static final DateTimeFormatter DATETIME_MINUTE_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MINUTE_PATTERN);
//
//    @Resource
//    private UserContextService userContextService;
//
//    @Value("${encryption.rsa.secretKey}")
//    private  String secretKey ;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        setHeader(request);
//        if ("local".equals(profile) && StringUtils.isEmpty(LocalThreadUtil.getHeader().getUserToken())) { // 本地环境不校验
//            String uid = request.getHeader("uid");
//            request.setAttribute(USERID, request.getHeader("uid"));
//            if (!StringUtils.isBlank(uid)) {
//                LocalThreadUtil.setUid(Long.parseLong(request.getHeader("uid")));
//            }
//            return true;
//        } else {
//            return (checkInner(handler) || checkAuthAnnotation(request, response, handler)) && checkAppVersion(request, response);
//        }
//    }
//
//    private boolean checkInner(Object handler) {
//        if (handler instanceof HandlerMethod) {
//            Inner annotation = ((HandlerMethod) handler).getMethodAnnotation(Inner.class);
//            return Objects.nonNull(annotation);
//        }
//        return false;
//    }
//
//    private boolean setRsp(HttpServletResponse rsp, Integer code, String msg) throws IOException {
//        rsp.setHeader("Content-Type", "application/json;charset=UTF-8");
//        ResponseMsg<String> responseMsg = new ResponseMsg<>(code, msg);
//        responseMsg.setTraceId(LogUtils.getTraceId());
//        rsp.getWriter().print(JSON.toJSONString(responseMsg, SerializerFeature.WriteMapNullValue));
//        return false;
//    }
//
//    /**
//     * 根据注解判断是否鉴权
//     *
//     * @throws NoSuchAlgorithmException
//     * @throws IOException
//     */
//    private boolean checkAuthAnnotation(HttpServletRequest request, HttpServletResponse response, Object handler) throws NoSuchAlgorithmException, IOException {
//        if (handler instanceof HandlerMethod) {
//            AuthIgnore annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthIgnore.class);
//            log.info("handler 的值为：{}", handler);
//            if (Objects.nonNull(annotation)) {
//                switch (annotation.type()) {
//                    case ALL:
//                        return true;
//                    case TOKEN:
//                        return verifySign(request, response);
//                    case ENCRYPT:
//                        return verifyToken(request, response);
//                    default:
//                        break;
//                }
//            }
//        }
//        log.info("判断是否进入 checkAuthAnnotation 方法中的 if中");
//        return verifyToken(request, response) && verifySign(request, response);
//    }
//
//    /**
//     * 设置请求头到当前线程
//     *
//     * @param request HttpServletRequest
//     */
//    private void setHeader(HttpServletRequest request) {
//        Enumeration<String> headerNames = request.getHeaderNames();
//        JSONObject header = new JSONObject();
//        while (headerNames.hasMoreElements()) {
//            String key = headerNames.nextElement();
//            header.put(key, request.getHeader(key));
//        }
//
//        String versionName = header.getString("versionname");
//        if (StringUtils.isNotBlank(versionName)) {
//            Integer from = header.getInteger(HeaderParam.Fields.platform);
//            if(Objects.nonNull(from)){
//                Ver parse = Ver.parse(versionName);
//                String androidVersion = SysUtil.IS_BUGE ? "4.6.6" : "1.2.13";
//                String iosVersion =  SysUtil.IS_BUGE ? "4.6.9" : "1.2.13";
//                boolean isDecodeVersion = (AppSourceEnums.ANDROID.getCode().equals(from) && parse.greaterThanOrEqual(Ver.parse(androidVersion))) ||
//                        (AppSourceEnums.IOS.getCode().equals(from) && parse.greaterThanOrEqual(Ver.parse(iosVersion)));
//                if (isDecodeVersion) {
//                    decodeDeviceId(header);
//                }
//            }
//        }
//        HeaderParam headerParam = header.toJavaObject(HeaderParam.class);
//        headerParam.setIp(IpUtil.getIpAddr(request));
//        headerParam.setPort(IpUtil.getPort(request));
//        LocalThreadUtil.setLocalObj(headerParam);
//        TradeRecord tradeRecord = new TradeRecord();
//        tradeRecord.setMethod(request.getMethod());
//        tradeRecord.setBody(HttpHelper.getBodyString(request));
//        tradeRecord.setUrl(request.getRequestURL().toString());
//        LocalThreadUtil.setLocalObj(tradeRecord);
//        String userId = request.getHeader("userId");
//        if (StringUtils.isBlank(userId) && StringUtils.isNotBlank(headerParam.getUserToken())) {
//            Object objectUserId = redisTemplate.opsForValue().get(headerParam.getUserToken());
//            userId = Objects.isNull(objectUserId) ? "" : objectUserId.toString();
//        }
//        headerParam.setUserId(userId);
//        log.info("\n请求Id: {} \n请求url: {} \n请求头信息为: {} \n请求类型: {} \n请求体: {} ",
//                LogUtils.getTraceId(), tradeRecord.getUrl(), headerParam, tradeRecord.getMethod(), tradeRecord.getBody());
//        LocalThreadUtil.setLocalObj(tradeRecord);
//        MDC.put(TIME_KEY, SystemClock.now() + "");
//        // 缓存请求头
//        HeaderUtils.push(headerParam);
//    }
//
//    private JSONObject decodeDeviceId(JSONObject header) {
//        String deviceId = header.getString("deviceid");
//        if(StringUtils.isNotBlank(deviceId)){
//            try {
//                deviceId = RSAUtils.decryptByPrivateKey(deviceId, secretKey);
//                header.put("deviceid", deviceId);
//            } catch (Exception e) {
//                log.error("解密deviceId失败", e);
//            }
//
//        }
//        String smDeviceId = header.getString("smdeviceid");
//        if(StringUtils.isNotBlank(smDeviceId)){
//            try {
//                smDeviceId = RSAUtils.decryptByPrivateKey(smDeviceId, secretKey);
//                header.put("smdeviceid", smDeviceId);
//            } catch (Exception e) {
//                log.error("解密smDeviceId失败", e);
//            }
//        }
//        return header;
//    }
//
//    /**
//     * 接口sign验证 除了接口加注解AuthIgnore type ALL，ENCRYPT的都需要校验
//     *
//     * @param request
//     * @throws NoSuchAlgorithmException
//     * @throws IOException
//     */
//    private boolean verifySign(HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException, IOException {
//        if (isEnvNeedCheckSign()) {
//            HeaderParam headerParam = LocalThreadUtil.getLocalObj(HeaderParam.class);
//            Long timestamp = headerParam.getTimestamp();
//            String tsign = SHAUtil.shaEncode(KEY_PRE + timestamp + KEY_SUF);
//            String token = Optional.ofNullable(headerParam.getUserToken()).orElse("");
//            String requestId = headerParam.getRequestId();
//            String source = null;
//            if (StringUtils.isNotBlank(requestId)) {
//                source = token + PROJECT_NAME_EN + tsign + R_ID + requestId;
//            } else {
//                source = token + PROJECT_NAME_EN + tsign;
//            }
//            String sign = MD5Util.md5Encode(source);
//            String urlSign = headerParam.getSign();
//            if (StringUtils.isBlank(urlSign) || !sign.equals(urlSign)) {
//                log.error("sign error sign is {},urlSign is {}", sign, urlSign);
//                return setRsp(response, SysCode.INVAILD_SIGN, "sign错误");
//            }
//            // 设置用户id信息
//            if (StringUtils.isNotBlank(token) && redisTemplate.hasKey(token)) {
//                String userId = String.valueOf(redisTemplate.opsForValue().get(headerParam.getUserToken()));
//                // 设置用id到上下文
//                LocalThreadUtil.setUid(Long.parseLong(userId));
//                // 将用户id设置到MDC中
//                LogUtils.setUserId(userId);
//                LogUtils.setToken(token);
//            }
//            // 接口防刷验证
//            if (limitSign && blackUrls.contains(request.getRequestURI())
//                    && redisTemplate.opsForSet().add(AuthRedisEnum.LIMIT_SIGN.getKey() + LocalDateTime.now().format(YYMMDDHH), sign + request.getRequestURI()).equals(0L)) {
//                log.info("limit is {},blackUrls is {},sign is {}", limitSign, blackUrls, sign + request.getRequestURI());
//                return setRsp(response, SysCode.WARN_NO_SHOW, "网络繁忙，请稍后再试");
//            }
//        }
//        return true;
//    }
//
//    private boolean isEnvNeedCheckSign() {
//        return SysUtil.IS_PROD || SysUtil.IS_PRODGRAY || SysUtil.IS_UAT;
//    }
//
//    /**
//     * 接口token验证除了接口加注解AuthIgnore type ALL，TOKEN的都需要校验
//     * R
//     *
//     * @throws IOException
//     */
//    private boolean verifyToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        HeaderParam headerParam = LocalThreadUtil.getLocalObj(HeaderParam.class);
//        String token = headerParam.getUserToken();
//        if (StringUtils.isEmpty(token) || Boolean.FALSE.equals(redisTemplate.hasKey(token))) {
//            log.warn("[verifyToken] verify token is error ,request is {},token is {}", request != null ? request.getParameterMap() : "no request param", token);
//            return setRsp(response, SysCode.INVAILD_USER_TOKRN, "您的账号登录凭证已过期或已注销，请重新登录");
//        }
//        String userId = String.valueOf(redisTemplate.opsForValue().get(headerParam.getUserToken()));
//        // 将用户id设置到MDC中
//        LogUtils.setUserId(userId);
//        LogUtils.setToken(token);
//        ChaUser user = userContextService.getUser(Long.parseLong(userId));
//        boolean isBan = true;
//        request.setAttribute(USERID, userId);
//        LocalThreadUtil.setUid(Long.parseLong(userId));
//        if (userContextService.userNotNull(user)) {
//
//            LocalThreadUtil.setLocalObj(buildSimpleUser(user));
//            // 用户被封禁逻辑
//            RiskDto riskDto = new RiskDto();
//            riskDto.setUserNumber(user.getUserNumber());
//            riskDto.setDeviceId(headerParam.getDeviceId() == null ? "" : headerParam.getDeviceId());
//            riskDto.setIp(headerParam.getIp() == null ? "" : headerParam.getIp());
//            riskDto.setMessage(getMessage(request));
//
//            try {
//                isBan = riskRpcService.isBooleanBan(riskDto);
//                if (!isBan) {
//                    // 查询封禁时长、封禁原因
//                    List<RiskInfoDto> riskList = riskRpcService.getBanUserInfo(riskDto);
//                    if (!CollectionUtils.isEmpty(riskList)) {
//                        StringBuilder sb = new StringBuilder();
//                        for (RiskInfoDto riskInfoDto : riskList) {
//                            String deviceId = riskInfoDto.getDeviceId();
//                            String ip = riskInfoDto.getIp();
//                            String userNumber = riskDto.getUserNumber();
//                            if (StrUtil.isNotBlank(deviceId)) {
//                                sb.append("设备号").append(",");
//                            } else if (StrUtil.isNotBlank(ip)){
//                                sb.append("IP地址").append(",");
//                            } else if (StrUtil.isNotBlank(userNumber)) {
//                                sb.append("账号").append(",");
//                            }
//                        }
//
//                        String head = sb.toString().substring(0, sb.length() - 1);
//                        String reason = riskList.get(0).getReason();
//                        String endDate = riskList.get(0).getSeconds() == -1? "永久封禁" : DateUtil.getAfterSeconds(new Date(), riskList.get(0).getSeconds());
//                        log.info("调用封禁推送，封禁用户为：{}", user.getUserNumber());
//                        isBan = getUserBlackListMessage(response, head, reason, endDate);
//                    }
//                } else {
//                    changeUserLoginInfo(user, headerParam.getDeviceId(), headerParam.getIp());
//                }
//                deleteRedisKey(user.getUserNumber());
//                log.info("清除redis中存在的封禁信息: {}", user.getUserNumber());
//                // riskRpcService.saveLoginInfo(riskDto);
//            } catch (Exception e) {
//
//                log.warn("请查看risk服务是否存在问题,先给通过！", e);
//                return true;
//            }
//            LogUtils.setUserNumber(user.getUserNumber());
//        }
//        log.info("该 token 校验最终返回值为：{}", isBan);
//        return isBan;
//    }
//
//    public String getMessage(HttpServletRequest request) {
//        String requestURI = request.getRequestURI();
//        Enumeration<String> headerNames = request.getHeaderNames();
//        Map<String, String> paramsMap = new HashMap<>();
//        while (headerNames.hasMoreElements()) {
//            String key = headerNames.nextElement();
//            String value = request.getHeader(key);
//            paramsMap.put(key, value);
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("url", requestURI);
//        map.put("params", paramsMap);
//        map.put("time", System.currentTimeMillis());
//        String message = JSONObject.toJSONString(map);
//        log.warn("接口访问：{}", message);
//        return message;
//    }
//
//    public void deleteRedisKey(String target) {
//        if (StringUtils.isNotBlank(target)) {
//            if (hasRedisKey(target)) {
//                redisTemplate.delete(addRedisPrefix(target));
//            }
//        }
//    }
//
//    public boolean hasRedisKey(String target) {
//        return redisTemplate.hasKey(addRedisPrefix(target));
//    }
//
//    public String addRedisPrefix(String string) {
//        return getBlackKey(string);
//    }
//
//    private SimpleUserDTO buildSimpleUser(ChaUser user) {
//        SimpleUserDTO simpleUserDTO = new SimpleUserDTO();
//        if (user == null) {
//            return simpleUserDTO;
//        }
//        simpleUserDTO.setUserName(user.getUserName());
//        simpleUserDTO.setUserNumber(user.getUserNumber());
//        simpleUserDTO.setUserIcon(user.getAvatarUrl());
//        simpleUserDTO.setGender(user.getGender());
//        String birthday = user.getBirthday();
//        simpleUserDTO.setAge(DateUtil.birthToAge(birthday));
//        simpleUserDTO.setUserId(user.getUserId());
//        simpleUserDTO.setPhone(user.getPhone());
//        return simpleUserDTO;
//    }
//
//    private String getBlackKey(String key) {
//        return AuthRedisEnum.USER_BAN_LIST.getKey() + key;
//    }
//
//
//    private boolean isRequestFromApp() {
//        return UserContext.isIos() || UserContext.isPc() || UserContext.isAndroid();
//    }
//
//    private boolean isRequestFromAppAndUserIdNotNull() {
//        return isRequestFromApp() && UserContext.getUserId() != null;
//    }
//
//
//    private boolean isExistBlack(String deviceId, String ip) {
//        return redisTemplate.hasKey(getBlackKey(deviceId))
//                || redisTemplate.hasKey(getBlackKey(ip));
//    }
//
//
//    private void changeUserLoginInfo(ChaUser user, String deviceId, String ip) {
//        Long userId = user.getUserId();
//        boolean isNeedUpdate = false;
//        if (StringUtils.isNotBlank(deviceId) && !deviceId.equals(user.getLastDeviceId())) {
//            isNeedUpdate = true;
//        }
//        if (StringUtils.isNotBlank(ip) && !ip.equals(user.getLastIp())) {
//            isNeedUpdate = true;
//        }
//        if (isNeedUpdate) {
//            log.info("[changeUserLoginInfo] userId is {},deviceId is {},ip is {}", userId, deviceId, ip);
//            mongoTemplate.updateFirst(Query.query(Criteria.where(ChaUser.Fields.userId).is(userId)), Update.update(ChaUser.Fields.lastIp, ip).set(ChaUser.Fields.lastDeviceId, deviceId), ChaUser.class);
//            userContextService.delUser(userId);
//        }
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        log.info("\n响应时间: {}ms - url: {} ",
//                SystemClock.now() - Long.parseLong(MDC.get(TIME_KEY)),
//                request.getRequestURI());
//        LocalThreadUtil.remove();
//        // 清空缓存请求头
//        HeaderUtils.clear();
//    }
//
//    /**
//     * 根据当前请求检查app 是否需要强制更新
//     *
//     * @throws IOException
//     */
//    private boolean checkAppVersion(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        if (StringUtils.isNotBlank(request.getHeader("version")) && ("0".equals(request.getHeader("platform")) || "1".equals(request.getHeader("platform")))) {
//            String[] urls = pointUrls.split(",");
//            String uri = request.getRequestURI();
//            for (String url : urls) {
//                if (uri.contains(url)) {
//                    return setRsp(response, SysCode.WARN, "版本过低，请退出app重新更新或者应用市场下载");
//                }
//            }
//        }
//        return true;
//    }
//
//    private boolean getUserBlackListMessage(HttpServletResponse response, String head, String reason, String endDate) throws IOException {
//        JSONObject message = new JSONObject();
//        message.put("head", "您的[" + head + "]涉嫌违规已被封禁");
//        if (!StringUtils.isBlank(reason)) {
//            message.put("reason", reason);
//        }
//        message.put("openTime", endDate);
//        log.info("[getUserBlackListMessage] reason is {}", message);
//        return getBlackListMessage(response, message);
//    }
//
//
//    private boolean getBlackListMessage(HttpServletResponse response, JSONObject message) throws IOException {
//        message.put("end", "公众号" + SysUtil.APP_NAME + "app-在线客服。");
//        log.info("getBlackListMessage is {}", message);
//        return setRsp(response, SysCode.USER_BAN_CODE, message.toString());
//    }
//
//
//
//}

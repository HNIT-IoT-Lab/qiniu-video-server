//package cn.hnit.common.dubbo.filter;
//
//import com.xhhd.buge.combiz.common.util.LogUtils;
//import com.xhhd.utils.context.UserContext;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.common.constants.CommonConstants;
//import org.apache.dubbo.common.extension.Activate;
//import org.apache.dubbo.rpc.*;
//
///**
// * @author xsm
// * @date 2021/12/7
// * @Description
// */
//@Slf4j
//@Activate(group = {CommonConstants.CONSUMER})
//public class DubboConsumerTraceIdFilter implements Filter {
//
//    /**
//     * 设置日志id
//     * @param invoker 调用者
//     * @param invocation 调用方法
//     */
//    @Override
//    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//        boolean newTraceId = setTraceId(invocation);
//        long time = 0;
//        if(log.isDebugEnabled()){
//            time = System.currentTimeMillis();
//            log.debug("dubbo consumer url Path: {},{}", invocation.getInvoker().getUrl().getPath(), invocation.getMethodName());
//        }
//        try {
//            return invoker.invoke(invocation);
//        }
//        finally {
//            // 如果是新生成的traceId, 说明并不是从SpringMVC处生成的, 那么需要将traceId清楚掉
//            if (newTraceId) {
//                LogUtils.clear();
//            }
//            if(log.isDebugEnabled()){
//                long sub = System.currentTimeMillis() - time;
//                log.debug("dubbo consumer request end. time:{},{},{}", sub, invocation.getInvoker().getUrl().getPath(), invocation.getMethodName());
//            }
//        }
//    }
//
//    /**
//     * 设置TraceId
//     * @return 返回true表示traceId是此请求发出的，需要自己清理
//     */
//    private boolean setTraceId(Invocation invocation) {
//        boolean newTraceId = false;
//        // 如果MDC获取失败，则生成一个traceId
//        String traceId = LogUtils.getTraceId();
//        if(StringUtils.isBlank(traceId)){
//            LogUtils.setTraceId();
//            newTraceId = true;
//        }
//        // dubbo 透传
//        invocation.getAttachments().put(LogUtils.TRACE_ID, traceId);
//
//        Long userId = UserContext.getUserId();
//        if (userId!=null){
//            // dubbo 透传
//            invocation.getAttachments().put(LogUtils.USER_ID, String.valueOf(userId));
//        }
//        return newTraceId;
//    }
//
//}

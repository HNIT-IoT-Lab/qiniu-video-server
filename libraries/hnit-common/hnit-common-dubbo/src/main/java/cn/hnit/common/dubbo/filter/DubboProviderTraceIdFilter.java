//package cn.hnit.common.dubbo.filter;
//
//import com.xhhd.buge.combiz.common.util.LogUtils;
//import com.xhhd.utils.LocalThreadUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.aopalliance.intercept.Invocation;
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
//@Activate(group = {CommonConstants.PROVIDER})
//public class DubboProviderTraceIdFilter implements Filter {
//
//    @Override
//    public Result invoke(Invoker<?> invoker, org.aopalliance.intercept.Invocation invocation) throws RpcException {
//        setTraceId(invocation);
//        long time = 0;
//        if(log.isDebugEnabled()){
//            time = System.currentTimeMillis();
//            log.debug("dubbo provider url Path: {},{}", invocation.getInvoker().getUrl().getPath(), invocation.getMethodName());
//        }
//        try {
//            return invoker.invoke(invocation);
//        } finally {
//            LogUtils.clear();
//            LocalThreadUtil.remove();
//            if(log.isDebugEnabled()){
//                long sub = System.currentTimeMillis() - time;
//                log.debug("dubbo provider request end. time:{},{},{}", sub, invocation.getInvoker().getUrl().getPath(), invocation.getMethodName());
//            }
//        }
//    }
//
//    /**
//     * 设置traceId
//     */
//    private void setTraceId(Invocation invocation) {
//        String traceId = invocation.getAttachment(LogUtils.TRACE_ID);
//        if (StringUtils.isBlank(traceId)) {
//            LogUtils.setTraceId();
//        } else {
//            LogUtils.setTraceId(traceId);
//        }
//
//        String userId = invocation.getAttachment(LogUtils.USER_ID);
//        if (userId!=null){
//            try {
//                LocalThreadUtil.setUid(Long.valueOf(userId));
//            } catch (NumberFormatException e) {
//               log.error(e.getMessage(),e);
//            }
//        }
//
//    }
//}

package cn.hnit.common.dubbo.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;

@Slf4j
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class RpcExceptionFilter implements Filter, Filter.Listener {

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
            try {
                Throwable exception = appResponse.getException();
                // directly throw if it's checked exception
                if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                    return;
                }
                // directly throw if it's JDK exception
                String className = exception.getClass().getName();
                if (className.startsWith("java.") || className.startsWith("javax.")) {
                    return;
                }
                // directly throw if it's dubbo exception
                if (exception instanceof RpcException) {
                    return;
                }
                // directly throw if it's buge exception
                if (exception instanceof RuntimeException) {
                    return;
                }
                log.error("dubbo service call error: ", exception);
                appResponse.setException(new RuntimeException("系统异常"));
            } catch (Exception e) {
                log.warn("Fail to ExceptionFilter when called by {}. service: {}, method: {}, exception: {}: {}", RpcContext.getContext().getRemoteHost(), invoker.getInterface().getName(), e.getClass().getName(), e.getMessage());
            }
        }
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        log.error("Got unchecked and undeclared exception which called by {}. service: {} , method: " + invocation.getMethodName() + ", exception: {}: {}", RpcContext.getContext().getRemoteHost(), invoker.getInterface().getName(), t.getClass().getName(), t.getMessage());
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {
        return invoker.invoke(invocation);
    }

}

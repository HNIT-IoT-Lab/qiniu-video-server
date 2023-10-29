package cn.hnit.starter.intercept.interceptor;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.common.resultx.ResponseMsg;
import cn.hnit.starter.annotation.ResponseIgnore;
import cn.hnit.utils.LocalThreadUtil;
import cn.hnit.utils.logutil.LogUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 增强所有的{@link ResponseMsg}
 *
 * @author 梁峰源
 * @since 2022-08-22 21:54
 **/
@Slf4j
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // 贴了注解的不用包装
        return !methodParameter.hasMethodAnnotation(ResponseIgnore.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        // 返回设置traceId
        if (body instanceof ResponseMsg) {
            return ((ResponseMsg<?>) body).setTraceId(LogUtils.getTraceId());
        }
        ResponseMsg<Object> objectResponseMsg = new ResponseMsg<>(body).setTraceId(LogUtils.getTraceId());
        // String类型不能直接包装
        if (methodParameter.getGenericParameterType().equals(String.class)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 将数据包装在ResultVo里后转换为json串进行返回
                return objectMapper.writeValueAsString(objectResponseMsg);
            } catch (JsonProcessingException e) {
                throw new AppException(500, e.getMessage());
            }
        }
        // 否则直接包装成ResultVo返回
        return objectResponseMsg;
    }
}

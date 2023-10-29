package cn.hnit.starter.intercept.crypt;

import cn.hnit.common.resultx.ResponseMsg;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 请求响应处理类<br>
 * <p>
 * 对加了@Encrypt的方法的数据进行加密操作
 *
 * @author 熊诗言
 */
@ControllerAdvice
@Slf4j
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {


    private final String key = "B7285tdXLibeTEKm";


    private final String pia = "s8qINySY8nw7wpcD";


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        boolean encrypt = NeedCrypto.needEncrypt(returnType);

        if (!encrypt) {
            return body;
        }

        if (!(body instanceof ResponseMsg)) {
            return body;
        }

        //只针对ResponseMsg的data进行加密
        ResponseMsg responseMsg = (ResponseMsg) body;
        Object data = responseMsg.getData();
        if (null == data) {
            return body;
        }

        String xx = null;
        Class<?> dataClass = data.getClass();
        if (dataClass.isPrimitive() || (data instanceof String)) {
            xx = String.valueOf(data);
        } else {
            log.info("序列化之前的数据={}", data);
            xx = JSONUtil.toJsonPrettyStr(data);
            log.info("序列化之后的数据={}", xx);
        }
        if (!StringUtils.isBlank(xx)) {
            responseMsg.setData(AESUtils.encrypt(xx, key, pia));
        }
        return responseMsg;
    }

}
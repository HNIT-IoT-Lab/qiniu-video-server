package cn.hnit.starter.intercept.crypt;

import cn.hnit.common.exception.base.AppException;
import cn.hutool.core.io.IoUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiongshiyan
 */
public class DecryptHttpInputMessage implements HttpInputMessage {
    private final HttpInputMessage inputMessage;
    private final String charset = "utf-8";
    private final String key;

    private final String pia;

    public DecryptHttpInputMessage(HttpInputMessage inputMessage, String key, String pia) {
        this.inputMessage = inputMessage;
        this.key = key;
        this.pia = pia;
    }

    @Override
    public InputStream getBody() throws IOException {
        String content = IoUtil.read(inputMessage.getBody(), charset);
        String decryptBody = AESUtils.decrypt(content, key, pia);
        if (StringUtils.EMPTY.equals(decryptBody)) {
            throw new AppException("系统更新，请重新刷新");
        }
        return new ByteArrayInputStream(decryptBody.getBytes(charset));
    }

    @Override
    public HttpHeaders getHeaders() {
        return inputMessage.getHeaders();
    }
}
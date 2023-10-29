package cn.hnit.utils;

import cn.hnit.utils.starter.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 *
 * MaskUtils
 *
 * @author huangjiangping
 */
@Slf4j
public class MaskUtils {

    public static String decryptIdCardNumber(String idCardNumber,String key) {
        if (StringUtils.isEmpty(idCardNumber)) {
            return idCardNumber;
        }
        int idCardNumLength = 18;
        if (idCardNumber.length() > idCardNumLength) {
            try {
                idCardNumber = RSAUtils.decryptByPrivateKey(idCardNumber, key);
            } catch (Exception e) {
                log.error("解密客户端身份证号码失败", e);
            }
        }
        return idCardNumber;
    }

    public static String idCardMask(String idCardNum) {
        String res = "";
        if (!StringUtils.isEmpty(idCardNum)) {
            StringBuilder stringBuilder = new StringBuilder(idCardNum);
            res = stringBuilder.replace(6, 14, "********").toString();
        }
        return res;
    }

    private static String secretKey = null;

    public static String decryptDeviceId(String deviceId){
        if (StringUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        if (secretKey == null) {
            secretKey = ApplicationContextHolder.getProperty("encryption.rsa.secretKey");
            AssertUtil.hasText(secretKey, "未配置encryption.rsa.secretKey");
        }
        try {
            return RSAUtils.decryptByPrivateKey(deviceId, secretKey);
        } catch (Exception e) {
            // do nothing
        }
        return deviceId;
    }
}

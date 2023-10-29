package cn.hnit.starter.intercept.crypt;


import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

@Slf4j
public class AESUtils {

    private static final String CHARSET_NAME = "UTF-8";
    private static final String AES_NAME = "AES";
    public static final String ALGORITHM = "AES/CBC/PKCS7Padding";

//    static {
//        Security.addProvider(new BouncyCastleProvider());
//    }

    /**
     * 加密
     *
     * @param content
     * @param key
     * @return
     */
    public static String encrypt(String content, String key, String pia) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(pia.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            result = cipher.doFinal(content.getBytes(CHARSET_NAME));
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }
        return Base64.encodeBase64String(result);
    }

    /**
     * 解密
     *
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key, String pia) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(pia.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return new String(cipher.doFinal(Base64.decodeBase64(content.getBytes())), CHARSET_NAME);
        } catch (Exception ex) {
            log.info("解密出错={},={}", content, ex);
        }
        return StringUtils.EMPTY;
    }

    public static void main(String[] args) {
        String key = "B7285tdXLibeTEKm";
        String content = "lVvxfiVM/CYUDRO4CHzkt5G6J3CcFEZbrEhFS6TeYjIfIwQtOB+7s/EEo3UWLLA46t4nT+DAhPCTPnBregratcdlpBCPq6KNaO+AH6z7ePPNHQheu71V0aoMyJJHWvCcWxCqIfZ2VMGeGH6Oc5Fgbubb2Pch9W4qIVRF4lbszKbCSYvl09E4hUeLSUU3dSnS22B7MyivmOOmBu3vimq+UdB0YOmPUJmjd7YooIhQcGiupVRnWhL8dS97MOfLnbOOXqPg4UHvKQl1JuY3wI7cNpOcbIVirNpd/Toxk946Mw5LTxQ59ojVEU8NXjmi8uJGR49lyDnveEUyi66f7SD+0JaFlS+ewtpKJZCevMuDEgDA9EwJ8i7x9gYfF/dEQ/eKGhskpzT3AJuNjNATm2IUdlNQlFlS61Aiiqu7Vcmscgf/IcuoH5vDWBm7o7z99HjTO1Hxe96zZiHcd/ZamdQHQ0w0cM1EX3BbJwzkIugAjyag1v/moJs3OISf2A6dWzxZAisRai5DcHsq8681bUIVRw==";
        String pia = "s8qINySY8nw7wpcD";
        log.info("result2:{}", AESUtils.decrypt(content, key, pia));
        log.info("result2:{}", AESUtils.encrypt(AESUtils.decrypt(content, key, pia), key, pia));


    }


}



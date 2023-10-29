//package cn.hnit.utils;
//
//import com.tls.base64_url.base64_url;
//import lombok.extern.slf4j.Slf4j;
//import org.bouncycastle.util.encoders.Base64;
//import org.json.JSONObject;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.Arrays;
//import java.util.zip.Deflater;
//
//@Slf4j
//public class TLSSigAPIv2 {
//    private final long sdkappid;
//    private final String key;
//
//    public TLSSigAPIv2(long sdkappid, String key) {
//        this.sdkappid = sdkappid;
//        this.key = key;
//    }
//
//    public static void main(String[] args) throws Exception {
//    	TLSSigAPIv2 tl  = new TLSSigAPIv2(1400454648,"5c7eea871a776aa92d2512df87b1cd9f4ee0ad7c0b50af23eb2f2d32228f4152");
//		log.info(tl.genSig("942442416",86400));
//	}
//
//    private String hmacsha256(String identifier, long currTime, long expire, String base64Userbuf) {
//        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
//                + "TLS.sdkappid:" + sdkappid + "\n"
//                + "TLS.time:" + currTime + "\n"
//                + "TLS.expire:" + expire + "\n";
//        if (null != base64Userbuf) {
//            contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
//        }
//        try {
//            byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
//            Mac hmac = Mac.getInstance("HmacSHA256");
//            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
//            hmac.init(keySpec);
//            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
//            return Base64.toBase64String(byteSig).replaceAll("\\s*", "");
//        } catch (NoSuchAlgorithmException|InvalidKeyException e) {
//            return "";
//        }
//    }
//
//    private String genSig(String identifier, long expire, byte[] userbuf) {
//
//        long currTime = System.currentTimeMillis()/1000;
//
//        JSONObject sigDoc = new JSONObject();
//        sigDoc.put("TLS.ver", "2.0");
//        sigDoc.put("TLS.identifier", identifier);
//        sigDoc.put("TLS.sdkappid", sdkappid);
//        sigDoc.put("TLS.expire", expire);
//        sigDoc.put("TLS.time", currTime);
//
//        String base64UserBuf = null;
//        if (null != userbuf) {
//            base64UserBuf = Base64.toBase64String(userbuf);
//            sigDoc.put("TLS.userbuf", base64UserBuf);
//        }
//        String sig = hmacsha256(identifier, currTime, expire, base64UserBuf);
//        if (sig.length() == 0) {
//            return "";
//        }
//        sigDoc.put("TLS.sig", sig);
//        Deflater compressor = new Deflater();
//        compressor.setInput(sigDoc.toString().getBytes(StandardCharsets.UTF_8));
//        compressor.finish();
//        byte [] compressedBytes = new byte[2048];
//        int compressedBytesLength = compressor.deflate(compressedBytes);
//        compressor.end();
//        return (new String(base64_url.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
//                0, compressedBytesLength)))).replaceAll("\\s*", "");
//    }
//
//    public String genSig(String identifier, long expire) {
//        return genSig(identifier, expire, null);
//    }
//
//    public String genSigWithUserBuf(String identifier, long expire, byte[] userbuf) {
//        return genSig(identifier, expire, userbuf);
//    }
//}
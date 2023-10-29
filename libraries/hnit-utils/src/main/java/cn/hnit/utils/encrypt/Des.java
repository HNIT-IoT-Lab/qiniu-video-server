package cn.hnit.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Slf4j
public class Des {

	private static final String defaultKey = "oxbix123";

//	public static void main(String[] args) throws Exception {
//
//		String str = "测试内容";
//		String key = "aaaabaaa";
//		byte[] result = Des.desCrypto(str.getBytes(), key);
//		System.out.println("加密后的内容为：" + result);
//		try {
//			byte[] decryResult = Des.decrypt(result, key);
//			System.out.println("加密后的内容为：" + new String(decryResult));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//
//	}


	public static void main(String[] args) throws Exception {
		String content = "{{\"broker_id\":\"27532644\",\"card_no\":\"17889972556\"," +
				"\"check_name\":\"Check\",\"dealer_id\":\"20375233\",\"id_card\":\"620502199509116337\"," +
				"\"notes\":\"10799098\",\"notify_url\":\"http://134.175.81.207:8088/cms/buge/drawMoney/callback\",\"order_id\":" +
				"\"40115577178568091799\",\"pay\":\"0.01\",\"pay_remark\":\"10799098\",\"real_name\":\"陈魁\"}";
		byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
		byte[] key = "2EqJfmcL4Tz3n46418JOBuIf".getBytes(StandardCharsets.UTF_8);

		byte[] enc = Des.desEncrypt(bytes, key);
		byte[] enc64 = Base64.encodeBase64(enc);
		System.out.println("encrypt: " + new String(enc64));
		byte[] dec64 = Base64.decodeBase64(enc64);
		byte[] dec = desDecrypt(dec64, key);
		System.out.println("decrypt: " + new String(dec));
	}

	/**
	 * 加密
	 */
	public static byte[] desEncrypt(byte[] content, byte[] key) throws Exception {
		byte[] icv = new byte[8];
		System.arraycopy(key, 0, icv, 0, 8);
		return tripleDesEncrypt(content, key, icv);
	}

	/**
	 * 解密
	 */
	public static byte[] desDecrypt(byte[] content, byte[] key) throws Exception {
		byte[] icv = new byte[8];
		System.arraycopy(key, 0, icv, 0, 8);
		return tripleDesDecrypt(content, key, icv);
	}


	protected static byte[] tripleDesEncrypt(byte[] content, byte[] key, byte[] icv) throws
			Exception {
		final SecretKey secretKey = new SecretKeySpec(key, "DESede");
		final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		final IvParameterSpec iv = new IvParameterSpec(icv);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		return cipher.doFinal(content);
	}

	protected static byte[] tripleDesDecrypt(byte[] content, byte[] key, byte[] icv) throws
			Exception {
		final SecretKey secretKey = new SecretKeySpec(key, "DESede");
		final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		final IvParameterSpec iv = new IvParameterSpec(icv);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		return cipher.doFinal(content);
	}


	public static byte[] desCrypto(byte[] datasource) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(defaultKey.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			log.error("error: ", e);
		}
		return null;
	}

	public static byte[] desCrypto(byte[] datasource, String key) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			log.error("error: ", e);
		}
		return null;
	}

	public static byte[] decrypt(byte[] src) throws Exception {
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(defaultKey.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		return cipher.doFinal(src);
	}

	public static byte[] decrypt(byte[] src, String key) throws Exception {
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(key.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		return cipher.doFinal(src);
	}

}

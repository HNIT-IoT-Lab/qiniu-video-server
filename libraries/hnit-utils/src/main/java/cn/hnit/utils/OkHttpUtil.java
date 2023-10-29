package cn.hnit.utils;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.utils.encrypt.MD5Util;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * http调用公用类
 * @author Admin
 *
 */
@Slf4j
public class OkHttpUtil {
	
	private OkHttpUtil() {
	    throw new IllegalStateException("Utility class");
	  }

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(10, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
            .build();
    // 平台相关
    public static final String PARAM_PLATFORMID = "platformId";
    public static final String PARAM_PLATFORMKEY = "platformkey";
    public static final String PARAM_PLATFORMCODD = "platformCode";
    public static final String PARAM_PLATFORM = "platform";
    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";


    /**
     * httpClient 方式实现post json
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String sendHttpPostWithHeads(String url, String body,Map<String,String> headers) {
        HttpPost httpPost = new HttpPost(url);
        packRequest(body, headers, httpPost);
        return executeByHttpClient(httpPost,url);
    }

    /**
     * httpClient 方式实现DELETE json
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String sendHttpDeleteWithHeads(String url, String body, Map<String,String> headers) {
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        packRequest(body, headers, httpDelete);
        return executeByHttpClient(httpDelete,url);
    }

    /**
     * httpClient 方式实现DELETE json
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String sendHttpPutWithHeads(String url, String body, Map<String,String> headers) {
        HttpPut httpPut = new HttpPut(url);
        packRequest(body, headers, httpPut);
        return executeByHttpClient(httpPut,url);
    }


    private static void packRequest(String body, Map<String, String> headers, HttpEntityEnclosingRequestBase request) {
        request.addHeader("Content-Type", CONTENT_TYPE_JSON);
        addHeader(request, headers);
        StringEntity s = new StringEntity(body, StandardCharsets.UTF_8);  //对参数进行编码，防止中文乱码
        s.setContentEncoding(StandardCharsets.UTF_8.toString());
        s.setContentType("application/json");
        request.setEntity(s);
    }

    /**
     * httpClient 方式实现post json
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String sendHttpPost(String url, String body) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", CONTENT_TYPE_JSON);
        StringEntity s = new StringEntity(body, StandardCharsets.UTF_8);  //对参数进行编码，防止中文乱码
        s.setContentEncoding(StandardCharsets.UTF_8.toString());
        s.setContentType("application/json");
        httpPost.setEntity(s);
        return executeByHttpClient(httpPost,url);
    }

    /**
     * 上传文件
     * @param url 连接地址
     * @param file 文件
     * @param maps 参数
     */
    public static String sendFromDataPostRequest(String url,File file,Map<String, String> maps,String typeName,String fileName){
        MultipartBody.Builder builder=  new MultipartBody.Builder().setType(MultipartBody.FORM);

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
        builder.addFormDataPart(typeName,file.getName(),fileBody);
		if (maps != null && maps.size() > 0) {
			for (Entry<String, String> entry : maps.entrySet()) {
				builder.addFormDataPart(entry.getKey(), entry.getValue());
			}
		}
        RequestBody body=builder.build();
        Request request =  new Request.Builder().url(url).post(body).build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            String result = response.body().string();
            log.info("调用sendFromDataPostRequest返回:"+result);
            return result;
        } catch (Exception e) {
            log.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
    }

    /**
     * get
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static  String get(String url, Map<String, String> queries) {
        Request request = new Request.Builder()
                .url(getUrlString(url,queries, false))
                .build();
        return executeByOkHttp(request, url);
    }
    
    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && !params.keySet().isEmpty()) {
            for (Entry<String,String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        return executeByOkHttp(request, url);
    }

    /**
     * get
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String getForHeader(String url, Map<String, String> queries) {
        Request request = new Request.Builder()
                .addHeader("key", "value")
                .url(getUrlString(url,queries, false))
                .build();
        return executeByOkHttp(request, url);
    }

    /**
     * Post请求发送JSON数据....{"name":"zhangsan","pwd":"123456"}
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public static String postJsonParams(String url, String jsonParams) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonParams);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type",CONTENT_TYPE_JSON)
                .build();
        return executeByOkHttp(request, url);
    }

    /**
     * Post请求发送xml数据....
     * 参数一：请求Url
     * 参数二：请求的xmlString
     * 参数三：请求回调
     */
    public static String postXmlParams(String url, String xml) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), xml);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return executeByOkHttp(request, url);
    }


    /**
     * 设置 header 发送 get 请求
     * @param url
     * @param queries
     * @return
     * @throws Exception
     */
    public static  String getSent(String url, Map<String, String> queries,Map<String,String> headers) {
        return getSent(url, queries, headers, false);
    }

    public static  String getSent(String url, Map<String, String> queries,Map<String,String> headers, boolean needUrlEncode) {
        Request.Builder builder = new Request.Builder().url(getUrlString(url,queries, needUrlEncode));
        if (headers != null && headers.size() > 0) {
            for (Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        return executeByOkHttp(request, url);
    }

    /**
     * 设置 header 发送 post 请求
     * @param url
     * @param queries
     * @return
     * @throws Exception
     */
    public static  String postHead(String url, Map<String, String> queries,Map<String,String> headers) {
        HttpPost httpPost = new HttpPost(url);
        addHeader(httpPost, headers);
        List<NameValuePair> formparams = new ArrayList<>();
        if (queries != null) {
            Set<String> keys = queries.keySet();
            for (String key : keys) {
                formparams.add(new BasicNameValuePair(key,queries.get(key)!=null?queries.get(key):null ));
            }
        }
        UrlEncodedFormEntity entity = null;
        try {
			entity = new UrlEncodedFormEntity(formparams, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			log.error("UrlEncodedFormEntity error",e);
		}
        httpPost.setEntity(entity);
        return executeByHttpClient(httpPost,url);
    }

    public static <T> T postByJson(String url, String json, Class<T> clazz) throws Exception{
    	RequestBody body = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        String rsp=executeByOkHttp(request, url);
        if(!"".equals(rsp)) {
        	return JSON.toJavaObject(JSON.parseObject(rsp), clazz);
        }else {
        	throw new AppException("调用第三方服务异常");
        }
    }

    /**
     * httpClient 方式实现post json
     * @param url
     * @return
     * @throws Exception
     */
    public static String postNoParam(String url) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", CONTENT_TYPE_JSON);
        return executeByHttpClient(httpPost, url);
    }

	private static String executeByHttpClient(HttpRequestBase httpPost, String url) {
		int index = url.indexOf('?');
		// Transaction transaction = Cat.newTransaction("Http", index != -1 ? url.substring(0, index) : url);
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(httpPost)) {
			HttpEntity e = response.getEntity();
			// transaction.setStatus(Transaction.SUCCESS);
			return EntityUtils.toString(e, StandardCharsets.UTF_8.toString());
		} catch (Exception e) {
			// transaction.setStatus(e);
			log.error("executeByHttpClient error", e);
		} finally {
			// transaction.complete();
		}
		return "";
	}

	private static String executeByOkHttp(Request request, String url) {
		int index = url.indexOf('?');
		// Transaction transaction = Cat.newTransaction("Http", index != -1 ? url.substring(0, index) : url);
		try (Response response = client.newCall(request).execute()) {
//			transaction.setStatus(Transaction.SUCCESS);
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				log.error("error rsp:{}", response);
			}
		} catch (Exception e) {
//			transaction.setStatus(e);
			log.error("okhttp3 excute error", e);
		} finally {
//			transaction.complete();
		}
		return "";
	}

    public static Map<String, String> setHeader(String token) {
        Map<String, String> headers = new HashMap<>(4);
        headers.put(PARAM_PLATFORMID, "number2");
        headers.put(PARAM_PLATFORMKEY, "HSX6PJC8Nb2Re122");
        headers.put(PARAM_PLATFORMCODD, "fJ6ryGymqhdszWqG");
        headers.put(PARAM_PLATFORM, "3");
        headers.put("userToken", token != null ? token : "");
        headers.put("tsign", "ecd477791fea26d70b6d66d6f2c76b36353984a3");// 默认tsign
        try {
            String sign = MD5Util.md5Encode(headers.get("userToken") + "champion" + "ecd477791fea26d70b6d66d6f2c76b36353984a3");//签名
            headers.put("sign", sign);
        } catch (Exception e) {
        	log.error("md5Encode error: {}", e);
        }
        return headers;
    }

    private static void addHeader(HttpRequestBase request,Map<String,String> headers) {
    	if (headers != null && headers.size() > 0) {
            for (Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
    
	public static String getUrlString(String url, Map<String, String> queries, boolean needUrlEncode) {
		StringBuilder sb = new StringBuilder(url);
        try {
            if (queries != null && !queries.keySet().isEmpty()) {
                boolean firstFlag = true;
                for (Entry<String, String> entry : queries.entrySet()) {
                    if (firstFlag) {
                        sb.append("?" + entry.getKey() + "=" + (needUrlEncode ? URLEncoder.encode(entry.getValue(), "UTF-8") : entry.getValue()));
                        firstFlag = false;
                    } else {
                        sb.append("&" + entry.getKey() + "=" + (needUrlEncode ? URLEncoder.encode(entry.getValue(), "UTF-8") : entry.getValue()));
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
	}

	static class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

        public static final String METHOD_NAME = "DELETE";

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }

        public HttpDeleteWithBody(final String url) {
            super();
            setURI(URI.create(url));
        }
    }

}

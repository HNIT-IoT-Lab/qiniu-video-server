package cn.hnit.utils.web;

import cn.hnit.utils.LocalThreadUtil;
import cn.hnit.utils.common.bean.HeaderParam;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * 获取请求Body 从request里面
 * @author liangfengyuan
 */
@Slf4j
public class HttpHelper {

	public static String getBodyString(ServletRequest request) {
		StringBuilder sb = new StringBuilder();
		try (InputStream inputStream = request.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			log.error("getBodyString error",e);
		}
		return sb.toString();
	}

    /**
     * 日志打印
     * @return
     */
    public static HeaderParam log(HttpServletRequest request) {
        //获取头和form参数
        Enumeration<String> enumeration = null;
        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();
        int i = 0;
        while (i < 2) {
            if (i == 0) {
                enumeration = request.getHeaderNames();
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    header.put(key, request.getHeader(key));
                }
            }else{
                enumeration = request.getParameterNames();
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    params.put(key, request.getParameter(key));
                }
            }
            i++;
        }
        HeaderParam headerParam = header.toJavaObject(HeaderParam.class);
        if (StrUtil.isEmpty(headerParam.getUserToken())) {
            headerParam.setUserToken(header.getString("usertoken"));
        }
        headerParam.setRequestTime(System.currentTimeMillis());
        LocalThreadUtil.setLocalObj(headerParam);
        log.info("\n请求开始时间{} \n请求url：{} \n请求头信息为:{} \n解析的请求头信息为:{} \n请求body：{} \n请求query{} \n请求方法{} \n请求host{}\n请求时间{}",
                System.nanoTime(),request.getRequestURI(),header, headerParam, HttpHelper.getBodyString(request)
                ,params,request.getMethod(), request.getRemoteHost(),headerParam.getRequestTime());
        return headerParam;
    }
}

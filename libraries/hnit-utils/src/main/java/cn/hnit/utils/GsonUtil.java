package cn.hnit.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * json转换工具类
 */
@Slf4j
public class GsonUtil {
    /**
     * 用于驼峰与下划线转换的gson
     */
    private static Gson humpGson = null;

    private static ObjectMapper jacksonMapper= null;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        humpGson = builder.create();
        jacksonMapper = new ObjectMapper();
        jacksonMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jacksonMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jacksonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jacksonMapper.registerModule(new JavaTimeModule());
    }

    private GsonUtil() {
    }

    /**
     * 将json串转为实体下划线转为驼峰
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToHumpObject(String json, Class<T> clazz) {
        return humpGson.fromJson(json, clazz);
    }

    public static <T> T jsonToHumpObject(JSONObject jsonObject, Class<T> clazz) {
        return jsonToHumpObject(jsonObject.toJSONString(), clazz);
    }

    public static String obj2UnderlineJson(Object object) {
        return humpGson.toJson(object);
    }

    public static Map<String, String> obj2UnderlineMap(Object object) {
        String json = obj2UnderlineJson(object);
        return humpGson.fromJson(json, Map.class);
    }

    public static byte[] obj2Bytes(Object obj){
        try {
            return jacksonMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error("json数据处理异常", e);
        }
        return null;
    }

}
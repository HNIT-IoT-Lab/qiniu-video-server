package cn.hnit.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 拷贝Bean
 */
@Slf4j
public final class BeanUtil extends BeanUtils {

    private BeanUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 拷贝属性
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (checkNull(source, targetClass)) {
            return null;
        }
        try {
            T newInstance = targetClass.newInstance();
            copyProperties(source, newInstance);
            return newInstance;
        } catch (Exception e) {
            log.error("error: ", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepClone(T object) {
        T cloneObject = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            cloneObject = (T) objectInputStream.readObject();
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e) {
            log.info("拷贝异常：：", e);
        }
        return cloneObject;
    }

    public static <T> T copyProperties(Object source, Class<T> targetClass, String... ignoreProperties) {
        if (checkNull(source, targetClass)) {
            return null;
        }
        try {
            T newInstance = targetClass.newInstance();
            copyProperties(source, newInstance, ignoreProperties);
            return newInstance;
        } catch (Exception e) {
            log.error("error: ", e);
            return null;
        }
    }

    /**
     * 拷贝集合
     */
    public static <T> List<T> copyProperties(List<?> sources, Class<T> targetClass) {
        if (checkNull(sources, targetClass) || sources.isEmpty()) {
            return new ArrayList<>();
        }
        return sources.stream().map(source -> copyProperties(source, targetClass)).collect(Collectors.toList());
    }

    private static <T> boolean checkNull(Object source, Class<T> targetClass) {
        return Objects.isNull(source) || Objects.isNull(targetClass);
    }

}

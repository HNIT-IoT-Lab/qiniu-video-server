package cn.hnit.starter.intercept.aop;

import cn.hnit.utils.SpelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aspect基类，提供基本的操作
 *
 * @author 梁峰源
 * @since 2022年9月16日11:30:54
 */
public abstract class BaseAop {

    /**
     * 获取切入目标方法
     *
     * @return
     */
    protected Method getTargetMethod(final ProceedingJoinPoint pjp) {
        return ((MethodSignature) pjp.getSignature()).getMethod();
    }

    /**
     * 获取切入目标方法参数名
     *
     * @param pjp
     * @return
     */
    protected String[] getMethodParameterNames(final ProceedingJoinPoint pjp) {
        return ((MethodSignature) pjp.getSignature()).getParameterNames();
    }

    /**
     * 获取切入目标上指定的Annotation。此方法优先从方法获取，若不存在则从类获取
     *
     * @param pjp
     * @param annotationClass
     * @return
     */
    protected <T extends Annotation> T getAnnotation(final ProceedingJoinPoint pjp, Class<T> annotationClass) {
        Method targetMethod = getTargetMethod(pjp);
        T annotation = targetMethod.getAnnotation(annotationClass);
        if (annotation == null) {
            annotation = targetMethod.getDeclaringClass().getAnnotation(annotationClass);
        }
        return annotation;
    }

    /**
     * 获取指定方法上指定的Annotation
     *
     * @param method
     * @param annotationClass
     * @return
     */
    protected <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    /**
     * 根据指定注解类获取拦截方法中的参数值
     *
     * @param pjp
     * @param annotationClass
     * @return
     */
    protected List<Object> getMethodParametersByAnnotation(final ProceedingJoinPoint pjp, Class<?> annotationClass) {
        return getMethodParametersByAnnotation(pjp, getTargetMethod(pjp), annotationClass);
    }

    /**
     * 根据指定注解方法获取拦截方法中的参数值
     *
     * @param pjp
     * @param annotationClass
     * @return
     */
    protected List<Object> getMethodParametersByAnnotation(final ProceedingJoinPoint pjp, Method method,
                                                           Class<?> annotationClass) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return Collections.emptyList();
        }

        List<Object> result = new ArrayList<>();
        int i = 0;
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(annotationClass)) {
                    result.add(pjp.getArgs()[i]);
                }
            }
            i++;
        }

        return result;
    }

    /**
     * 根据指定注解方法获取拦截方法中的注解及参数值
     *
     * @param pjp
     * @param method
     * @param annotationClass
     * @return List<[ annotation, value ]>
     */
    @SuppressWarnings("unchecked")
    protected <T> List<Pair<T, Object>> getMethodAnnotationAndParametersByAnnotation(final ProceedingJoinPoint pjp,
                                                                                     Method method, Class<T> annotationClass) {

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        if (parameterAnnotations.length == 0) {
            return Collections.emptyList();
        }

        List<Pair<T, Object>> result = new ArrayList<>();
        int i = 0;
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(annotationClass)) {
                    result.add(Pair.of((T) annotation, pjp.getArgs()[i]));
                }
            }
            i++;
        }

        return result;
    }

    /**
     * 根据指定注解方法获取拦截方法中的注解及参数值
     *
     * @param pjp
     * @param method
     * @param annotationClass
     * @return List<[ annotation, value ]>
     */
    @SuppressWarnings("unchecked")
    protected <T> List<Triple<T, Integer, Object>> getMethodAnnotationAndParamIndexAndParametersByAnnotation(final ProceedingJoinPoint pjp,
                                                                                                             Method method, Class<T> annotationClass) {

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        if (parameterAnnotations.length == 0) {
            return Collections.emptyList();
        }
        int i = 0;
        List<Triple<T, Integer, Object>> result = new ArrayList<>();

        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(annotationClass)) {
                    result.add(Triple.of((T) annotation, Integer.valueOf(i), pjp.getArgs()[i]));
                }
            }
            i++;
        }

        return result;
    }

    /**
     * 拼接参数中的值作为redis的key
     *
     * @param keyBuffer
     * @param param     参数值
     * @param value     默认值
     * @return
     */
    protected StringBuilder packParamKey(StringBuilder keyBuffer, Object param, String value) {
        if (param != null && StringUtils.isNotBlank(value)) {
            param = SpelUtils.getValue(param, value);
        }
        if (param == null) {
            keyBuffer.append(":-");
        } else {
            keyBuffer.append(":").append(param.toString().replace(":", "-"));
        }
        return keyBuffer;
    }

}

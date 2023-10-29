package cn.hnit.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁参数，必须结合Lock注解一起使用。<br/>
 * 同一Lock支持多个LockParam，按参数顺序进行拼装
 * @author xsm
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LockParam {

    /**
     * 当修饰参数为复杂对象时，支持按SpEL取属性值。默认为空，直接取值对象本身
     */
    String value() default "";
}

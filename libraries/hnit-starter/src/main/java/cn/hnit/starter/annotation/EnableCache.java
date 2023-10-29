package cn.hnit.starter.annotation;

import cn.hnit.starter.cache.aspect.CacheAop;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启缓存注解的使用
 *
 * @author 梁峰源
 * @since  2022年9月16日11:17:18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(value = CacheAop.class)
public @interface EnableCache {

}

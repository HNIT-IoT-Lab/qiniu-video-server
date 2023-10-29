package cn.hnit.sdk.orm.mongodb.utils;

import cn.hnit.sdk.orm.mongodb.exception.OrmException;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;

/**
 * mongo工具类
 */
@Slf4j
public class MongoUtils {

    private MongoUtils() {
    }

    /**
     * 默认的时间模板
     */
    public static final String DEFAULT_DATE = "%Y-%m-%d";

    /**
     * 点
     */
    private static final String DOT = ".";

    /**
     * 模糊匹配模板
     */
    private static final String LIKE_FORMAT = ".*%s.*";

    /**
     * 获取集合名称
     *
     * @param c 类
     * @return 名称
     */
    public static String getTableName(Class<?> c) {
        Document d;
        String name;
        if ((d = c.getAnnotation(Document.class)) == null || (name = CharSequenceUtil.emptyToDefault(d.value(), d.collection())) == null) {
            throw new OrmException("请使用@Document指定集合名称");
        }
        return name;
    }

    /**
     * 点拼接
     * <p>
     * a.b.c.d.e...
     *
     * @param str 字符串1
     * @return s1.s2
     */
    public static String dot(String... str) {
        return ArrayUtil.join(str, DOT);
    }

    /**
     * 返回模糊匹配regex
     *
     * @param value 匹配值
     * @return 正则
     */
    public static String like(String value) {
        return String.format(LIKE_FORMAT, value);
    }

    /**
     * 时间范围设置
     *
     * @param main  方法
     * @param field 字段
     * @param start 起始时间
     * @param end   结束时间
     */
    public static Criteria betweenTime(Criteria main, String field, LocalDateTime start, LocalDateTime end) {
        return between(main, field, start, end);
    }

    /**
     * 范围设置
     *
     * @param main  条件对象
     * @param field 字段
     * @param min   最小值
     * @param max   最大值
     */
    public static <T> Criteria between(Criteria main, String field, T min, T max) {
        if (min != null || max != null) {
            Criteria between = main.and(field);
            if (min != null) {
                between = between.gte(min);
            }
            if (max != null) {
                between.lte(max);
            }
        }
        return main;
    }
}

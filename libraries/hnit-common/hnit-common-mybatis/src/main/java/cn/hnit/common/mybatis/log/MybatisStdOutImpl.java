package cn.hnit.common.mybatis.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * sql彩色打印
 * <p> 1、删除无用日志信息
 * <p> 2、SQL高亮显示
 *
 * @author king
 * @since 2022-10-09 19:20
 **/
@Slf4j
@Component
public class MybatisStdOutImpl implements Log {
    /**
     * 是否彩色SQL日志
     */
    @Value("${spring.myconfig.colorSql}")
    private Boolean colorSql = Boolean.TRUE;

    public MybatisStdOutImpl(String clazz) {
        // Do Nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        log.error(s);
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    /**
     * MyBatis动作 打印
     * 执行Sql与参数 打印
     */
    @Override
    public void debug(String s) {
        // 以下日志，不再打印
        if (s.startsWith("Creating") || s.startsWith("SqlSession") || s.startsWith("Cache") || s.startsWith("JDBC") || s.startsWith("Closing")) {
            return;
        }
        // 如果是sql语句，则: 蓝色、加粗、下划线
        // 参考：https://blog.csdn.net/soinice/article/details/97052030
        if (colorSql && s.startsWith("==>  Preparing")) {
            log.info("\033[34;1;4m{}\033[0m", s);
//			s = s.replaceAll("==>  Preparing: ", "");
//			s = "==>  Preparing: " + s;
        }
    }

    /**
     * Sql执行结果，打印
     */
    @Override
    public void trace(String s) {
        log.info(s);
    }

    @Override
    public void warn(String s) {
        log.info(s);
    }
}

package cn.hnit.common.mybatis.config;

import cn.hnit.common.mybatis.log.MybatisStdOutImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mapper.xml相关配置注入
 *
 * @author king
 * @since 2022-10-09 20:01
 **/
@Slf4j
@Configuration
public class MyBatisConfig {
    @Value("${spring.myconfig.autoRefreshSql}")
    private boolean autoRefreshSql;
    @Value("${spring.myconfig.refreshDelay}")
    private long refreshDelay;
    @Value("${spring.myconfig.refreshPeriod}")
    private long refreshPeriod;


    @Bean(name = "MybatisMapperDynamicLoader")
    public MybatisMapperDynamicLoader get() {
        if (autoRefreshSql) {
            log.info("mapper.xml开启热刷新");
        }
        return new MybatisMapperDynamicLoader(autoRefreshSql, refreshDelay, refreshPeriod);
    }


    /**
     * 注入日志组件 （从yml文件中配置的方式，打包后有概率无法启动项目且无法解决，故用此方法注入自定义日志组件）
     */
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        sqlSessionFactory.getConfiguration().setLogImpl(MybatisStdOutImpl.class);
    }
}

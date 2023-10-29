package cn.hnit.common.mybatis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * mapper.xml热刷新操作类
 *
 * @author king
 * @since 2022-10-09 20:02
 **/
@Slf4j
public class MybatisMapperDynamicLoader implements InitializingBean, ApplicationContextAware {

    private final boolean enabledAutoRefresh;
    private final Long refreshDelay;
    private final Long refreshPeriod;
    private final Map<String, String> mappers = new HashMap<>();
    private AtomicReference<ConfigurableApplicationContext> atomicContext = null;
    private AtomicReference<Scanner> atomicScanner = null;

    public MybatisMapperDynamicLoader(boolean enabledAutoRefresh, Long refreshDelay, Long refreshPeriod) {
        this.enabledAutoRefresh = enabledAutoRefresh;
        this.refreshDelay = refreshDelay;
        this.refreshPeriod = refreshPeriod;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.atomicContext.set((ConfigurableApplicationContext) applicationContext);
    }

    @Override
    public void afterPropertiesSet() {
        // 如果未开启 直接返回
        if (!enabledAutoRefresh) {
            return;
        }
        try {
            atomicScanner.set(new Scanner());
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (atomicScanner.get().isChanged()) {
                            // System.out.println("load mapper.xml");
                            log.info("mapper.xml热刷新成功，当前时间：【{}】", LocalDateTime.now());
                            atomicScanner.get().reloadXml();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, refreshDelay, refreshPeriod);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    class Scanner {
        private static final String XML_RESOURCE_PATTERN = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "**/*Mapper.xml";
        private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        public Scanner() throws IOException {
            Resource[] resources = findResource();
            if (resources != null) {
                for (Resource resource : resources) {
                    String key = resource.getURI().toString();
                    String value = getMd(resource);
                    mappers.put(key, value);
                }
            }
        }

        public void reloadXml() throws Exception {
            SqlSessionFactory factory = atomicContext.get().getBean(SqlSessionFactory.class);
            Configuration configuration = factory.getConfiguration();

//            org.apache.ibatis.session.Configuration
//            com.baomidou.mybatisplus.core.MybatisConfiguration

            removeConfig(configuration);
            for (Resource resource : findResource()) {
                try {
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(), configuration, resource.toString(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } finally {
                    ErrorContext.instance().reset();
                }
            }
        }

        private void removeConfig(Configuration configuration) throws Exception {
            Class<?> classConfig = configuration.getClass();
            // 如果使用了mybatis-plus，可能会获取不到真正的 Configuration类，需要加上以下代码，才能正确运行   ====== start
            if (!classConfig.equals(Configuration.class)) {
//            	System.err.println("开始 转化");
                classConfig = classConfig.getSuperclass();
            }
            //  ====== end
            clearMap(classConfig, configuration, "mappedStatements");
            clearMap(classConfig, configuration, "caches");
            clearMap(classConfig, configuration, "resultMaps");
            clearMap(classConfig, configuration, "parameterMaps");
            clearMap(classConfig, configuration, "keyGenerators");
            clearMap(classConfig, configuration, "sqlFragments");
            clearSet(classConfig, configuration, "loadedResources");
        }

        @SuppressWarnings("rawtypes")
        private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
            Field field = classConfig.getDeclaredField(fieldName);
            field.setAccessible(true);
            ((Map) field.get(configuration)).clear();
        }

        @SuppressWarnings("rawtypes")
        private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
            Field field = classConfig.getDeclaredField(fieldName);
            field.setAccessible(true);
            ((Set) field.get(configuration)).clear();
        }

        public boolean isChanged() throws IOException {
            boolean isChanged = false;
            for (Resource resource : findResource()) {
                String key = resource.getURI().toString();
                String value = getMd(resource);
                if (!value.equals(mappers.get(key))) {
                    isChanged = true;
                    mappers.put(key, value);
                }
            }
            return isChanged;
        }

        private Resource[] findResource() throws IOException {
            return resourcePatternResolver.getResources(XML_RESOURCE_PATTERN);
        }

        private String getMd(Resource resource) throws IOException {
            return new StringBuilder().append(resource.contentLength()).append("-").append(resource.lastModified()).toString();
        }
    }
}

package cn.hnit.starter.cache.manager;

import cn.hnit.starter.cache.core.McRedisCache;
import cn.hnit.starter.cache.core.McRedisCacheWriter;
import com.google.common.collect.Lists;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author 梁峰源
 * @since 2022/10/2 12:09
 */
public class McRedisCacheManager extends RedisCacheManager {

    private final McRedisCacheWriter cacheWriter;
    private final RedisCacheConfiguration defaultCacheConfig;



    public McRedisCacheManager(McRedisCacheWriter cacheWriter,
                               RedisCacheConfiguration defaultCacheConfiguration,
                               Map<String, RedisCacheConfiguration> initialCaches,
                               boolean allowInFlightCacheCreation) {

        super(cacheWriter, defaultCacheConfiguration, initialCaches, allowInFlightCacheCreation);
        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        return new McRedisCache(name, this.cacheWriter, cacheConfig != null ? cacheConfig : this.defaultCacheConfig);
    }


    /**
     * Entry point for builder style {@link RedisCacheManager} configuration.
     *
     * @param cacheWriter must not be {@literal null}.
     * @return new {@link RedisCacheManagerBuilder}.
     */
    public static McRedisCacheManagerBuilder builder(McRedisCacheWriter cacheWriter) {

        Assert.notNull(cacheWriter, "CacheWriter must not be null!");

        return McRedisCacheManagerBuilder.fromCacheWriter(cacheWriter);
    }


    public static class McRedisCacheManagerBuilder  {
        private final McRedisCacheWriter cacheWriter;
        private RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        private final Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
        private boolean enableTransactions;
        boolean allowInFlightCacheCreation = true;

        private McRedisCacheManagerBuilder(McRedisCacheWriter cacheWriter) {
            this.cacheWriter = cacheWriter;
        }

        /**
         * Entry point for builder style {@link RedisCacheManager} configuration.
         *
         * @param connectionFactory must not be {@literal null}.
         * @return new {@link RedisCacheManagerBuilder}.
         */
        public static McRedisCacheManagerBuilder fromConnectionFactory(RedisConnectionFactory connectionFactory) {

            Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");

            return builder(new McRedisCacheWriter(connectionFactory));
        }

        /**
         * Entry point for builder style {@link RedisCacheManager} configuration.
         *
         * @param cacheWriter must not be {@literal null}.
         * @return new {@link RedisCacheManagerBuilder}.
         */
        public static McRedisCacheManagerBuilder fromCacheWriter(McRedisCacheWriter cacheWriter) {

            Assert.notNull(cacheWriter, "CacheWriter must not be null!");

            return new McRedisCacheManagerBuilder(cacheWriter);
        }

        /**
         * Define a default {@link RedisCacheConfiguration} applied to dynamically created {@link RedisCache}s.
         *
         * @param defaultCacheConfiguration must not be {@literal null}.
         * @return this {@link RedisCacheManagerBuilder}.
         */
        public McRedisCacheManagerBuilder cacheDefaults(RedisCacheConfiguration defaultCacheConfiguration) {

            Assert.notNull(defaultCacheConfiguration, "DefaultCacheConfiguration must not be null!");

            this.defaultCacheConfiguration = defaultCacheConfiguration;

            return this;
        }

        /**
         * Enable {@link RedisCache}s to synchronize cache put/evict operations with ongoing Spring-managed transactions.
         *
         * @return this {@link RedisCacheManagerBuilder}.
         */
        public McRedisCacheManagerBuilder transactionAware() {

            this.enableTransactions = true;

            return this;
        }

        /**
         * Append a {@link Set} of cache names to be pre initialized with current {@link RedisCacheConfiguration}.
         * <strong>NOTE:</strong> This calls depends on {@link #cacheDefaults(RedisCacheConfiguration)} using whatever
         * default {@link RedisCacheConfiguration} is present at the time of invoking this method.
         *
         * @param cacheNames must not be {@literal null}.
         * @return this {@link RedisCacheManagerBuilder}.
         */
        public McRedisCacheManagerBuilder initialCacheNames(Set<String> cacheNames) {

            Assert.notNull(cacheNames, "CacheNames must not be null!");

            Map<String, RedisCacheConfiguration> cacheConfigMap = new LinkedHashMap<>(cacheNames.size());
            cacheNames.forEach(it -> cacheConfigMap.put(it, defaultCacheConfiguration));

            return withInitialCacheConfigurations(cacheConfigMap);
        }

        /**
         * Append a {@link Map} of cache name/{@link RedisCacheConfiguration} pairs to be pre initialized.
         *
         * @param cacheConfigurations must not be {@literal null}.
         * @return this {@link RedisCacheManagerBuilder}.
         */
        public McRedisCacheManagerBuilder withInitialCacheConfigurations(
                Map<String, RedisCacheConfiguration> cacheConfigurations) {

            Assert.notNull(cacheConfigurations, "CacheConfigurations must not be null!");
            cacheConfigurations.forEach((cacheName, configuration) -> Assert.notNull(configuration,
                    String.format("RedisCacheConfiguration for cache %s must not be null!", cacheName)));

            this.initialCaches.putAll(cacheConfigurations);

            return this;
        }

        /**
         * Disable in-flight {@link org.springframework.cache.Cache} creation for unconfigured caches.
         * <p />
         * {@link RedisCacheManager#getMissingCache(String)} returns {@literal null} for any unconfigured
         * {@link org.springframework.cache.Cache} instead of a new {@link RedisCache} instance. This allows eg.
         * {@link org.springframework.cache.support.CompositeCacheManager} to chime in.
         *
         * @return this {@link RedisCacheManagerBuilder}.
         * @since 2.0.4
         */
        public McRedisCacheManagerBuilder disableCreateOnMissingCache() {

            this.allowInFlightCacheCreation = false;
            return this;
        }

        /**
         * Create new instance of {@link RedisCacheManager} with configuration options applied.
         *
         * @return new instance of {@link RedisCacheManager}.
         */
        public RedisCacheManager build() {

            RedisCacheManager cm = new McRedisCacheManager(cacheWriter, defaultCacheConfiguration, initialCaches,
                    allowInFlightCacheCreation);

            cm.setTransactionAware(enableTransactions);

            return cm;
        }
    }
}

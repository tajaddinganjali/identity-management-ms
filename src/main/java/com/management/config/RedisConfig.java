package com.management.config;

import com.management.config.serializer.KryoGenericSerializer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("#{${spring.cachge.config}}")
    public Map<String, Long> redisCacheConfigs;
    @Value("${identity-svc.redis-host}")
    private String host;
    @Value("${identity-svc.redis-port}")
    private int port;
    @Value("${identity-svc.redis-password}")
    private String password;
    @Value("${identity-svc.redis-database}")
    private Integer database;
    @Value("${identity-svc.redis-timeout}")
    private Integer timeout;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(password);
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration =
                JedisClientConfiguration.builder();
        jedisClientConfiguration.readTimeout(Duration.ofMillis(timeout));
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(timeout));
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
    }

    @Bean
    public RedissonClient getRedisson() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setPassword(password)
                .setConnectTimeout(timeout)
                .setAddress(String.format("redis://%s:%s", host, port));
        return Redisson.create(config);
    }


    @Bean("commonRedisTemplate")
    public RedisTemplate<String, Object> commonRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean("cacheManager")
    public CacheManager cacheManager(
            @Qualifier("jedisConnectionFactory") JedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        RedisSerializer<Object> kryoGenericSerializer = new KryoGenericSerializer<>();
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(kryoGenericSerializer));
        redisCacheConfigs.forEach((String cacheName, Long cacheTtlMinutes) -> cacheConfigurations.put(cacheName,
                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(cacheTtlMinutes))));
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

}

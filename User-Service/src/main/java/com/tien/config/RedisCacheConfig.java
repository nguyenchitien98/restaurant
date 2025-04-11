package com.tien.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class RedisCacheConfig {
    // RedisCacheConfig để cấu hình TTL (Time-To-Live)
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // cấu hình Redis cache dùng Jackson2JsonRedisSerializer để lưu object dưới dạng JSON,
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .entryTtl(Duration.ofMinutes(5)); // TTL mặc định là 5 phút

        // TTL cho user theo ID (3 phút)
        RedisCacheConfiguration userCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(3));

        // Cấu hình TTL riêng cho từng cache
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        cacheConfigs.put("users", userCacheConfig); // Cache usersPage giờ sẽ tự động hết hạn sau 3 phút.
        cacheConfigs.put("usersPage", defaultConfig.entryTtl(Duration.ofMinutes(2))); //Cache usersPage giờ sẽ tự động hết hạn sau 2 phút.

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigs)
                .cacheDefaults(defaultConfig) // fallback
                .build();
    }
}

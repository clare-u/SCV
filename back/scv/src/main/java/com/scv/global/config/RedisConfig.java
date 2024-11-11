package com.scv.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Primary
    @Bean
    public LettuceConnectionFactory accessTokenBlacklistConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(0);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public LettuceConnectionFactory refreshTokenWhitelistConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(1);
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "accessTokenBlacklistRedisTemplate")
    public RedisTemplate<String, Object> accessTokenBlacklistRedisTemplate() {
        return createRedisTemplate(accessTokenBlacklistConnectionFactory());
    }

    @Bean(name = "refreshTokenWhitelistRedisTemplate")
    public RedisTemplate<String, Object> refreshTokenWhitelistRedisTemplate() {
        return createRedisTemplate(refreshTokenWhitelistConnectionFactory());
    }

    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));

        return template;
    }
}

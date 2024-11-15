package com.scv.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class RedisConfig {

    // Access Token Master
    @Value("${spring.data.redis.token.access.master.host}")
    private String accessMasterHost;

    @Value("${spring.data.redis.token.access.master.port}")
    private int accessMasterPort;

    @Value("${spring.data.redis.token.access.master.password}")
    private String accessMasterPassword;

    // Access Token Slaves
    @Value("${spring.data.redis.token.access.slaves[0].host}")
    private String accessSlave1Host;

    @Value("${spring.data.redis.token.access.slaves[0].port}")
    private int accessSlave1Port;

    @Value("${spring.data.redis.token.access.slaves[0].password}")
    private String accessSlave1Password;

    @Value("${spring.data.redis.token.access.slaves[1].host}")
    private String accessSlave2Host;

    @Value("${spring.data.redis.token.access.slaves[1].port}")
    private int accessSlave2Port;

    @Value("${spring.data.redis.token.access.slaves[1].password}")
    private String accessSlave2Password;

    @Value("${spring.data.redis.token.access.slaves[2].host}")
    private String accessSlave3Host;

    @Value("${spring.data.redis.token.access.slaves[2].port}")
    private int accessSlave3Port;

    @Value("${spring.data.redis.token.access.slaves[2].password}")
    private String accessSlave3Password;

    // Refresh Token Master
    @Value("${spring.data.redis.token.refresh.host}")
    private String refreshMasterHost;

    @Value("${spring.data.redis.token.refresh.port}")
    private int refreshMasterPort;

    @Value("${spring.data.redis.token.refresh.password}")
    private String refreshMasterPassword;

    // Access Token Master Connection Factory
    @Primary
    @Bean
    public LettuceConnectionFactory accessMasterConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(accessMasterHost, accessMasterPort);
        config.setPassword(accessMasterPassword);
        return new LettuceConnectionFactory(config);
    }

    // Access Token Slaves Connection Factories
    @Bean(name = "accessSlave1ConnectionFactory")
    public LettuceConnectionFactory accessSlave1ConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(accessSlave1Host, accessSlave1Port);
        config.setPassword(accessSlave1Password);
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "accessSlave2ConnectionFactory")
    public LettuceConnectionFactory accessSlave2ConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(accessSlave2Host, accessSlave2Port);
        config.setPassword(accessSlave2Password);
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "accessSlave3ConnectionFactory")
    public LettuceConnectionFactory accessSlave3ConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(accessSlave3Host, accessSlave3Port);
        config.setPassword(accessSlave3Password);
        return new LettuceConnectionFactory(config);
    }

    // Refresh Token Master Connection Factory
    @Bean
    public LettuceConnectionFactory refreshMasterConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(refreshMasterHost, refreshMasterPort);
        config.setPassword(refreshMasterPassword);
        return new LettuceConnectionFactory(config);
    }

    // RedisTemplate for Access Token Master (Write operations)
    @Bean(name = "accessMasterTemplate")
    public RedisTemplate<String, Object> accessMasterTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(accessMasterConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        log.error("accessMasterHost: {}", accessMasterHost);
        log.error("accessMasterPort: {}", accessMasterPort);

        return template;
    }

    @Bean(name = "accessSlaveTemplates")
    public List<RedisTemplate<String, Object>> accessSlaveTemplates() {
        return Arrays.asList(accessSlave1Template(), accessSlave2Template(), accessSlave3Template());
    }

    // RedisTemplates for Access Token Slaves (Read operations)
    @Bean(name = "accessSlave1Template")
    public RedisTemplate<String, Object> accessSlave1Template() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(accessSlave1ConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        log.error("accessSlave1Host: {}", accessSlave1Host);
        log.error("accessSlave1Port: {}", accessSlave1Port);

        return template;
    }

    @Bean(name = "accessSlave2Template")
    public RedisTemplate<String, Object> accessSlave2Template() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(accessSlave2ConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        log.error("accessSlave2Host: {}", accessSlave2Host);
        log.error("accessSlave2Port: {}", accessSlave2Port);

        return template;
    }

    @Bean(name = "accessSlave3Template")
    public RedisTemplate<String, Object> accessSlave3Template() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(accessSlave3ConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        log.error("accessSlave3Host: {}", accessSlave3Host);
        log.error("accessSlave3Port: {}", accessSlave3Port);

        return template;
    }

    // RedisTemplate for Refresh Token (Single Master)
    @Bean(name = "refreshMasterTemplate")
    public RedisTemplate<String, Object> refreshMasterTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(refreshMasterConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        log.error("refreshMasterHost: {}", refreshMasterHost);
        log.error("refreshMasterPort: {}", refreshMasterPort);

        return template;
    }
}

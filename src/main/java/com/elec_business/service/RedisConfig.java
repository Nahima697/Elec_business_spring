package com.elec_business.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class RedisConfig {

    @Value("${REDIS_URL:}")
    private String redisUrl;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() throws URISyntaxException {
        if (redisUrl == null || redisUrl.isEmpty()) {
            throw new IllegalStateException("REDIS_URL environment variable is not set!");
        }

        URI redisUri = new URI(redisUrl);

        String host = redisUri.getHost();
        int port = redisUri.getPort();

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}

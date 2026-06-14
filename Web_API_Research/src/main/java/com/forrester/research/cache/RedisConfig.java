package com.forrester.research.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Collection;

@Configuration
@EnableKafka
@ComponentScan("com.forrester.research")
public class RedisConfig {

    @Value("#{'${spring.redis.cluster.nodes}'.split(',')}")
    private Collection<String> redisNodes;

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
    	    return new JedisConnectionFactory(new RedisClusterConfiguration(redisNodes));
    }


    @Bean
    public RedisTemplate<String, Object> getRedisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }
}
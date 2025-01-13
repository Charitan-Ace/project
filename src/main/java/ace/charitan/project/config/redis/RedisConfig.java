package ace.charitan.project.config.redis;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import ace.charitan.project.internal.project.dto.project.InternalProjectDtoImpl;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.sentinel.master}")
    private String sentinelMasterName;

    @Value("${spring.data.redis.sentinel.nodes}")
    private List<String> sentinelNodes;

    @Value("${spring.data.redis.pool.max-active}")
    private int maxActive;

    @Value("${spring.data.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.data.redis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.data.redis.pool.max-wait-time}")
    private long maxWaitTime;

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(
                redisSentinelConfiguration(),
                poolConfig());
    }

    @Bean(name = "poolConfig")
    public JedisPoolConfig poolConfig() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setMaxWaitMillis(maxWaitTime);
        return jedisPoolConfig;
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        return new RedisSentinelConfiguration(sentinelMasterName,
                new HashSet<String>(sentinelNodes));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    // Caching single project object
    @Bean
    RedisTemplate<String, InternalProjectDtoImpl> redisProjectTemplate(ObjectMapper objectMapper) {
        RedisTemplate<String, InternalProjectDtoImpl> template = new RedisTemplate<String, InternalProjectDtoImpl>();

        Jackson2JsonRedisSerializer<InternalProjectDtoImpl> jsonSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper, InternalProjectDtoImpl.class);

        template.setConnectionFactory(jedisConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setHashValueSerializer(jsonSerializer);
        template.setValueSerializer(jsonSerializer);

        return template;
    }


    // Caching list of project objects
    @Bean
    RedisTemplate<String, String> redisProjectZSetTemplate(ObjectMapper objectMapper) {
        RedisTemplate<String, String> template = new RedisTemplate<String, String>();

        Jackson2JsonRedisSerializer<String> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,
                String.class);

        template.setConnectionFactory(jedisConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setHashValueSerializer(jsonSerializer);
        template.setValueSerializer(jsonSerializer);

        return template;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}

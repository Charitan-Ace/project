package ace.charitan.project.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import ace.charitan.project.internal.project.dto.project.InternalProjectDtoImpl;

@Configuration
public class JedisConfig {

    @Value("redis.host-name")
    private String redisHostName;

    @Value("redis.port")
    private Integer redisPort;
    

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHostName);
        redisStandaloneConfiguration.setPort(redisPort);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    @Bean
    public RedisTemplate<String, InternalProjectDtoImpl> redisTemplate(
            ObjectMapper objectMapper) {
        RedisTemplate<String, InternalProjectDtoImpl> template = new RedisTemplate<String, InternalProjectDtoImpl>();
        Jackson2JsonRedisSerializer<InternalProjectDtoImpl> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,
                InternalProjectDtoImpl.class);
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer);
        template.setValueSerializer(jsonSerializer);
        return template;
    }

}

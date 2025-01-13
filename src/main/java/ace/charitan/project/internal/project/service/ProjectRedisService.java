package ace.charitan.project.internal.project.service;

import static ace.charitan.project.config.redis.RedisConstant.PROJECT_CACHE_PREFIX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import ace.charitan.project.internal.project.dto.project.InternalProjectDtoImpl;

@Component
class ProjectRedisService {

    @Autowired
    @Qualifier("redisProjectTemplate")
    private RedisTemplate<String, InternalProjectDtoImpl> redisProjectTemplate;

    @Autowired
    @Qualifier("redisProjectZSetTemplate")
    private RedisTemplate<String, String> redisProjectZSetTemplate;

    void createProject(InternalProjectDtoImpl internalProjectDto) {

        // Cache by id
        redisProjectTemplate.opsForValue().set(PROJECT_CACHE_PREFIX + internalProjectDto.getId(), internalProjectDto);

        // Cache by
    }

}

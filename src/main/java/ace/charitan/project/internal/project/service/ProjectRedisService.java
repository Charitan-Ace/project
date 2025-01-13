package ace.charitan.project.internal.project.service;

import static ace.charitan.project.config.redis.RedisConstant.PROJECT_CACHE_PREFIX;
import static ace.charitan.project.config.redis.RedisConstant.PROJECT_LIST_CACHE_KEY_USER_ID;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

//    @Autowired
//    @Qualifier("redisProjectZSetTemplate")
//    private RedisTemplate<String, String> redisProjectZSetTemplate;

    void cacheById(InternalProjectDtoImpl projectDto) {
        redisProjectTemplate.opsForValue().set(PROJECT_CACHE_PREFIX + projectDto.getId(), projectDto, 10,
                TimeUnit.MINUTES);
    }

    void cacheByCharityId(InternalProjectDtoImpl internalProjectDto) {
        redisProjectTemplate.opsForSet().add(
                PROJECT_LIST_CACHE_KEY_USER_ID + ":" + internalProjectDto.getCharityId(),
                internalProjectDto
        );
    }

    void createProject(InternalProjectDtoImpl internalProjectDto) {

        // Cache by id
        cacheById(internalProjectDto);

        // Cache by charity Id
        cacheByCharityId(internalProjectDto);
    }

    List<InternalProjectDtoImpl> findListByUserId(String userId) {
        String key = PROJECT_LIST_CACHE_KEY_USER_ID + ":" + userId;
        Set<InternalProjectDtoImpl> projectDtoSet =  redisProjectTemplate.opsForSet().members(key);

        return !Objects.isNull(projectDtoSet) ? projectDtoSet.stream().toList() : List.of();
    }

    InternalProjectDtoImpl findOneById(String projectId) {
        return redisProjectTemplate.opsForValue().get(PROJECT_CACHE_PREFIX + projectId);
    }

}

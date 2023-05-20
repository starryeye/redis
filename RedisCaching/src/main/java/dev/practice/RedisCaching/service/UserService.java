package dev.practice.RedisCaching.service;

import dev.practice.RedisCaching.dto.UserProfile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final String KEY_PATTERN = "nameKey:%s";

    private final ExternalApiService externalApiService;
    private final ValueOperations<String, String> ops;

    public UserService(ExternalApiService externalApiService, StringRedisTemplate stringRedisTemplate) {
        this.externalApiService = externalApiService;
        this.ops = stringRedisTemplate.opsForValue();
    }

    /**
     * userName 에 대해서 Cache-Aside(Lazy Loading) 전략을 적용
     */
    public UserProfile getUserProfile(String userId) {

        String key = String.format(KEY_PATTERN, userId);

        String userName = null;

        String cachedUserName = ops.get(key);
        if(cachedUserName != null) {
            userName = cachedUserName;
        }else {
            userName = externalApiService.getUserName(userId);
            ops.set(key, userName, 5L, TimeUnit.SECONDS); //5초 만료 정책
        }

        int userAge = externalApiService.getUserAge(userId);

        return UserProfile.builder()
                .name(userName)
                .age(userAge)
                .build();
    }
}

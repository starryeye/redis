package dev.practice.LeaderBoard.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RankingService {

    private static final String LEADERBOARD_KEY = "leaderBoard";

    private final ZSetOperations<String, String> zSetOperations;

    public RankingService(StringRedisTemplate stringRedisTemplate) {
        this.zSetOperations = stringRedisTemplate.opsForZSet();
    }

    public boolean setUserScore(String userId, Integer score) {
        zSetOperations.add(LEADERBOARD_KEY, userId, score);
        return true;
    }

    public Long getUserRanking(String userId) {
        //내림차순 순위
        return zSetOperations.reverseRank(LEADERBOARD_KEY, userId);
    }

    public List<String> getTopRank(Long limit) {
        //내림차순 순위 리스트
        return Objects.requireNonNull(zSetOperations.reverseRange(LEADERBOARD_KEY, 0, limit - 1))
                .stream().toList();

    }
}

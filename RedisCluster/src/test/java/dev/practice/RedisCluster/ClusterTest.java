package dev.practice.RedisCluster;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.stream.IntStream;

/**
 * 1. 다양한 클러스터에 값이 골고루 나눠서 들어가도록 한다.
 * 2. 특정 마스터를 죽인다. (fail-over 동작)
 * 3. 들어가있던 값들이 유지 되는지, 서비스는 정상 동작하는지 확인
 */
@SpringBootTest
public class ClusterTest {

    private static final String DUMMY_VALUE = "abc";
    private final ValueOperations<String, String> operations;

    public ClusterTest(RedisTemplate<String, String> redisTemplate) {
        this.operations = redisTemplate.opsForValue();;
    }

    @Test
    void setValue() {

        IntStream.range(0, 1000)
                .parallel()
                .forEach(i -> {
                    String key = String.format("name:%d", i);
                    operations.set(key, DUMMY_VALUE);
                });
    }

    @Test
    void getValue() {

        IntStream.range(0, 1000)
                .parallel()
                .forEach(i -> {
                    String key = String.format("name:%d", i);
                    String value = operations.get(key);

                    Assertions.assertThat(value).isEqualTo(DUMMY_VALUE);
                });
    }

}

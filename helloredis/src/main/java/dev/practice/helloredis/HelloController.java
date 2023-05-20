package dev.practice.helloredis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/string/{key}")
    public String getString(@PathVariable String key) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        return ops.get("%s".formatted(key));
    }

    @PostMapping("/string")
    public String setString(@RequestBody KeyValue body) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        ops.set(body.getKey(), body.getValue());

        return "OK";
    }

    @Getter
    @Setter
    static class KeyValue {
        private String key;
        private String value;
    }
}

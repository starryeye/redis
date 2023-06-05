package dev.practice.IssueTracking;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceWithLettuce {

    private final ValueOperations<String, String> valueOperations;

    public InventoryServiceWithLettuce(StringRedisTemplate stringRedisTemplate) {
        valueOperations = stringRedisTemplate.opsForValue();
    }

    public int getCurrentStock(String key) {
        String value = valueOperations.get(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    public void setCurrentStock(String key, int value) {
        valueOperations.set(key, String.valueOf(value));
    }
}

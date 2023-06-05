package dev.practice.IssueTracking;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceWithRedisson {

    private final RedissonClient redissonClient;

    public int getCurrentStock(String key) {
        return Integer.parseInt(redissonClient.<String>getBucket(key).get());
    }

    public void setCurrentStock(String key, int value) {
        redissonClient.getBucket(key).set(String.valueOf(value));
    }
}

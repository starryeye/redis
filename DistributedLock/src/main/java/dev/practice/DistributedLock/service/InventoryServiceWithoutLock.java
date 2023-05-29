package dev.practice.DistributedLock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InventoryServiceWithoutLock {

    private static final int STOCK_EMPTY = 0;

    private final ValueOperations<String, String> valueOperations;

    public InventoryServiceWithoutLock(StringRedisTemplate stringRedisTemplate) {
        this.valueOperations = stringRedisTemplate.opsForValue();
    }

    public void reduceStock(String productId, int amount) {

        Thread currentThread = Thread.currentThread();

        int stock = getCurrentStock(productId);

        if(stock - amount < STOCK_EMPTY) {
            log.info("{} not enough stock.. productId={}, current amount={}, need amount={}",
                    currentThread.getName(), productId, stock, amount);
            return;
        }
        
        setStock(productId, stock - amount);
    }

    private int getCurrentStock(String key) {
        String value = valueOperations.get(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    private void setStock(String key, int value) {
        valueOperations.set(key, String.valueOf(value));
    }
}

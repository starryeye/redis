package dev.practice.DistributedLock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * Lettuce 에서도 락을 지원하는데 setnx 메서드로 스핀락을 사용할 수 있다.
 * 락 획득에 실패하면 지속적인 락 점유 시도를 하게 되며, redis 는 부하를 받게 되므로 권장되지 않는 방법이다.
 * 또한, 최대 락 점유 시간 설정이 없어서, 락을 점유하고 있는 상황에서 장애가 발생하면 문제가 생긴다.
 *
 * 참고
 * Application 이 단 하나일 경우엔 하나의 JVM 내의 멀티 스레드 동시성 문제만 해결하면 되므로
 * Java ReentrantLock 을 사용하면 Redis 에 부하를 주지 않고 해결 가능하다.
 */
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

        log.info("{} success reduce stock.. productId={}, previous amount={}, after amount={}",
                currentThread.getName(), productId, stock, stock - amount);
    }

    public void addStock(String productId, int amount) {
        Thread currentThread = Thread.currentThread();

        int stock = getCurrentStock(productId);

        setStock(productId, stock + amount);

        log.info("{} success add stock.. productId={}, previous amount={}, after amount={}",
                currentThread.getName(), productId, stock, stock + amount);
    }

    public void initStock(String productId) {
        Thread currentThread = Thread.currentThread();

        int stock = getCurrentStock(productId);

        setStock(productId, 0);

        log.info("{} success init stock.. productId={}, previous amount={}, after amount={}",
                currentThread.getName(), productId, stock, 0);
    }

    public int getCurrentStock(String key) {
        String value = valueOperations.get(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    private void setStock(String key, int value) {
        valueOperations.set(key, String.valueOf(value));
    }
}

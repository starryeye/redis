package dev.practice.DistributedLock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 는 싱글 스레드 모델로 동작한다. 순차적으로 한번에 하나의 명령어만 처리한다는 뜻이다.
 * 하지만, Redis 에 여러 클라이언트가 연결하고 동시에 요청을 보낼 수 있다.
 *
 * 분산 락의 필요성
 * - 단순히 어느 한 데이터에 대해 Redis 의 INCR, DECR 을 사용하면 원자적으로 수행되어 Race Condition 문제가 발생하지 않는다.
 * - 하지만, 비즈니스 로직이 복잡해질 경우 race condition 이 생길 수 있다.
 * - 아래 Service 와 같이 재고가 0 이하로 떨어지지 않도록..
 * - 재고를 먼저 조회하고, 0 이하가 안되면 재고를 수정하는 상황처럼..
 * - 조회와 수정이 별도의 연산으로 이루어지는 경우 Race Condition 문제가 발생할 수 있다. (동시에 재고 확인 후, 동일한 값으로 수정)
 * 따라서, 분산 락을 활용하면 두개의 별도 연산을 원자적으로 수행 할 수 있게 된다.
 *
 * RLock 의 lock, tryLock 메서드
 * lock 은 락을 획득할 수 있을 때까지, 현재 스레드를 block 한다.
 * tryLock 은 락을 획득 할 수 있으면, true return, 아니면, false return 이다. (lock 과 다른점은 최대 대기 시간이 존재한다는 점)
 *
 * Redisson(내부) 의 락 획득 과정
 * Redis 의 Pub/Sub 기능을 활용하여 락의 상태 변경을 알린다.
 * 락이 해제 되면 발생하는 이벤트를 다른 클라이언트가 구독할 수 있도록 한다.
 * Pub/Sub 모델 활용 시, 락 상태 확인을 위해 Redis 에 지속적으로 요청을 보내는 부하를 줄일 수 있게 된다.
 * 해당 과정은 알아서 진행 되므로, 개발자는 lock, tryLock 만 쓰면 된다.
 *
 *
 * fair, 전략 설정..
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final int STOCK_EMPTY = 0;
    private static final String LOCK_NAME = "%s:lock";

    private final RedissonClient redissonClient;

    public void reduceStock(String productId, int amount) {

        Thread currentThread = Thread.currentThread();

        /**
         * RLock 은 Redisson 에서 제공하는 분산형 락 서비스이다.
         * 일반적인 Java 의 Lock 처럼 사용할 수 있으나,
         * 락의 범위가.. 단일 Application 이 아니라 클러스터 전체이다.
         * Java 의 ReentrantLock 을 따르며, 공정한(fair) 락이나 비공정한(unfair) 락 모드를 지원한다.
         * - 공정 : 요청한 순서대로 락 획득, redissonClient.getFairLock()
         * - 비공정 : 순서 없이 랜덤 락 획득, redissonClient.getLock()
         *
         * RLock 은 AutoCloseable 을 구현하고 있으므로, try-with-resources 구문을 사용할 수 있으나..
         * tryLock() 메서드가 check exception 을 던져서 try-with-resources 구문을 적용하여도 복잡해져서 적용하지 않음
         */
        RLock lock = redissonClient.getLock(String.format(LOCK_NAME, productId));

        try {
            /**
             * 락 획득하지 못할 시, 1초간 대기
             * 락 점유 시, 최대 3초간 점유
             */
            if (!lock.tryLock(1L, 3L, TimeUnit.SECONDS)){
                log.warn("{} failed to acquire lock.. productId={}, need amount={}",
                        currentThread.getName(), productId, amount);
                return;
            }

            //락 점유
            int stock = getCurrentStock(productId);

            if (stock - amount < STOCK_EMPTY) {
                log.info("{} not enough stock.. productId={}, current amount={}, need amount={}",
                        currentThread.getName(), productId, stock, amount);
                return;
            }

            setStock(productId, stock - amount);

        } catch (InterruptedException e) {
            log.error("{} failed to acquire lock due to interruption.. productId={}, need amount={}",
                    currentThread.getName(), productId, amount, e);
            currentThread.interrupt();
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * getBucket() 메서드는 RBucket 이라는 Redisson 에서 제공하는 타입을 리턴한다.
     * RBucket 은 가장 기본적인 Redis String Data Type 에 대응되는 데이터 Holder 이다.
     * 따라서, RBucket 에다가 get() 을 하면 데이터를 받을 수 있고..
     * set() 을 하면 데이터를 수정할 수 있다.
     */
    private int getCurrentStock(String key) {
        return (int) redissonClient.getBucket(key).get();
    }

    private void setStock(String key, int value) {
        redissonClient.getBucket(key).set(value);
    }
}

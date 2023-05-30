package dev.practice.DistributedLock.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryServiceWithoutLock inventoryServiceWithoutLock;

    private final String productId = "item";

    @BeforeEach
    void init() {
        inventoryServiceWithoutLock.initStock(productId);
        inventoryServiceWithoutLock.addStock(productId, 100);
        int currentStock = inventoryServiceWithoutLock.getCurrentStock(productId);

        log.info("data initialize.. productId={}, stock={}", productId, currentStock);
    }

    /**
     * 원래 분산 환경에서 Test 를 수행해야 하지만,
     * 분산 락은 멀티 쓰레드 환경에서도 동작하고,
     * 락이 없는 서비스는 멀티 쓰레드 환경에서도 race condition 문제가 있으므로
     * 멀티 쓰레드 Test 로 대체 한다.
     */
    @Test
    void reduceStockTestWithoutLock() throws InterruptedException {
        //given
        int workerCount = 100;
        int amount = 2;
        CountDownLatch countDownLatch = new CountDownLatch(workerCount);
        ExecutorService executorService = Executors.newFixedThreadPool(workerCount);

        //when
        List<workerWithoutLock> workerList = Stream.generate(() -> new workerWithoutLock(inventoryServiceWithoutLock, productId, amount, countDownLatch))
                .limit(workerCount)
                .toList();
        workerList.forEach(executorService::execute);

        //다른 스레드에서 숫자 100 을 감소시켜 0이 될 때 까지(해당 테스트에서는 모든 작업 완료 까지), block
        countDownLatch.await();
        //모든 스레드가 작업이 완료 되었다면 스레드 풀 종료 (예약 성격이지만, 명시적으로 작업 종료 후에 호출)
        executorService.shutdown();

        //then
        int resultStock = inventoryServiceWithoutLock.getCurrentStock(productId);
        Assertions.assertThat(resultStock).isGreaterThan(0); //Race Condition 문제 발생!
    }

    @Test
    void reduceStockTestWithLock() throws InterruptedException {
        //given
        int workerCount = 100;
        int amount = 2;
        CountDownLatch countDownLatch = new CountDownLatch(workerCount);
        ExecutorService executorService = Executors.newFixedThreadPool(workerCount);

        //when
        List<workerWithLock> workerList = Stream.generate(() -> new workerWithLock(inventoryService, productId, amount, countDownLatch))
                .limit(workerCount)
                .toList();

        workerList.forEach(executorService::execute);

        countDownLatch.await();
        executorService.shutdown();

        //then
        int resultStock = inventoryServiceWithoutLock.getCurrentStock(productId);
        Assertions.assertThat(resultStock).isEqualTo(0);
    }

    private static class workerWithoutLock implements Runnable {

        private final InventoryServiceWithoutLock inventoryServiceWithoutLock;
        private final String productId;
        private final int amount;
        private final CountDownLatch countDownLatch;

        public workerWithoutLock(InventoryServiceWithoutLock inventoryServiceWithoutLock, String productId, int amount, CountDownLatch countDownLatch) {
            this.inventoryServiceWithoutLock = inventoryServiceWithoutLock;
            this.productId = productId;
            this.amount = amount;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            inventoryServiceWithoutLock.reduceStock(productId, amount);
            countDownLatch.countDown();
        }
    }

    private static class workerWithLock implements Runnable {

        private final InventoryService inventoryService;
        private final String productId;
        private final int amount;
        private final CountDownLatch countDownLatch;

        public workerWithLock(InventoryService inventoryService, String productId, int amount, CountDownLatch countDownLatch) {
            this.inventoryService = inventoryService;
            this.productId = productId;
            this.amount = amount;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            inventoryService.reduceStock(productId, amount);
            countDownLatch.countDown();
        }
    }
}
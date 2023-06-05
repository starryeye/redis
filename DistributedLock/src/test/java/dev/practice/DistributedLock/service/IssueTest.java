package dev.practice.DistributedLock.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class IssueTest {

    private RedissonClient redissonClient;

    @BeforeEach
    void init() {
        Config config = new Config();

        config.useSingleServer()
                .setAddress("redis://localhost:6379");

        //config.setCodec(new JsonJacksonCodec());

        redissonClient = Redisson.create(config);
    }

    @Test
    void issueTestWithoutLock() throws InterruptedException {
        redissonClient.getBucket("test").set(123);
        ExecutorService e = Executors.newFixedThreadPool(16);
        for (int i = 0; i < 1000; i++) {
            e.submit(() -> {
                try {
                    getValue("test");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        e.shutdown();
        Assertions.assertThat(e.awaitTermination(20, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void issueTestWithLock() throws InterruptedException {

        redissonClient.getBucket("test").set(123);
        ExecutorService e = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 1000; i++) {
            e.submit(() -> {

                Thread currentThread = Thread.currentThread();
                RLock lock = redissonClient.getLock("test::lock");

                try {

                    if(!lock.tryLock(1L, 3L, TimeUnit.SECONDS)) {
                        System.out.println("failed to acquire lock, current thread : " + currentThread.getName());
                        return;
                    }


                    int value = getValue("test");

                    System.out.println("succeeded acquire lock, current thread : " + currentThread.getName() + "value : " + value);

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            });
        }

        e.shutdown();
        Assertions.assertThat(e.awaitTermination(20, TimeUnit.SECONDS)).isTrue();
    }

    private int getValue(String key) {
        return redissonClient.<Integer>getBucket(key).get();
    }
}

package dev.practice.DistributedLock.config;

import dev.practice.DistributedLock.common.RedisProperty;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private static final String SERVER_URL = "redis://%s:%s";

    private final RedisProperty redisProperty;

    /**
     * Spring Data Redis 는 Redis Client 로 Lettuce(Default), Jedis 를 사용한다.
     * 이를 통해 자동으로 RedisTemplate 이 구성되는데..
     * Redisson 은 Spring Data 프로젝트에 속하지 않는다.
     * 따라서, 따로 Client 를 구성하고 직접 사용해야 한다.
     * (그래도 Spring Cache, Spring Session, Spring Data 등과 호환성있게 사용이 용이한 라이브러리이다.)
     *
     * 참고
     * Lettuce, Jedis 는 Redis 데이터를 접근하고 Redis 와의 통신을 추상화하는데 목적이 있으나..
     * Redisson 은 Redis 를 이용한 분산 자바 객체와 분산 서비스의 지원에 목적이 있다.
     */
    @Bean
    public RedissonClient redissonClient() {

        //Redisson Client 를 생성하기 위한 설정 파일
        Config config = new Config();

        config.useSingleServer() //redis 배포 형태 설정
                .setAddress(String.format(SERVER_URL, redisProperty.getHost(), redisProperty.getPort())); // redis address

        /**
         * lettuce 의 기본 코덱은 StringCodec 이다.
         * redisson 의 코덱을 StringCodec 으로 맞춰서 상호 호환이 되도록 한다.
         */
        config.setCodec(new StringCodec());

        return Redisson.create(config);
    }
}

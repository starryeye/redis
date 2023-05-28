package dev.practice.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

/**
 * Redis Streams 를 구독하기 위한 설정
 *
 * 참고
 * Redis Pub/Sub 은 Push 모델 이지만,
 * Redis Streams 는 Pull 모델이다.
 */
@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

    private static final String CONSUMER_GROUP_NAME = "payment-service-group";
    private static final String CONSUMER_NAME = "instance-1";

    private final OrderEventStreamListener orderEventStreamListener;

    @Bean
    public Subscription subscription(RedisConnectionFactory factory) {

        /**
         * Listener Container 객체를 생성하기 위해서 option 객체를 생성(옵션 지정)
         */
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                .build();

        /**
         * option 객체를 이용해서 Listener Container 를 생성
         */
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = StreamMessageListenerContainer.create(factory, options);

        /**
         * Listener Container 에서 Subscription 을 만든다.
         * receiveAutoAck 메서드를 사용하여 리스너(컨슈머)가 Redis 메시지를 처리했다고 Ack 를 남긴다.
         * - 처리 했다고 표시, 같은 컨슈머 그룹의 다른 컨슈머가 중복하여 처리하지 않도록 함
         * - 처리를 성공했다고 가정하는 것이다. (실패해도 자동으로 Ack)
         * - 해당 프로젝트에서는 컨슈머가 하나이므로 의미는 없음
         * Offset 기반의 엔트리 읽기 방법으로 "order-events" key 의 엔트리를 읽는다.
         * - lastConsumed() 메서드로 마지막으로 소비된 메시지 다음 메시지를 소비한다.
         * StreamListener 를 구현한 리스너 객체(orderEventStreamListener)를 넣어준다.
         */
        Subscription subscription = listenerContainer.receiveAutoAck(
                Consumer.from(CONSUMER_GROUP_NAME, CONSUMER_NAME),
                StreamOffset.create("order-events", ReadOffset.lastConsumed()),
                orderEventStreamListener
        );

        /**
         * 실제 메시지 수신 시작
         */
        listenerContainer.start();

        return subscription;
    }
}

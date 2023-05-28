package dev.practice.NotificationService;

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
 */
@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

    private static final String CONSUMER_GROUP_NAME = "notification-service-group";
    private static final String CONSUMER_NAME = "instance-1";

    private final OrderEventStreamListener orderEventStreamListener;
    private final PaymentEventStreamListener paymentEventStreamListener;

    @Bean
    public Subscription orderSubscription(RedisConnectionFactory factory) {

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = getListenerContainer(factory);

        Subscription subscription = listenerContainer.receiveAutoAck(
                Consumer.from(CONSUMER_GROUP_NAME, CONSUMER_NAME),
                StreamOffset.create("order-events", ReadOffset.lastConsumed()),
                orderEventStreamListener
        );

        listenerContainer.start();

        return subscription;
    }

    @Bean
    public Subscription paymentSubscription(RedisConnectionFactory factory) {

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = getListenerContainer(factory);

        Subscription subscription = listenerContainer.receiveAutoAck(
                Consumer.from(CONSUMER_GROUP_NAME, CONSUMER_NAME),
                StreamOffset.create("payment-events", ReadOffset.lastConsumed()),
                paymentEventStreamListener
        );

        listenerContainer.start();

        return subscription;
    }

    private static StreamMessageListenerContainer<String, MapRecord<String, String, String>> getListenerContainer(RedisConnectionFactory factory) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                .build();

        return StreamMessageListenerContainer.create(factory, options);
    }
}

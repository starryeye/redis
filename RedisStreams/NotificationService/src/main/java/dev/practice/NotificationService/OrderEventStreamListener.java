package dev.practice.NotificationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Redis Streams Message Listener
 *
 * OrderService 에서 발행한 order-events 메시지 처리
 */
@Slf4j
@Component
public class OrderEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        log.info("order consumed..");

        Map<String, String> values = message.getValue();

        String userId = values.get("userId");
        String productId = values.get("productId");

        //주문 건에 대한 메일 발송
        //...

        log.info("order userId={}, productId={}", userId, productId);
    }
}

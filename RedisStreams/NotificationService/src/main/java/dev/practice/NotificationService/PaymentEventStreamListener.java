package dev.practice.NotificationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Redis Streams Message Listener
 *
 * PaymentService 에서 발행한 payment-events 메시지 처리
 */
@Slf4j
@Component
public class PaymentEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        log.info("payment consumed..");

        Map<String, String> values = message.getValue();

        String userId = values.get("userId");
        String paymentProcessId = values.get("paymentProcessId");

        //결제 완료 건에 대해 SMS 발송 처리
        //...

        log.info("payment userId={}, productId={}", userId, paymentProcessId);
    }
}

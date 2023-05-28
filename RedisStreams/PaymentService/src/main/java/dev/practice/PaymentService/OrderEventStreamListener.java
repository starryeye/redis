package dev.practice.PaymentService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Redis Streams Message Listener
 */
@Slf4j
@Component
public class OrderEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    //TODO: Caching
    private final AtomicInteger paymentProcessCount = new AtomicInteger(0);
    private static final String STREAM_KEY = "payment-events";

    private final StreamOperations<String, Object, Object> streamOperations;

    public OrderEventStreamListener(StringRedisTemplate stringRedisTemplate) {
         this.streamOperations = stringRedisTemplate.opsForStream();
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        log.info("order consumed..");

        Map<String, String> values = message.getValue();

        String userId = values.get("userId");
        String productId = values.get("productId");
        String price = values.get("price");

        //결제 관련 로직 처리
        String paymentProcessId = Integer.toString(paymentProcessCount.incrementAndGet());


        //결제 완료 이벤트 발행
        HashMap<String, String> fieldMap = new HashMap<>();
        fieldMap.put("userId", userId);
        fieldMap.put("productId", productId);
        fieldMap.put("price", price);
        fieldMap.put("paymentProcessId", paymentProcessId);

        streamOperations.add(STREAM_KEY, fieldMap);

        log.info("payment created.. {}", paymentProcessId);
    }
}

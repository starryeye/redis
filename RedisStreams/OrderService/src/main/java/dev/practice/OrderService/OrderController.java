package dev.practice.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
public class OrderController {

    //Redis Streams 의 Key 이자, Event 이름
    private static final String STREAM_KEY = "order-events";

    private final StreamOperations<String, String, String> streamOperations;

    public OrderController(StringRedisTemplate stringRedisTemplate) {
        this.streamOperations = stringRedisTemplate.opsForStream();
    }

    /**
     * 주문 요청이 오면, Event 를 발행한다.
     */
    @PostMapping("/order")
    public String order(@Valid @RequestBody RequestOrderDto dto) {

        HashMap<String, String> fieldMap = new HashMap<>();
        fieldMap.put("userId", dto.userId());
        fieldMap.put("productId", dto.productId());
        fieldMap.put("price", dto.price());

        streamOperations.add(STREAM_KEY, fieldMap);

        log.info("order created..");

        return "ok";
    }
}

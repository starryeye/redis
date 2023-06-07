package dev.practice.Architecture.adapter.out.persistence.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

/**
 * Redis Hashes
 * Key : @RedisHash value + id
 * -> id 값이 123 이면, Key 는 inventory:123 이다.
 * field : 해당 객체(RedisHashesInventory) 의 모든 필드
 * value : 해당 객체(RedisHashesInventory) 의 모든 필드의 값
 */
@Getter
@ToString
@RedisHash(value = "inventory") //Redis Hashes 와 매핑하겠다!
public class RedisHashesInventory {

    @Id //JPA 의 @Id 가 아닌.. Spring Data 의 @Id 이다.
    private String id; //@RedisHash value 값과 append 되어 Key 가 된다.

    @Indexed //Spring Data Redis 의 CrudRepository 의 findByXXX 로 사용될 수 있도록 만들어 줌.
    private String itemName;

    private Long stock;

    @TimeToLive //TTL, SECONDS 단위가 default
    private Long expire;

    @Builder
    public RedisHashesInventory(String id, String itemName, Long stock, Long expire) {
        this.id = id;
        this.itemName = itemName;
        this.stock = stock;
        this.expire = expire;
    }
}

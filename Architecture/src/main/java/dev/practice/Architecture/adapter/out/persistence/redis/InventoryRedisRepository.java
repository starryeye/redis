package dev.practice.Architecture.adapter.out.persistence.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * CrudRepository 가 제공하는 것 외에 커스텀하게 RedisTemplate 을 사용할 땐,
 * custom interface 와 impl 을 생성하여 InventoryRedisRepository 에서 상속해준다. (Spring Data Jpa 와 동일)
 */
public interface InventoryRedisRepository extends CrudRepository<RedisHashesInventory, String> {
    /**
     * @Indexed 가 적용된 필드는 findByXXX 로 Spring Data 식 인터페이스 문법을 사용할 수 있다.
     */

    //redis-cli 에서 사용되던 명령어(HGET 등)와 매핑할 생각하지말고.. 객체를 통째로 가져온다고 따로 생각하자..
    Optional<RedisHashesInventory> findByItemName(String itemName);
}

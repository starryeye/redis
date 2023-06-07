package dev.practice.Architecture;

import dev.practice.Architecture.adapter.out.persistence.jpa.UserJpaEntity;
import dev.practice.Architecture.adapter.out.persistence.jpa.UserJpaRepository;
import dev.practice.Architecture.adapter.out.persistence.redis.InventoryRedisRepository;
import dev.practice.Architecture.adapter.out.persistence.redis.RedisHashesInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.Optional;

//아래는 생략가능한듯..
@EnableJpaRepositories(basePackages = "dev.practice.Architecture.adapter.out.persistence.jpa")
@EnableRedisRepositories(basePackages = "dev.practice.Architecture.adapter.out.persistence.redis")
@SpringBootApplication
public class ArchitectureApplication {

	@Autowired
	private InventoryRedisRepository inventoryRedisRepository;
	@Autowired
	private UserJpaRepository userJpaRepository;

	public static void main(String[] args) {
		SpringApplication.run(ArchitectureApplication.class, args);
	}

	@Bean
	ApplicationRunner runner() {
		return args -> {

			//Redis
			RedisHashesInventory hashesInventory = RedisHashesInventory.builder()
					.id("123")
					.itemName("item1")
					.stock(10L)
					.expire(300L)
					.build();

			inventoryRedisRepository.save(hashesInventory);

			Optional<RedisHashesInventory> item1 = inventoryRedisRepository.findByItemName("item1");

			System.out.println(item1);

			//-----------------------------------------------

			//RDBMS
			UserJpaEntity jpaEntity = UserJpaEntity.builder()
					.name("user1")
					.build();

			userJpaRepository.save(jpaEntity);

		};
	}

}

# redis
Redis study with Java, Spring boot

## Project
- helloredis
  - RedisTemplate 기본 사용법
- SessionStore
  - Redis 세션 스토어
  - HttpSession + Redis 사용
- RedisCaching
  - Redis 캐싱
  - Redis, Cache-Aside(Lazy Loading) Pattern
  - RedisCacheManager 기본 사용법
- LeaderBoard
  - Redis 미들웨어
  - Redis, Stored Set Data Type 이용한 랭킹 서비스
- PubSubChat
  - Redis Pub/Sub
  - Command line 으로 동작되는 실시간 채팅 서비스
- HelloSentinel
  - Redis Replica, Sentinel
  - Redis의 백업(RDB, AOF)
  - Redis의 트래픽 분산, HA (replica, sentinel)
- RedisCluster
  - Redis Cluster
  - Redis의 트래픽/데이터 분산, HA (Cluster)
- RedisStreams
  - Redis Streams
  - Redis의 Event Broker (Event-Driven Architecture)
  - 주문, 결제, 알림 서비스 (MSA)
  - Subscription, StreamListener 사용
- DistributedLock
  - Redis Distributed Lock
  - Redis Client Redisson 의 분산 락을 사용하여 race condition 해결
- IssueTracking
  - lettuce Client 와 redisson Client 를 동시에 사용할 경우 Serializer(Codec) 을 동일하게 맞춰야한다.
    - [redisson 문의](https://github.com/redisson/redisson/issues/5072)
  - lettuce : StringCodec(default), redisson : Kryo5Codec(default)
- Architecture
  - Spring Data Redis
  - Redis Repository(CrudRepository) 사용법

- 다른 리포지토리
  - [spring-webflux](https://github.com/starryeye/spring-webflux)
    - Spring Data Redis reactive
  
## Dependency
- Java 17
- Spring Boot 3.X
- Spring Web
- Spring Data Redis
- redisson-spring-boot-starter 3.X
- Validation
- Springdoc-openApi
- lombok
- Spring Boot Test

## Posting
- [Redis 시작해보기](https://starryeye.tistory.com/161)
- [Redis Data Type 1](https://starryeye.tistory.com/162)
- [Redis Data Type 2](https://starryeye.tistory.com/163)
- [Redis Data Type 3](https://starryeye.tistory.com/164)
- [Redis Core, I/O, Transaction, Lock](https://starryeye.tistory.com/167)

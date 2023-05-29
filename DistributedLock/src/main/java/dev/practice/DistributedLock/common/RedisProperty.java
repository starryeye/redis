package dev.practice.DistributedLock.common;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@ConfigurationProperties(prefix = "spring.data.redis")
@RequiredArgsConstructor
@Validated
public class RedisProperty {

    @NotEmpty
    private final String host;
    @NotEmpty
    private final String port;
}

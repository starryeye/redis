package dev.practice.DistributedLock;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan({"dev.practice.DistributedLock.common"})
public class DistributedLockConfig {
}

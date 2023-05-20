package dev.practice.RedisCaching.dto;

import lombok.Builder;

@Builder
public record UserProfile(
        String name,
        int age
) {
}

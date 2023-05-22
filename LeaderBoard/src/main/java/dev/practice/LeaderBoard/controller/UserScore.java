package dev.practice.LeaderBoard.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserScore(
        @NotEmpty
        String userId,
        @NotNull
        Integer score
) {
}

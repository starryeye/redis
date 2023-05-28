package dev.practice.OrderService;

import jakarta.validation.constraints.NotEmpty;

public record RequestOrderDto(
        @NotEmpty
        String userId,
        @NotEmpty
        String productId,
        @NotEmpty
        String price
) {
}

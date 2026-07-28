package com.titanforge.gym.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PaymentRequest(
        @NotNull Integer memberId,
        @NotNull @Min(0) Double amount,
        @NotNull LocalDate paymentDate,
        @NotBlank String paymentMethod,
        @NotBlank String status
) {
}


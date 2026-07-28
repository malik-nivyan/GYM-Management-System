package com.titanforge.gym.dto.payment;

import java.time.LocalDate;

public record PaymentResponse(
        Integer paymentId,
        Integer memberId,
        String memberName,
        Double amount,
        LocalDate paymentDate,
        String paymentMethod,
        String status
) {
}


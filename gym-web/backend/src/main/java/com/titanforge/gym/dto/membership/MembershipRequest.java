package com.titanforge.gym.dto.membership;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MembershipRequest(
        @NotBlank String name,
        @NotNull @Min(1) Integer durationMonths,
        @NotNull @Min(0) Double fee
) {
}


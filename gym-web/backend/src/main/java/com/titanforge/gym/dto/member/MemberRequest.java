package com.titanforge.gym.dto.member;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record MemberRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String gender,
        @NotNull @Min(1) Integer age,
        @NotBlank String phone,
        @Email @NotBlank String email,
        @NotNull LocalDate joinDate,
        @NotNull Integer trainerId,
        @NotBlank String status,
        @NotNull Integer membershipId
) {
}


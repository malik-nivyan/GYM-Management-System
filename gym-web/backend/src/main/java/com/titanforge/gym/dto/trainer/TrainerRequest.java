package com.titanforge.gym.dto.trainer;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record TrainerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String gender,
        @NotNull @Min(1) Integer age,
        @NotBlank String phone,
        @Email @NotBlank String email,
        @NotBlank String specialization,
        @NotNull LocalDate hireDate
) {
}


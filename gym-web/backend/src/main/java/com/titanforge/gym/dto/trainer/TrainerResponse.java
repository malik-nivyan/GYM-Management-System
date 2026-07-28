package com.titanforge.gym.dto.trainer;

import java.time.LocalDate;

public record TrainerResponse(
        Integer trainerId,
        String firstName,
        String lastName,
        String gender,
        Integer age,
        String phone,
        String email,
        String specialization,
        LocalDate hireDate
) {
}


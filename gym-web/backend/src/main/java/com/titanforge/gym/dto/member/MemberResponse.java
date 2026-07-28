package com.titanforge.gym.dto.member;

import java.time.LocalDate;

public record MemberResponse(
        Integer memberId,
        String firstName,
        String lastName,
        String gender,
        Integer age,
        String phone,
        String email,
        LocalDate joinDate,
        Integer trainerId,
        String trainerName,
        String status,
        Integer membershipId,
        String membershipName
) {
}


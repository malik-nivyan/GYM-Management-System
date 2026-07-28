package com.titanforge.gym.dto.membership;

public record MembershipResponse(
        Integer membershipId,
        String name,
        Integer durationMonths,
        Double fee
) {
}


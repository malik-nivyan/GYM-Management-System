package com.titanforge.gym.repository;

import com.titanforge.gym.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Integer> {
}


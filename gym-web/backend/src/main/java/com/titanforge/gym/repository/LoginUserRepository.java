package com.titanforge.gym.repository;

import com.titanforge.gym.model.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginUserRepository extends JpaRepository<LoginUser, String> {
}


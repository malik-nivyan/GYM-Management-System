package com.titanforge.gym.service;

import com.titanforge.gym.dto.auth.AuthRequest;
import com.titanforge.gym.dto.auth.AuthResponse;
import com.titanforge.gym.model.LoginUser;
import com.titanforge.gym.repository.LoginUserRepository;
import com.titanforge.gym.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final LoginUserRepository loginUserRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       LoginUserRepository loginUserRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.loginUserRepository = loginUserRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        LoginUser user = loginUserRepository.findById(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        String token = jwtService.generateToken(principal, user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}


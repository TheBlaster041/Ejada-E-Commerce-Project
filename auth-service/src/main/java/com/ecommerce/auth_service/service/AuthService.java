package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.AuthRequest;
import com.ecommerce.auth_service.dto.RegisterRequest;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.util.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole("USER");

        userRepository.save(user);

        return "Registration successful";
    }

    public String login(AuthRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid username or password");
        }

        return jwtUtil.generateToken(
                user.getUsername(),
                user.getRole()
        );
    }
}
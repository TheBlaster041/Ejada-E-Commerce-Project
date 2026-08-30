package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.dto.AuthRequest;
import com.ecommerce.auth_service.dto.RegisterRequest;
import com.ecommerce.auth_service.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) 
    {
    	return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }
}
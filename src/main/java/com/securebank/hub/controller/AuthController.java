package com.securebank.hub.controller;

import com.securebank.hub.dto.AuthResponse;
import com.securebank.hub.dto.LoginRequest;
import com.securebank.hub.exception.UnauthorizedException;
import com.securebank.hub.model.User;
import com.securebank.hub.security.JwtUtil;
import com.securebank.hub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody User user) {
        // Validation errors are handled by GlobalExceptionHandler via @Valid
        User savedUser = userService.registerUser(user);
        String token = jwtUtil.generateToken(savedUser.getUsername());
        AuthResponse response = new AuthResponse(token, savedUser.getUsername(), savedUser.getRole().name());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    if (userService.validatePassword(loginRequest.getPassword(), user.getPassword())) {
                        String token = jwtUtil.generateToken(user.getUsername());
                        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole().name());
                        return ResponseEntity.ok(response);
                    } else {
                        throw new UnauthorizedException("Invalid username or password");
                    }
                })
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
    }
}


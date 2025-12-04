package com.securebank.hub.controller;

import com.securebank.hub.dto.AuthResponse;
import com.securebank.hub.dto.LoginRequest;
import com.securebank.hub.model.User;
import com.securebank.hub.security.JwtUtil;
import com.securebank.hub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            String token = jwtUtil.generateToken(savedUser.getUsername());
            AuthResponse response = new AuthResponse(token, savedUser.getUsername(), savedUser.getRole().name());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    if (userService.validatePassword(loginRequest.getPassword(), user.getPassword())) {
                        String token = jwtUtil.generateToken(user.getUsername());
                        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole().name());
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("{\"error\": \"Invalid username or password\"}");
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid username or password\"}"));
    }
}


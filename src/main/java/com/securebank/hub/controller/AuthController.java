package com.securebank.hub.controller;

import com.securebank.hub.dto.AuthResponse;
import com.securebank.hub.dto.LoginRequest;
import com.securebank.hub.dto.RefreshTokenRequest;
import com.securebank.hub.exception.UnauthorizedException;
import com.securebank.hub.model.User;
import com.securebank.hub.security.JwtUtil;
import com.securebank.hub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody User user) {
        // Validation errors are handled by GlobalExceptionHandler via @Valid
        User savedUser = userService.registerUser(user);
        String accessToken = jwtUtil.generateToken(savedUser.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUsername());
        AuthResponse response = new AuthResponse(accessToken, refreshToken, savedUser.getUsername(), savedUser.getRole().name());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    if (userService.validatePassword(loginRequest.getPassword(), user.getPassword())) {
                        String accessToken = jwtUtil.generateToken(user.getUsername());
                        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
                        AuthResponse response = new AuthResponse(accessToken, refreshToken, user.getUsername(), user.getRole().name());
                        return ResponseEntity.ok(response);
                    } else {
                        throw new UnauthorizedException("Invalid username or password");
                    }
                })
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            // Validate refresh token and extract username
            String username = jwtUtil.validateAndExtractUsernameFromRefreshToken(refreshTokenRequest.getRefreshToken());
            
            // Verify user still exists
            return userService.findByUsername(username)
                    .map(user -> {
                        // Generate new access token
                        String newAccessToken = jwtUtil.generateToken(user.getUsername());
                        // Optionally generate new refresh token (rotate refresh token)
                        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());
                        AuthResponse response = new AuthResponse(newAccessToken, newRefreshToken, user.getUsername(), user.getRole().name());
                        return ResponseEntity.ok(response);
                    })
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid refresh token");
        }
    }
}


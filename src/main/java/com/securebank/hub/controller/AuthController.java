package com.securebank.hub.controller;

import com.securebank.hub.dto.AuthResponse;
import com.securebank.hub.dto.LoginRequest;
import com.securebank.hub.dto.RefreshTokenRequest;
import com.securebank.hub.exception.UnauthorizedException;
import com.securebank.hub.model.User;
import com.securebank.hub.security.JwtUtil;
import com.securebank.hub.service.AuditLogService;
import com.securebank.hub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    
    @Autowired
    private AuditLogService auditLogService;
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody User user, HttpServletRequest request) {
        try {
            // Validation errors are handled by GlobalExceptionHandler via @Valid
            User savedUser = userService.registerUser(user);
            String accessToken = jwtUtil.generateToken(savedUser.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUsername());
            AuthResponse response = new AuthResponse(accessToken, refreshToken, savedUser.getUsername(), savedUser.getRole().name());
            
            // Audit log successful registration
            auditLogService.logUserRegistration(savedUser.getUsername(), savedUser.getEmail(), true, null);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Audit log failed registration
            auditLogService.logUserRegistration(user.getUsername(), user.getEmail(), false, e.getMessage());
            throw e;
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        
        return userService.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    if (userService.validatePassword(loginRequest.getPassword(), user.getPassword())) {
                        String accessToken = jwtUtil.generateToken(user.getUsername());
                        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
                        AuthResponse response = new AuthResponse(accessToken, refreshToken, user.getUsername(), user.getRole().name());
                        
                        // Audit log successful login
                        auditLogService.logAuthenticationAttempt(user.getUsername(), true, null, ipAddress);
                        
                        return ResponseEntity.ok(response);
                    } else {
                        // Audit log failed login
                        auditLogService.logAuthenticationAttempt(loginRequest.getUsername(), false, "Invalid password", ipAddress);
                        throw new UnauthorizedException("Invalid username or password");
                    }
                })
                .orElseThrow(() -> {
                    // Audit log failed login (user not found)
                    auditLogService.logAuthenticationAttempt(loginRequest.getUsername(), false, "User not found", ipAddress);
                    return new UnauthorizedException("Invalid username or password");
                });
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
                        
                        // Audit log successful token refresh
                        auditLogService.logTokenRefresh(user.getUsername(), true, null);
                        
                        return ResponseEntity.ok(response);
                    })
                    .orElseThrow(() -> {
                        auditLogService.logTokenRefresh(username, false, "User not found");
                        return new UnauthorizedException("User not found");
                    });
        } catch (UnauthorizedException e) {
            // Try to extract username for logging
            try {
                String username = jwtUtil.validateAndExtractUsernameFromRefreshToken(refreshTokenRequest.getRefreshToken());
                auditLogService.logTokenRefresh(username, false, e.getMessage());
            } catch (Exception ex) {
                auditLogService.logTokenRefresh("unknown", false, "Invalid refresh token");
            }
            throw e;
        } catch (Exception e) {
            auditLogService.logTokenRefresh("unknown", false, "Invalid refresh token");
            throw new UnauthorizedException("Invalid refresh token");
        }
    }
}


package com.securebank.hub.security;

import com.securebank.hub.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret:SecureBankSecretKeyForJWTTokenGeneration2024SecureBankHub}")
    private String secret;
    
    @Value("${jwt.expiration:900000}") // 15 minutes default
    private Long expiration;
    
    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private Long refreshExpiration;
    
    private SecretKey getSigningKey() {
        // Ensure key is at least 256 bits (32 bytes) for HS256
        String key = secret;
        if (key.length() < 32) {
            key = key + "SecureBankSecretKeyForJWTTokenGeneration2024SecureBankHub".substring(0, 32 - key.length());
        }
        return Keys.hmacShaKeyFor(key.substring(0, 32).getBytes());
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate a refresh token with longer expiration
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Validate refresh token and extract username
     */
    public String validateAndExtractUsernameFromRefreshToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            String type = claims.get("type", String.class);
            if (!"refresh".equals(type)) {
                throw new UnauthorizedException("Invalid token type");
            }
            if (isTokenExpired(refreshToken)) {
                throw new UnauthorizedException("Refresh token expired");
            }
            return claims.getSubject();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid refresh token: " + e.getMessage());
        }
    }
}


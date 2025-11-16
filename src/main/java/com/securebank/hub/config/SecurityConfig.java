package com.securebank.hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Allow all API endpoints
                .requestMatchers("/api/**").permitAll()
                // Allow static resources (React frontend)
                .requestMatchers("/", "/index.html", "/static/**", "/favicon.ico", "/manifest.json", "/robots.txt", "/asset-manifest.json").permitAll()
                // Allow all other requests (for React Router and static files)
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }
}

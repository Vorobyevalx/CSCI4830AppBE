package com.securebank.hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 12 rounds for security
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Allow authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // Allow health check
                .requestMatchers("/api/health").permitAll()
                // Allow all API endpoints (will add JWT protection later)
                .requestMatchers("/api/**").permitAll()
                // Allow static resources (React frontend)
                .requestMatchers("/", "/index.html", "/static/**", "/favicon.ico", "/manifest.json", "/robots.txt", "/asset-manifest.json").permitAll()
                // Allow H2 console for local development
                .requestMatchers("/h2-console/**").permitAll()
                // Allow all other requests (for React Router and static files)
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable())  // Disabled for API development, enable for production
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                // Fix deprecation: use frameOptions with customizer
                .frameOptions(frameOptions -> frameOptions.deny())
                // Enhanced security headers (from day2.md)
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .exposedHeaders("Authorization");
            }
        };
    }
}

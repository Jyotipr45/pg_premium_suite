package com.jash.taskservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Activates Layer 2: Granular Method-Level Authorization Security
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF since tokens are stateless and stored outside browser cookies
            .csrf(csrf -> csrf.disable())
            
            // Layer 1: Strict URL Pattern Matching Gates
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll() // Public login/registration endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // Only accounts with ROLE_ADMIN pass Layer 1
                .requestMatchers("/api/v1/owner/**").hasAnyRole("ADMIN", "OWNER")
                .anyRequest().authenticated() // All other requests must provide a valid verified JWT
            )
            
            // Fixed for Spring Boot 3 / Spring Security 6 fluent API style
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
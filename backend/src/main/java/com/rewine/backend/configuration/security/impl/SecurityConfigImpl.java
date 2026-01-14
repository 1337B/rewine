package com.rewine.backend.configuration.security.impl;

import com.rewine.backend.configuration.security.ISecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration implementation.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigImpl implements ISecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/health",
            "/version",
            "/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/h2-console/**"
    };

    @Override
    public String[] getPublicEndpoints() {
        return PUBLIC_ENDPOINTS;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


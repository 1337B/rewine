package com.rewine.backend.configuration.security.impl;

import com.rewine.backend.configuration.properties.SecurityHeadersProperties;
import com.rewine.backend.configuration.security.ISecurityConfig;
import com.rewine.backend.configuration.security.JwtAuthenticationEntryPoint;
import com.rewine.backend.configuration.security.JwtAuthenticationFilter;
import com.rewine.backend.configuration.web.impl.RateLimitFilterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Objects;

/**
 * Security configuration implementation.
 * Includes comprehensive security headers and rate limiting.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfigImpl implements ISecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/health",
            "/version",
            "/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/actuator/**",
            "/actuator/health/**",
            "/actuator/health/liveness",
            "/actuator/health/readiness",
            "/actuator/info",
            "/wines",
            "/wines/**",
            "/wine-routes/**",
            "/wineries/**",
            "/events",
            "/events/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final SecurityHeadersProperties securityHeadersProperties;
    private final RateLimitFilterImpl rateLimitFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfigImpl(
            @Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            @Lazy UserDetailsService userDetailsService,
            SecurityHeadersProperties securityHeadersProperties,
            @Lazy RateLimitFilterImpl rateLimitFilter,
            CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.securityHeadersProperties = securityHeadersProperties;
        this.rateLimitFilter = rateLimitFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Override
    public String[] getPublicEndpoints() {
        return PUBLIC_ENDPOINTS;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> configureSecurityHeaders(headers));

        return http.build();
    }

    /**
     * Configure comprehensive security headers.
     */
    private void configureSecurityHeaders(HeadersConfigurer<HttpSecurity> headers) {
        if (!securityHeadersProperties.isEnabled()) {
            return;
        }

        // X-Content-Type-Options: nosniff (enabled by default, just configure it)
        if (securityHeadersProperties.isContentTypeOptions()) {
            headers.contentTypeOptions(contentTypeOptions -> contentTypeOptions.disable());
            headers.contentTypeOptions(contentTypeOptions -> { });
        }

        // X-Frame-Options
        switch (securityHeadersProperties.getFrameOptions()) {
            case DENY -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
            case SAMEORIGIN -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
            default -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
        }

        // Referrer-Policy
        headers.referrerPolicy(referrer -> {
            String policy = securityHeadersProperties.getReferrerPolicy();
            ReferrerPolicyHeaderWriter.ReferrerPolicy referrerPolicy = mapReferrerPolicy(policy);
            referrer.policy(referrerPolicy);
        });

        // Content-Security-Policy
        String csp = securityHeadersProperties.getContentSecurityPolicy();
        if (Objects.nonNull(csp) && !csp.isEmpty()) {
            headers.contentSecurityPolicy(contentSecurity ->
                    contentSecurity.policyDirectives(csp));
        }

        // Permissions-Policy
        String permissionsPolicy = securityHeadersProperties.getPermissionsPolicy();
        if (Objects.nonNull(permissionsPolicy) && !permissionsPolicy.isEmpty()) {
            headers.permissionsPolicy(permissions ->
                    permissions.policy(permissionsPolicy));
        }

        // X-XSS-Protection (deprecated but still useful for older browsers)
        if (securityHeadersProperties.isXssProtection()) {
            headers.xssProtection(xss ->
                    xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));
        }

        // HSTS (HTTP Strict Transport Security) - only in production
        SecurityHeadersProperties.HstsConfig hstsConfig = securityHeadersProperties.getHsts();
        if (hstsConfig.isEnabled()) {
            headers.httpStrictTransportSecurity(hsts -> {
                hsts.maxAgeInSeconds(hstsConfig.getMaxAgeSeconds());
                hsts.includeSubDomains(hstsConfig.isIncludeSubdomains());
                hsts.preload(hstsConfig.isPreload());
            });
        } else {
            headers.httpStrictTransportSecurity(hsts -> hsts.disable());
        }

        // Cache-Control for API responses
        headers.cacheControl(cache -> { });
    }

    private ReferrerPolicyHeaderWriter.ReferrerPolicy mapReferrerPolicy(String policy) {
        return switch (policy.toLowerCase()) {
            case "no-referrer" -> ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER;
            case "no-referrer-when-downgrade" ->
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER_WHEN_DOWNGRADE;
            case "same-origin" -> ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN;
            case "origin" -> ReferrerPolicyHeaderWriter.ReferrerPolicy.ORIGIN;
            case "strict-origin" -> ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN;
            case "origin-when-cross-origin" ->
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.ORIGIN_WHEN_CROSS_ORIGIN;
            case "strict-origin-when-cross-origin" ->
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN;
            case "unsafe-url" -> ReferrerPolicyHeaderWriter.ReferrerPolicy.UNSAFE_URL;
            default -> ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN;
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


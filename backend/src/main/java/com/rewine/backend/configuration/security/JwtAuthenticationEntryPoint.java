package com.rewine.backend.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewine.backend.dto.common.ApiErrorResponse;
import com.rewine.backend.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

/**
 * Custom authentication entry point for handling unauthorized requests.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        LOGGER.warn("Unauthorized access attempt to: {} - {}",
                request.getRequestURI(),
                authException.getMessage());

        String requestId = MDC.get("requestId");
        if (Objects.isNull(requestId)) {
            requestId = java.util.UUID.randomUUID().toString();
        }

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                request.getRequestURI(),
                requestId,
                HttpStatus.UNAUTHORIZED.value(),
                ErrorCode.UNAUTHORIZED.getCode(),
                "Authentication required. Please provide a valid token.",
                null
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}


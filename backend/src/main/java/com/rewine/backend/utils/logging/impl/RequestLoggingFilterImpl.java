package com.rewine.backend.utils.logging.impl;

import com.rewine.backend.utils.logging.IRequestLoggingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of request logging filter.
 * Provides request correlation through MDC and logs request/response details.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilterImpl extends OncePerRequestFilter implements IRequestLoggingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilterImpl.class);

    private static final int NANOS_TO_MILLIS = 1_000_000;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.nanoTime();

        try {
            // Setup MDC context
            setupMdc(request, response);

            // Log request start
            logRequestStart(request);

            // Continue with filter chain
            filterChain.doFilter(request, response);

        } finally {
            // Calculate elapsed time
            long elapsedTime = (System.nanoTime() - startTime) / NANOS_TO_MILLIS;

            // Add response details to MDC
            MDC.put(MDC_STATUS_CODE, String.valueOf(response.getStatus()));
            MDC.put(MDC_ELAPSED_TIME, String.valueOf(elapsedTime));

            // Log request completion
            logRequestCompletion(request, response, elapsedTime);

            // Clear MDC to prevent memory leaks
            clearMdc();
        }
    }

    /**
     * Sets up MDC context with request information.
     */
    private void setupMdc(HttpServletRequest request, HttpServletResponse response) {
        // Get or generate request ID
        String requestId = extractOrGenerateRequestId(request);
        MDC.put(MDC_REQUEST_ID, requestId);

        // Add request ID to response header
        response.setHeader(REQUEST_ID_HEADER, requestId);

        // Add request path and method
        MDC.put(MDC_PATH, request.getRequestURI());
        MDC.put(MDC_METHOD, request.getMethod());

        // Add user ID if authenticated
        String userId = extractUserId();
        if (Objects.nonNull(userId)) {
            MDC.put(MDC_USER_ID, userId);
        }
    }

    /**
     * Extracts request ID from header or generates a new one.
     */
    private String extractOrGenerateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (Objects.isNull(requestId) || requestId.isBlank()) {
            requestId = generateRequestId();
        }
        return requestId;
    }

    /**
     * Extracts user ID from security context if available.
     */
    private String extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Logs the start of a request.
     */
    private void logRequestStart(HttpServletRequest request) {
        if (shouldLog(request)) {
            String queryString = request.getQueryString();
            String fullPath = Objects.nonNull(queryString)
                    ? request.getRequestURI() + "?" + queryString
                    : request.getRequestURI();

            LOGGER.info("Request started: {} {} from {}",
                    request.getMethod(),
                    fullPath,
                    getClientIp(request));
        }
    }

    /**
     * Logs the completion of a request.
     */
    private void logRequestCompletion(HttpServletRequest request, HttpServletResponse response, long elapsedTime) {
        if (shouldLog(request)) {
            int status = response.getStatus();
            String logLevel = determineLogLevel(status);

            switch (logLevel) {
                case "ERROR" -> LOGGER.error("Request completed: {} {} - {} ({} ms)",
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        elapsedTime);
                case "WARN" -> LOGGER.warn("Request completed: {} {} - {} ({} ms)",
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        elapsedTime);
                default -> LOGGER.info("Request completed: {} {} - {} ({} ms)",
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        elapsedTime);
            }
        }
    }

    /**
     * Determines log level based on HTTP status code.
     */
    private String determineLogLevel(int status) {
        if (status >= 500) {
            return "ERROR";
        } else if (status >= 400) {
            return "WARN";
        }
        return "INFO";
    }

    /**
     * Checks if the request should be logged.
     * Excludes health checks, actuator, and static resources.
     */
    private boolean shouldLog(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/actuator")
                && !path.startsWith("/health")
                && !path.equals("/favicon.ico")
                && !path.startsWith("/swagger-ui")
                && !path.startsWith("/v3/api-docs")
                && !path.startsWith("/webjars");
    }

    /**
     * Gets the client IP address, considering proxy headers.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (Objects.nonNull(xForwardedFor) && !xForwardedFor.isBlank()) {
            // Return the first IP in the chain
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (Objects.nonNull(xRealIp) && !xRealIp.isBlank()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Clears all MDC values.
     */
    private void clearMdc() {
        MDC.remove(MDC_REQUEST_ID);
        MDC.remove(MDC_USER_ID);
        MDC.remove(MDC_PATH);
        MDC.remove(MDC_METHOD);
        MDC.remove(MDC_STATUS_CODE);
        MDC.remove(MDC_ELAPSED_TIME);
    }

    @Override
    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}


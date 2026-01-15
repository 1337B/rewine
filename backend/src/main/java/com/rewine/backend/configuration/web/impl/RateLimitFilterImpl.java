package com.rewine.backend.configuration.web.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rewine.backend.configuration.properties.RateLimitProperties;
import com.rewine.backend.configuration.web.IRateLimitFilter;
import com.rewine.backend.dto.common.ApiErrorResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Rate limiting filter implementation using Bucket4j.
 * Applies different rate limits based on endpoint category and client IP.
 */
@Slf4j
@Component
public class RateLimitFilterImpl extends OncePerRequestFilter implements IRateLimitFilter {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_REAL_IP = "X-Real-IP";

    private final RateLimitProperties rateLimitProperties;
    private final ObjectMapper objectMapper;

    // Separate caches for each rate limit category
    private final Cache<String, Bucket> loginBuckets;
    private final Cache<String, Bucket> registerBuckets;
    private final Cache<String, Bucket> publicGetBuckets;
    private final Cache<String, Bucket> authenticatedBuckets;

    public RateLimitFilterImpl(RateLimitProperties rateLimitProperties, ObjectMapper objectMapper) {
        this.rateLimitProperties = rateLimitProperties;
        this.objectMapper = objectMapper;

        // Initialize caches for each category
        this.loginBuckets = buildCache();
        this.registerBuckets = buildCache();
        this.publicGetBuckets = buildCache();
        this.authenticatedBuckets = buildCache();

        log.info("Rate limit filter initialized - enabled: {}, login: {}/{}, register: {}/{}, publicGet: {}/{}",
                rateLimitProperties.isEnabled(),
                rateLimitProperties.getLogin().getRequests(),
                rateLimitProperties.getLogin().getWindowSeconds(),
                rateLimitProperties.getRegister().getRequests(),
                rateLimitProperties.getRegister().getWindowSeconds(),
                rateLimitProperties.getPublicGet().getRequests(),
                rateLimitProperties.getPublicGet().getWindowSeconds());
    }

    private Cache<String, Bucket> buildCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(rateLimitProperties.getCacheExpirationSeconds(), TimeUnit.SECONDS)
                .maximumSize(rateLimitProperties.getMaxCacheSize())
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!rateLimitProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitCategory category = getRateLimitCategory(request);

        if (category == RateLimitCategory.NONE) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = getClientIdentifier(request);
        Bucket bucket = getBucket(clientId, category);
        RateLimitProperties.EndpointLimit limits = getLimits(category);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // Add rate limit headers to response
        addRateLimitHeaders(response, probe, limits);

        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
        } else {
            handleRateLimitExceeded(request, response, probe, clientId, category);
        }
    }

    @Override
    public RateLimitCategory getRateLimitCategory(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip rate limiting for actuator and swagger
        if (path.contains("/actuator") || path.contains("/swagger") || path.contains("/api-docs")) {
            return RateLimitCategory.NONE;
        }

        // Login endpoint - strict rate limiting
        if (path.endsWith("/auth/login") && "POST".equalsIgnoreCase(method)) {
            return RateLimitCategory.LOGIN;
        }

        // Register endpoint - very strict rate limiting
        if (path.endsWith("/auth/register") && "POST".equalsIgnoreCase(method)) {
            return RateLimitCategory.REGISTER;
        }

        // Public GET endpoints (wines, wine-routes, events, etc.)
        if ("GET".equalsIgnoreCase(method) && isPublicGetEndpoint(path)) {
            return RateLimitCategory.PUBLIC_GET;
        }

        // All other authenticated endpoints
        return RateLimitCategory.AUTHENTICATED;
    }

    private boolean isPublicGetEndpoint(String path) {
        return path.contains("/wines")
                || path.contains("/wine-routes")
                || path.contains("/wineries")
                || path.contains("/events")
                || path.contains("/health")
                || path.contains("/version");
    }

    @Override
    public String getClientIdentifier(HttpServletRequest request) {
        // Check X-Forwarded-For first (for requests behind proxy/load balancer)
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (Objects.nonNull(xForwardedFor) && !xForwardedFor.isEmpty()) {
            // Take the first IP in the chain (original client)
            return xForwardedFor.split(",")[0].trim();
        }

        // Check X-Real-IP
        String xRealIp = request.getHeader(X_REAL_IP);
        if (Objects.nonNull(xRealIp) && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // Fall back to remote address
        return request.getRemoteAddr();
    }

    @Override
    public boolean isEnabled() {
        return rateLimitProperties.isEnabled();
    }

    private Bucket getBucket(String clientId, RateLimitCategory category) {
        Cache<String, Bucket> cache = getCacheForCategory(category);
        RateLimitProperties.EndpointLimit limits = getLimits(category);

        return cache.get(clientId, key -> createBucket(limits));
    }

    private Cache<String, Bucket> getCacheForCategory(RateLimitCategory category) {
        return switch (category) {
            case LOGIN -> loginBuckets;
            case REGISTER -> registerBuckets;
            case PUBLIC_GET -> publicGetBuckets;
            case AUTHENTICATED -> authenticatedBuckets;
            case NONE -> throw new IllegalArgumentException("NONE category should not use bucket");
        };
    }

    private RateLimitProperties.EndpointLimit getLimits(RateLimitCategory category) {
        return switch (category) {
            case LOGIN -> rateLimitProperties.getLogin();
            case REGISTER -> rateLimitProperties.getRegister();
            case PUBLIC_GET -> rateLimitProperties.getPublicGet();
            case AUTHENTICATED -> rateLimitProperties.getAuthenticated();
            case NONE -> throw new IllegalArgumentException("NONE category has no limits");
        };
    }

    private Bucket createBucket(RateLimitProperties.EndpointLimit limits) {
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(limits.getRequests())
                .refillGreedy(limits.getRequests(), Duration.ofSeconds(limits.getWindowSeconds()))
                .build();

        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }

    private void addRateLimitHeaders(HttpServletResponse response,
                                     ConsumptionProbe probe,
                                     RateLimitProperties.EndpointLimit limits) {
        response.addHeader(HEADER_RATE_LIMIT_LIMIT, String.valueOf(limits.getRequests()));
        response.addHeader(HEADER_RATE_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));

        // Calculate reset time
        long resetTimeSeconds = Instant.now().plusNanos(probe.getNanosToWaitForRefill()).getEpochSecond();
        response.addHeader(HEADER_RATE_LIMIT_RESET, String.valueOf(resetTimeSeconds));
    }

    private void handleRateLimitExceeded(HttpServletRequest request,
                                         HttpServletResponse response,
                                         ConsumptionProbe probe,
                                         String clientId,
                                         RateLimitCategory category) throws IOException {
        long retryAfterSeconds = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());

        log.warn("Rate limit exceeded - IP: {}, category: {}, path: {}, method: {}, retryAfter: {}s",
                clientId, category, request.getRequestURI(), request.getMethod(), retryAfterSeconds);

        response.addHeader(HEADER_RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String requestId = request.getHeader("X-Request-Id");
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .requestId(requestId)
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .code("RATE_LIMIT_EXCEEDED")
                .message(String.format("Too many requests. Please retry after %d seconds.", retryAfterSeconds))
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}


package com.rewine.backend.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Rate limiting configuration properties.
 * Configures request rate limits per IP address for different endpoint categories.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "rewine.rate-limit")
public class RateLimitProperties {

    // Default constants for rate limiting
    private static final int DEFAULT_LOGIN_REQUESTS = 10;
    private static final int DEFAULT_REGISTER_REQUESTS = 5;
    private static final int DEFAULT_PUBLIC_GET_REQUESTS = 120;
    private static final int DEFAULT_AUTHENTICATED_REQUESTS = 200;
    private static final int DEFAULT_WINDOW_SECONDS = 60;
    private static final int DEFAULT_CACHE_EXPIRATION_SECONDS = 300;
    private static final int DEFAULT_MAX_CACHE_SIZE = 10000;

    /**
     * Whether rate limiting is enabled.
     */
    private boolean enabled = true;

    /**
     * Rate limit configuration for login endpoint.
     */
    private EndpointLimit login = new EndpointLimit(DEFAULT_LOGIN_REQUESTS, DEFAULT_WINDOW_SECONDS);

    /**
     * Rate limit configuration for register endpoint.
     */
    private EndpointLimit register = new EndpointLimit(DEFAULT_REGISTER_REQUESTS, DEFAULT_WINDOW_SECONDS);

    /**
     * Rate limit configuration for general public GET endpoints.
     */
    private EndpointLimit publicGet = new EndpointLimit(DEFAULT_PUBLIC_GET_REQUESTS, DEFAULT_WINDOW_SECONDS);

    /**
     * Rate limit configuration for authenticated endpoints.
     */
    private EndpointLimit authenticated = new EndpointLimit(DEFAULT_AUTHENTICATED_REQUESTS, DEFAULT_WINDOW_SECONDS);

    /**
     * Cache expiration time in seconds for bucket entries.
     * After this time, unused rate limit buckets are evicted.
     */
    private int cacheExpirationSeconds = DEFAULT_CACHE_EXPIRATION_SECONDS;

    /**
     * Maximum number of cached IP addresses.
     */
    private int maxCacheSize = DEFAULT_MAX_CACHE_SIZE;

    /**
     * Configuration for a single endpoint rate limit.
     */
    @Data
    public static class EndpointLimit {

        private static final int ENDPOINT_DEFAULT_REQUESTS = 60;
        private static final int ENDPOINT_DEFAULT_WINDOW = 60;

        /**
         * Maximum number of requests allowed in the time window.
         */
        private int requests;

        /**
         * Time window in seconds.
         */
        private int windowSeconds;

        public EndpointLimit() {
            this.requests = ENDPOINT_DEFAULT_REQUESTS;
            this.windowSeconds = ENDPOINT_DEFAULT_WINDOW;
        }

        public EndpointLimit(int requests, int windowSeconds) {
            this.requests = requests;
            this.windowSeconds = windowSeconds;
        }
    }
}


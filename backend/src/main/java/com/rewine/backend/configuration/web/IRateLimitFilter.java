package com.rewine.backend.configuration.web;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface for rate limiting filter.
 * Defines rate limiting behavior for different endpoint categories.
 */
public interface IRateLimitFilter {

    /**
     * Rate limit response header for remaining requests.
     */
    String HEADER_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";

    /**
     * Rate limit response header for total limit.
     */
    String HEADER_RATE_LIMIT_LIMIT = "X-RateLimit-Limit";

    /**
     * Rate limit response header for reset time (Unix timestamp).
     */
    String HEADER_RATE_LIMIT_RESET = "X-RateLimit-Reset";

    /**
     * Rate limit response header for retry-after (seconds).
     */
    String HEADER_RETRY_AFTER = "Retry-After";

    /**
     * Determine the rate limit category for a request.
     *
     * @param request The HTTP request
     * @return The rate limit category
     */
    RateLimitCategory getRateLimitCategory(HttpServletRequest request);

    /**
     * Get the client identifier for rate limiting (typically IP address).
     *
     * @param request The HTTP request
     * @return The client identifier
     */
    String getClientIdentifier(HttpServletRequest request);

    /**
     * Check if rate limiting is enabled.
     *
     * @return true if enabled
     */
    boolean isEnabled();

    /**
     * Rate limit categories for different endpoint types.
     */
    enum RateLimitCategory {
        /**
         * Login endpoint - strict limit.
         */
        LOGIN,

        /**
         * Register endpoint - very strict limit.
         */
        REGISTER,

        /**
         * Public GET endpoints - moderate limit.
         */
        PUBLIC_GET,

        /**
         * Authenticated endpoints - higher limit.
         */
        AUTHENTICATED,

        /**
         * No rate limiting.
         */
        NONE
    }
}


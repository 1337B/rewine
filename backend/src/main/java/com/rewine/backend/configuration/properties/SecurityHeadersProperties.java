package com.rewine.backend.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Security headers configuration properties.
 * Configures HTTP security headers per environment.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "rewine.security.headers")
public class SecurityHeadersProperties {

    /**
     * Whether security headers are enabled.
     */
    private boolean enabled = true;

    /**
     * X-Content-Type-Options configuration.
     */
    private boolean contentTypeOptions = true;

    /**
     * X-Frame-Options configuration.
     */
    private FrameOptions frameOptions = FrameOptions.DENY;

    /**
     * Referrer-Policy header value.
     */
    private String referrerPolicy = "strict-origin-when-cross-origin";

    /**
     * Content-Security-Policy header value.
     * For API-only backends, a strict policy is recommended.
     */
    private String contentSecurityPolicy = "default-src 'none'; frame-ancestors 'none'";

    /**
     * Permissions-Policy (formerly Feature-Policy) header value.
     */
    private String permissionsPolicy = "geolocation=(), camera=(), microphone=()";

    /**
     * HSTS (HTTP Strict Transport Security) configuration.
     */
    private HstsConfig hsts = new HstsConfig();

    /**
     * X-XSS-Protection header (deprecated but still useful for older browsers).
     */
    private boolean xssProtection = true;

    /**
     * Frame options enum.
     */
    public enum FrameOptions {
        DENY,
        SAMEORIGIN
    }

    /**
     * HSTS configuration.
     */
    @Data
    public static class HstsConfig {

        private static final long DEFAULT_MAX_AGE_ONE_YEAR = 31536000L;

        /**
         * Whether HSTS is enabled.
         * Should only be enabled in production with HTTPS.
         */
        private boolean enabled = false;

        /**
         * Max-age in seconds.
         * Default: 1 year (31536000 seconds).
         */
        private long maxAgeSeconds = DEFAULT_MAX_AGE_ONE_YEAR;

        /**
         * Whether to include subdomains.
         */
        private boolean includeSubdomains = true;

        /**
         * Whether to add preload directive.
         */
        private boolean preload = false;
    }
}


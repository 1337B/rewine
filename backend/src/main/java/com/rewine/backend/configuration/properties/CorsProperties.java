package com.rewine.backend.configuration.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * CORS configuration properties.
 * Configures Cross-Origin Resource Sharing settings per environment.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "rewine.cors")
public class CorsProperties {

    private static final long DEFAULT_MAX_AGE_SECONDS = 3600L;

    /**
     * Whether CORS is enabled.
     */
    private boolean enabled = true;

    /**
     * List of allowed origins.
     * In production, this should NOT contain wildcards.
     */
    @NotEmpty(message = "At least one allowed origin must be configured")
    private List<String> allowedOrigins = List.of("http://localhost:3000");

    /**
     * List of allowed HTTP methods.
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");

    /**
     * List of allowed headers.
     */
    private List<String> allowedHeaders = List.of(
            "Authorization",
            "Content-Type",
            "X-Request-Id",
            "Accept",
            "Origin",
            "X-Requested-With"
    );

    /**
     * List of headers exposed to the client.
     */
    private List<String> exposedHeaders = List.of(
            "X-Request-Id",
            "X-RateLimit-Remaining",
            "X-RateLimit-Limit",
            "X-RateLimit-Reset"
    );

    /**
     * Whether credentials (cookies, authorization headers) are allowed.
     */
    private boolean allowCredentials = true;

    /**
     * Maximum age in seconds that the results of a preflight request can be cached.
     */
    private long maxAge = DEFAULT_MAX_AGE_SECONDS;

    /**
     * Get allowed origins as array.
     */
    public String[] getAllowedOriginsArray() {
        return allowedOrigins.toArray(new String[0]);
    }

    /**
     * Get allowed methods as array.
     */
    public String[] getAllowedMethodsArray() {
        return allowedMethods.toArray(new String[0]);
    }

    /**
     * Get allowed headers as array.
     */
    public String[] getAllowedHeadersArray() {
        return allowedHeaders.toArray(new String[0]);
    }

    /**
     * Get exposed headers as array.
     */
    public String[] getExposedHeadersArray() {
        return exposedHeaders.toArray(new String[0]);
    }
}


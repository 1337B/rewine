package com.rewine.backend.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.Objects;

/**
 * JWT configuration properties.
 * Loaded from application.yml under 'jwt' prefix.
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Validated
public class JwtProperties {

    // Time conversion constants
    private static final long MILLIS_PER_MINUTE = 60_000L;
    private static final long MILLIS_PER_DAY = 86_400_000L;

    // Default values
    private static final long DEFAULT_ACCESS_TOKEN_EXPIRATION_MS = 15 * MILLIS_PER_MINUTE; // 15 minutes
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRATION_MS = 7 * MILLIS_PER_DAY;   // 7 days

    // Security constants
    private static final int MIN_SECRET_LENGTH = 32;

    /**
     * JWT issuer name.
     */
    @NotBlank
    private String issuer = "rewine-backend";

    /**
     * Secret key for signing JWTs (minimum 256 bits / 32 characters).
     */
    @NotBlank
    private String secret = "rewine-development-secret-key-change-in-production-minimum-256-bits";

    /**
     * Access token time-to-live in milliseconds.
     * Default: 15 minutes
     */
    @Positive
    private long accessTokenExpiration = DEFAULT_ACCESS_TOKEN_EXPIRATION_MS;

    /**
     * Refresh token time-to-live in milliseconds.
     * Default: 7 days
     */
    @Positive
    private long refreshTokenExpiration = DEFAULT_REFRESH_TOKEN_EXPIRATION_MS;

    /**
     * Token type prefix for Authorization header.
     */
    private String tokenPrefix = "Bearer ";

    /**
     * Authorization header name.
     */
    private String headerName = "Authorization";

    // Getters and Setters

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    /**
     * Gets access token expiration in minutes.
     */
    public long getAccessTokenExpirationMinutes() {
        return accessTokenExpiration / MILLIS_PER_MINUTE;
    }

    /**
     * Gets refresh token expiration in days.
     */
    public long getRefreshTokenExpirationDays() {
        return refreshTokenExpiration / MILLIS_PER_DAY;
    }

    /**
     * Validates that the secret is strong enough.
     */
    public boolean isSecretValid() {
        return Objects.nonNull(secret) && secret.length() >= MIN_SECRET_LENGTH;
    }
}


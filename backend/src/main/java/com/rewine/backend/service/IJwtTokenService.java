package com.rewine.backend.service;

import com.rewine.backend.model.entity.UserEntity;

import java.util.Map;
import java.util.Optional;

/**
 * Service interface for JWT token operations.
 */
public interface IJwtTokenService {

    /**
     * Generates an access token for a user.
     *
     * @param user the user entity
     * @return the generated JWT access token
     */
    String generateAccessToken(UserEntity user);

    /**
     * Generates an access token with additional claims.
     *
     * @param user the user entity
     * @param extraClaims additional claims to include
     * @return the generated JWT access token
     */
    String generateAccessToken(UserEntity user, Map<String, Object> extraClaims);

    /**
     * Generates a refresh token.
     *
     * @return the generated refresh token (plain text)
     */
    String generateRefreshToken();

    /**
     * Validates an access token.
     *
     * @param token the JWT token to validate
     * @return true if valid, false otherwise
     */
    boolean validateAccessToken(String token);

    /**
     * Extracts the username (subject) from a token.
     *
     * @param token the JWT token
     * @return optional containing the username if extraction succeeds
     */
    Optional<String> extractUsername(String token);

    /**
     * Extracts the user ID from a token.
     *
     * @param token the JWT token
     * @return optional containing the user ID if extraction succeeds
     */
    Optional<String> extractUserId(String token);

    /**
     * Hashes a refresh token for secure storage.
     *
     * @param plainToken the plain text refresh token
     * @return the hashed token
     */
    String hashRefreshToken(String plainToken);

    /**
     * Verifies a plain refresh token against a stored hash.
     *
     * @param plainToken the plain text token
     * @param hashedToken the stored hash
     * @return true if they match
     */
    boolean verifyRefreshToken(String plainToken, String hashedToken);
}


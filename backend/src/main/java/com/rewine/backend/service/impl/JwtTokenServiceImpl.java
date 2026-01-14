package com.rewine.backend.service.impl;

import com.rewine.backend.configuration.properties.JwtProperties;
import com.rewine.backend.model.entity.UserEntity;
import com.rewine.backend.service.IJwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JWT token service implementation.
 */
@Service
public class JwtTokenServiceImpl implements IJwtTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenServiceImpl.class);
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLES = "roles";
    private static final int REFRESH_TOKEN_BYTES = 32;

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;
    private final SecureRandom secureRandom;

    public JwtTokenServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        String secret = jwtProperties.getSecret();
        if (Objects.isNull(secret) || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set jwt.secret in application properties.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generateAccessToken(UserEntity user) {
        return generateAccessToken(user, new HashMap<>());
    }

    @Override
    public String generateAccessToken(UserEntity user, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put(CLAIM_USER_ID, user.getId().toString());
        claims.put(CLAIM_EMAIL, user.getEmail());
        claims.put(CLAIM_ROLES, user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList()));

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        byte[] randomBytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.debug("JWT token expired: {}", e.getMessage());
        } catch (JwtException e) {
            LOGGER.warn("Invalid JWT token: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<String> extractUsername(String token) {
        return extractClaims(token)
                .map(Claims::getSubject);
    }

    @Override
    public Optional<String> extractUserId(String token) {
        return extractClaims(token)
                .map(claims -> claims.get(CLAIM_USER_ID, String.class));
    }

    @Override
    public String hashRefreshToken(String plainToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    @Override
    public boolean verifyRefreshToken(String plainToken, String hashedToken) {
        if (Objects.isNull(plainToken) || Objects.isNull(hashedToken)) {
            return false;
        }
        String computedHash = hashRefreshToken(plainToken);
        return MessageDigest.isEqual(
                computedHash.getBytes(StandardCharsets.UTF_8),
                hashedToken.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Extracts claims from a JWT token.
     */
    private Optional<Claims> extractClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (Exception e) {
            LOGGER.debug("Failed to extract claims: {}", e.getMessage());
            return Optional.empty();
        }
    }
}


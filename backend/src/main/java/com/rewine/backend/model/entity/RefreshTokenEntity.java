package com.rewine.backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

/**
 * Refresh token entity for managing JWT refresh tokens.
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user", "tokenHash"})
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "device_info", length = 500)
    private String deviceInfo;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_reason", length = 100)
    private String revokedReason;

    @Column(name = "replaced_by_token_hash", length = 255)
    private String replacedByTokenHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    /**
     * Checks if the token is expired.
     *
     * @return true if expired
     */
    public boolean isExpired() {
        return Objects.nonNull(expiresAt) && Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks if the token is revoked.
     *
     * @return true if revoked
     */
    public boolean isRevoked() {
        return Objects.nonNull(revokedAt);
    }

    /**
     * Checks if the token is valid (not expired and not revoked).
     *
     * @return true if valid
     */
    public boolean isValid() {
        return !isExpired() && !isRevoked();
    }

    /**
     * Revokes this token.
     *
     * @param reason the reason for revocation
     */
    public void revoke(String reason) {
        this.revokedAt = Instant.now();
        this.revokedReason = reason;
    }

    /**
     * Revokes this token and marks it as replaced by another token.
     *
     * @param newTokenHash the hash of the new token
     */
    public void revokeAndReplace(String newTokenHash) {
        this.revokedAt = Instant.now();
        this.revokedReason = "Token rotation";
        this.replacedByTokenHash = newTokenHash;
    }

    /**
     * Common revocation reasons.
     */
    public static final String REVOKED_LOGOUT = "User logout";
    public static final String REVOKED_ROTATION = "Token rotation";
    public static final String REVOKED_SECURITY = "Security concern";
    public static final String REVOKED_PASSWORD_CHANGE = "Password changed";
    public static final String REVOKED_ADMIN = "Revoked by admin";
}


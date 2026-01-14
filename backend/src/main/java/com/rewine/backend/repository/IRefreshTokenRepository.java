package com.rewine.backend.repository;

import com.rewine.backend.model.entity.RefreshTokenEntity;
import com.rewine.backend.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RefreshToken entity.
 */
@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /**
     * Finds a refresh token by its hash.
     *
     * @param tokenHash the token hash to search for
     * @return optional containing token if found
     */
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    /**
     * Finds all tokens for a user.
     *
     * @param user the user entity
     * @return list of refresh tokens
     */
    List<RefreshTokenEntity> findByUser(UserEntity user);

    /**
     * Finds all tokens for a user by user ID.
     *
     * @param userId the user ID
     * @return list of refresh tokens
     */
    List<RefreshTokenEntity> findByUserId(UUID userId);

    /**
     * Finds all active (non-revoked, non-expired) tokens for a user.
     *
     * @param userId the user ID
     * @param now the current timestamp
     * @return list of active refresh tokens
     */
    @Query("SELECT rt FROM RefreshTokenEntity rt "
            + "WHERE rt.user.id = :userId "
            + "AND rt.revokedAt IS NULL "
            + "AND rt.expiresAt > :now")
    List<RefreshTokenEntity> findActiveTokensByUserId(
            @Param("userId") UUID userId,
            @Param("now") Instant now);

    /**
     * Finds a valid token by hash (not revoked and not expired).
     *
     * @param tokenHash the token hash
     * @param now the current timestamp
     * @return optional containing token if valid
     */
    @Query("SELECT rt FROM RefreshTokenEntity rt "
            + "WHERE rt.tokenHash = :tokenHash "
            + "AND rt.revokedAt IS NULL "
            + "AND rt.expiresAt > :now")
    Optional<RefreshTokenEntity> findValidTokenByHash(
            @Param("tokenHash") String tokenHash,
            @Param("now") Instant now);

    /**
     * Checks if a token hash exists.
     *
     * @param tokenHash the token hash to check
     * @return true if exists
     */
    boolean existsByTokenHash(String tokenHash);

    /**
     * Revokes all tokens for a user.
     *
     * @param userId the user ID
     * @param reason the revocation reason
     * @param revokedAt the revocation timestamp
     */
    @Modifying
    @Query("UPDATE RefreshTokenEntity rt "
            + "SET rt.revokedAt = :revokedAt, rt.revokedReason = :reason "
            + "WHERE rt.user.id = :userId AND rt.revokedAt IS NULL")
    void revokeAllTokensForUser(
            @Param("userId") UUID userId,
            @Param("reason") String reason,
            @Param("revokedAt") Instant revokedAt);

    /**
     * Revokes a specific token by hash.
     *
     * @param tokenHash the token hash
     * @param reason the revocation reason
     * @param revokedAt the revocation timestamp
     */
    @Modifying
    @Query("UPDATE RefreshTokenEntity rt "
            + "SET rt.revokedAt = :revokedAt, rt.revokedReason = :reason "
            + "WHERE rt.tokenHash = :tokenHash AND rt.revokedAt IS NULL")
    void revokeTokenByHash(
            @Param("tokenHash") String tokenHash,
            @Param("reason") String reason,
            @Param("revokedAt") Instant revokedAt);

    /**
     * Deletes expired tokens older than a certain date.
     *
     * @param expirationThreshold tokens expired before this date will be deleted
     * @return number of deleted tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiresAt < :threshold")
    int deleteExpiredTokens(@Param("threshold") Instant expirationThreshold);

    /**
     * Counts active tokens for a user.
     *
     * @param userId the user ID
     * @param now the current timestamp
     * @return count of active tokens
     */
    @Query("SELECT COUNT(rt) FROM RefreshTokenEntity rt "
            + "WHERE rt.user.id = :userId "
            + "AND rt.revokedAt IS NULL "
            + "AND rt.expiresAt > :now")
    long countActiveTokensByUserId(@Param("userId") UUID userId, @Param("now") Instant now);

    /**
     * Finds tokens by device info for a user.
     *
     * @param userId the user ID
     * @param deviceInfo the device info
     * @return list of matching tokens
     */
    List<RefreshTokenEntity> findByUserIdAndDeviceInfo(UUID userId, String deviceInfo);
}


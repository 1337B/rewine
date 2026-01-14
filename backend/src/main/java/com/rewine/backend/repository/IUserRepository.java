package com.rewine.backend.repository;

import com.rewine.backend.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * Repository for User entity.
 */
@Repository
public interface IUserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return optional containing user if found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return optional containing user if found
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Finds a user by email or username.
     *
     * @param email the email to search for
     * @param username the username to search for
     * @return optional containing user if found
     */
    Optional<UserEntity> findByEmailOrUsername(String email, String username);

    /**
     * Checks if a user exists by email.
     *
     * @param email the email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user exists by username.
     *
     * @param username the username to check
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by email or username.
     *
     * @param email the email to check
     * @param username the username to check
     * @return true if exists
     */
    boolean existsByEmailOrUsername(String email, String username);

    /**
     * Finds all enabled users.
     *
     * @param pageable pagination info
     * @return page of enabled users
     */
    Page<UserEntity> findByEnabledTrue(Pageable pageable);

    /**
     * Finds users by role name.
     *
     * @param roleName the role name to filter by
     * @param pageable pagination info
     * @return page of users with the specified role
     */
    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName")
    Page<UserEntity> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    /**
     * Finds users created after a specific date.
     *
     * @param date the date to filter by
     * @return list of users created after the date
     */
    List<UserEntity> findByCreatedAtAfter(Instant date);

    /**
     * Updates the last login timestamp for a user.
     *
     * @param userId the user ID
     * @param lastLoginAt the login timestamp
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :userId")
    void updateLastLoginAt(@Param("userId") UUID userId, @Param("lastLoginAt") Instant lastLoginAt);

    /**
     * Locks a user account.
     *
     * @param userId the user ID
     * @param lockReason the reason for locking
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.locked = true, u.lockReason = :lockReason WHERE u.id = :userId")
    void lockUser(@Param("userId") UUID userId, @Param("lockReason") String lockReason);

    /**
     * Unlocks a user account.
     *
     * @param userId the user ID
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.locked = false, u.lockReason = null WHERE u.id = :userId")
    void unlockUser(@Param("userId") UUID userId);

    /**
     * Searches users by name, email, or username.
     *
     * @param searchTerm the search term
     * @param pageable pagination info
     * @return page of matching users
     */
    @Query("SELECT u FROM UserEntity u WHERE "
            + "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<UserEntity> search(@Param("searchTerm") String searchTerm, Pageable pageable);
}


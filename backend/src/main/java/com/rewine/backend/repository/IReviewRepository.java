package com.rewine.backend.repository;

import com.rewine.backend.model.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Review entity.
 */
@Repository
public interface IReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    /**
     * Finds reviews by wine ID with pagination.
     *
     * @param wineId   the wine ID
     * @param pageable pagination info
     * @return page of reviews
     */
    Page<ReviewEntity> findByWine_Id(UUID wineId, Pageable pageable);

    /**
     * Finds reviews by user ID with pagination.
     *
     * @param userId   the user ID
     * @param pageable pagination info
     * @return page of reviews
     */
    Page<ReviewEntity> findByUser_Id(UUID userId, Pageable pageable);

    /**
     * Finds a review by wine ID and user ID.
     *
     * @param wineId the wine ID
     * @param userId the user ID
     * @return optional containing the review
     */
    Optional<ReviewEntity> findByWine_IdAndUser_Id(UUID wineId, UUID userId);

    /**
     * Checks if a review exists for a wine by a user.
     *
     * @param wineId the wine ID
     * @param userId the user ID
     * @return true if exists
     */
    boolean existsByWine_IdAndUser_Id(UUID wineId, UUID userId);

    /**
     * Finds reviews by wine ID ordered by creation date (most recent first).
     *
     * @param wineId   the wine ID
     * @param pageable pagination info
     * @return page of recent reviews
     */
    Page<ReviewEntity> findByWine_IdOrderByCreatedAtDesc(UUID wineId, Pageable pageable);

    /**
     * Finds featured reviews by wine ID (ordered by helpful count).
     *
     * @param wineId   the wine ID
     * @param pageable pagination info
     * @return page of featured reviews
     */
    @Query("SELECT r FROM ReviewEntity r WHERE r.wine.id = :wineId ORDER BY r.helpfulCount DESC, r.createdAt DESC")
    Page<ReviewEntity> findFeaturedByWineId(@Param("wineId") UUID wineId, Pageable pageable);

    /**
     * Finds reviews by wine ID with eager loading of user.
     *
     * @param wineId   the wine ID
     * @param pageable pagination info
     * @return page of reviews with user loaded
     */
    @Query("SELECT r FROM ReviewEntity r LEFT JOIN r.user WHERE r.wine.id = :wineId")
    Page<ReviewEntity> findByWineIdWithUser(@Param("wineId") UUID wineId, Pageable pageable);

    /**
     * Calculates the average rating for a wine.
     *
     * @param wineId the wine ID
     * @return average rating or null
     */
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.wine.id = :wineId")
    Double calculateAverageRating(@Param("wineId") UUID wineId);

    /**
     * Counts reviews for a wine.
     *
     * @param wineId the wine ID
     * @return count
     */
    long countByWine_Id(UUID wineId);

    /**
     * Finds review by ID with user eagerly loaded.
     *
     * @param id the review ID
     * @return optional containing the review
     */
    @Query("SELECT r FROM ReviewEntity r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.wine WHERE r.id = :id")
    Optional<ReviewEntity> findByIdWithUserAndWine(@Param("id") UUID id);

    /**
     * Finds top reviews for a wine (highest rated).
     *
     * @param wineId   the wine ID
     * @param pageable pagination info
     * @return list of top reviews
     */
    @Query("SELECT r FROM ReviewEntity r WHERE r.wine.id = :wineId ORDER BY r.rating DESC, r.createdAt DESC")
    List<ReviewEntity> findTopByWineId(@Param("wineId") UUID wineId, Pageable pageable);

    /**
     * Finds reviews by wine IDs (for batch loading).
     *
     * @param wineIds the list of wine IDs
     * @return list of reviews
     */
    @Query("SELECT r FROM ReviewEntity r WHERE r.wine.id IN :wineIds")
    List<ReviewEntity> findByWineIdIn(@Param("wineIds") List<UUID> wineIds);
}


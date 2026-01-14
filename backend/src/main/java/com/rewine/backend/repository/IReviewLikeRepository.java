package com.rewine.backend.repository;

import com.rewine.backend.model.entity.ReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for ReviewLike entity.
 */
@Repository
public interface IReviewLikeRepository extends JpaRepository<ReviewLikeEntity, ReviewLikeEntity.ReviewLikeId> {

    /**
     * Checks if a user has liked a review.
     *
     * @param reviewId the review ID
     * @param userId   the user ID
     * @return true if liked
     */
    boolean existsByIdReviewIdAndIdUserId(UUID reviewId, UUID userId);

    /**
     * Counts likes for a review.
     *
     * @param reviewId the review ID
     * @return like count
     */
    long countByIdReviewId(UUID reviewId);

    /**
     * Finds all likes for a review.
     *
     * @param reviewId the review ID
     * @return list of likes
     */
    List<ReviewLikeEntity> findByIdReviewId(UUID reviewId);

    /**
     * Deletes a like by review ID and user ID.
     *
     * @param reviewId the review ID
     * @param userId   the user ID
     */
    void deleteByIdReviewIdAndIdUserId(UUID reviewId, UUID userId);

    /**
     * Finds review IDs liked by a user.
     *
     * @param userId the user ID
     * @return list of review IDs
     */
    @Query("SELECT rl.id.reviewId FROM ReviewLikeEntity rl WHERE rl.id.userId = :userId")
    List<UUID> findReviewIdsLikedByUser(@Param("userId") UUID userId);
}


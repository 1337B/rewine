package com.rewine.backend.repository;

import com.rewine.backend.model.entity.ReviewCommentEntity;
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
 * Repository for ReviewComment entity.
 */
@Repository
public interface IReviewCommentRepository extends JpaRepository<ReviewCommentEntity, UUID> {

    /**
     * Finds comments by review ID with pagination.
     *
     * @param reviewId the review ID
     * @param pageable pagination info
     * @return page of comments
     */
    Page<ReviewCommentEntity> findByReview_IdOrderByCreatedAtDesc(UUID reviewId, Pageable pageable);

    /**
     * Counts comments for a review.
     *
     * @param reviewId the review ID
     * @return comment count
     */
    long countByReview_Id(UUID reviewId);

    /**
     * Finds a comment by ID with user loaded.
     *
     * @param id the comment ID
     * @return optional containing the comment
     */
    @Query("SELECT c FROM ReviewCommentEntity c LEFT JOIN FETCH c.user WHERE c.id = :id")
    Optional<ReviewCommentEntity> findByIdWithUser(@Param("id") UUID id);

    /**
     * Finds all comments for a review with users loaded.
     *
     * @param reviewId the review ID
     * @return list of comments
     */
    @Query("SELECT c FROM ReviewCommentEntity c LEFT JOIN FETCH c.user WHERE c.review.id = :reviewId ORDER BY c.createdAt DESC")
    List<ReviewCommentEntity> findByReviewIdWithUser(@Param("reviewId") UUID reviewId);

    /**
     * Deletes comments by review ID.
     *
     * @param reviewId the review ID
     */
    void deleteByReview_Id(UUID reviewId);
}


package com.rewine.backend.service;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.CreateReviewRequest;
import com.rewine.backend.dto.response.ReviewResponse;
import com.rewine.backend.model.enums.ReviewFilter;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Interface for review service operations.
 */
public interface IReviewService {

    /**
     * Creates a new review for a wine.
     *
     * @param wineId  the wine ID
     * @param userId  the user ID
     * @param request the review request
     * @return the created review response
     */
    ReviewResponse createReview(UUID wineId, UUID userId, CreateReviewRequest request);

    /**
     * Lists reviews for a wine with filtering.
     *
     * @param wineId   the wine ID
     * @param filter   the filter type
     * @param userId   the current user ID (for MINE filter)
     * @param pageable pagination info
     * @return page of reviews
     */
    PageResponse<ReviewResponse> listReviews(UUID wineId, ReviewFilter filter, UUID userId, Pageable pageable);

    /**
     * Gets a review by ID.
     *
     * @param reviewId the review ID
     * @return the review response
     */
    ReviewResponse getReview(UUID reviewId);

    /**
     * Updates a review.
     *
     * @param reviewId the review ID
     * @param userId   the user ID (must match reviewer)
     * @param request  the update request
     * @return the updated review response
     */
    ReviewResponse updateReview(UUID reviewId, UUID userId, CreateReviewRequest request);

    /**
     * Deletes a review.
     *
     * @param reviewId the review ID
     * @param userId   the user ID (must match reviewer or be admin)
     */
    void deleteReview(UUID reviewId, UUID userId);

    /**
     * Toggles like on a review.
     *
     * @param reviewId the review ID
     * @param userId   the user ID
     * @return true if now liked, false if unliked
     */
    boolean toggleLike(UUID reviewId, UUID userId);

    /**
     * Gets the average rating for a wine.
     *
     * @param wineId the wine ID
     * @return average rating or null
     */
    Double getAverageRating(UUID wineId);

    /**
     * Gets the review count for a wine.
     *
     * @param wineId the wine ID
     * @return review count
     */
    long getReviewCount(UUID wineId);

    /**
     * Checks if a user has reviewed a wine.
     *
     * @param wineId the wine ID
     * @param userId the user ID
     * @return true if reviewed
     */
    boolean hasUserReviewed(UUID wineId, UUID userId);
}


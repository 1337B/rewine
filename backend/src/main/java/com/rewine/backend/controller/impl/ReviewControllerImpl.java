package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IReviewController;
import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.CreateReviewRequest;
import com.rewine.backend.dto.response.ReviewResponse;
import com.rewine.backend.model.enums.ReviewFilter;
import com.rewine.backend.service.IReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

/**
 * Review endpoints implementation.
 */
@RestController
@RequestMapping
@Tag(name = "Reviews", description = "Wine review management endpoints")
public class ReviewControllerImpl implements IReviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewControllerImpl.class);
    private static final int MAX_PAGE_SIZE = 50;

    private final IReviewService reviewService;

    public ReviewControllerImpl(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    @GetMapping("/wines/{wineId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getWineReviews(
            @PathVariable UUID wineId,
            @RequestParam(required = false) ReviewFilter filter,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        LOGGER.info("GET /wines/{}/reviews - filter={}, page={}, size={}", wineId, filter, page, size);

        // Get current user ID if authenticated (for MINE filter and like status)
        UUID userId = null; // Will be populated via SecurityContext if needed

        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        PageResponse<ReviewResponse> response = reviewService.listReviews(
                wineId,
                filter,
                userId,
                PageRequest.of(page, pageSize)
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Gets reviews with authentication context.
     */
    @GetMapping("/wines/{wineId}/reviews/auth")
    public ResponseEntity<PageResponse<ReviewResponse>> getWineReviewsAuthenticated(
            @PathVariable UUID wineId,
            @RequestParam(required = false) ReviewFilter filter,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        LOGGER.info("GET /wines/{}/reviews/auth - filter={}", wineId, filter);

        UUID userId = extractUserId(userDetails);
        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        PageResponse<ReviewResponse> response = reviewService.listReviews(
                wineId,
                filter,
                userId,
                PageRequest.of(page, pageSize)
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/wines/{wineId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable UUID wineId,
            @Valid @RequestBody CreateReviewRequest request) {

        LOGGER.info("POST /wines/{}/reviews - Creating review", wineId);

        // Get authenticated user - this endpoint requires auth via security config
        UUID userId = getCurrentUserId();

        ReviewResponse response = reviewService.createReview(wineId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Creates a review with explicit authentication.
     */
    @PostMapping("/wines/{wineId}/reviews/auth")
    public ResponseEntity<ReviewResponse> createReviewAuthenticated(
            @PathVariable UUID wineId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        LOGGER.info("POST /wines/{}/reviews/auth - Creating review", wineId);

        UUID userId = extractUserId(userDetails);
        if (Objects.isNull(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ReviewResponse response = reviewService.createReview(wineId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody CreateReviewRequest request) {

        LOGGER.info("PUT /reviews/{} - Updating review", reviewId);

        UUID userId = getCurrentUserId();

        ReviewResponse response = reviewService.updateReview(reviewId, userId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Updates a review with explicit authentication.
     */
    @PutMapping("/reviews/{reviewId}/auth")
    public ResponseEntity<ReviewResponse> updateReviewAuthenticated(
            @PathVariable UUID reviewId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        LOGGER.info("PUT /reviews/{}/auth - Updating review", reviewId);

        UUID userId = extractUserId(userDetails);
        if (Objects.isNull(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ReviewResponse response = reviewService.updateReview(reviewId, userId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        LOGGER.info("DELETE /reviews/{} - Deleting review", reviewId);

        UUID userId = getCurrentUserId();

        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a review with explicit authentication.
     */
    @DeleteMapping("/reviews/{reviewId}/auth")
    public ResponseEntity<Void> deleteReviewAuthenticated(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        LOGGER.info("DELETE /reviews/{}/auth - Deleting review", reviewId);

        UUID userId = extractUserId(userDetails);
        if (Objects.isNull(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable UUID reviewId) {
        LOGGER.info("POST /reviews/{}/like - Toggling like", reviewId);

        UUID userId = getCurrentUserId();

        boolean liked = reviewService.toggleLike(reviewId, userId);

        return ResponseEntity.ok(liked);
    }

    /**
     * Toggles like with explicit authentication.
     */
    @PostMapping("/reviews/{reviewId}/like/auth")
    public ResponseEntity<Boolean> toggleLikeAuthenticated(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        LOGGER.info("POST /reviews/{}/like/auth - Toggling like", reviewId);

        UUID userId = extractUserId(userDetails);
        if (Objects.isNull(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean liked = reviewService.toggleLike(reviewId, userId);

        return ResponseEntity.ok(liked);
    }

    /**
     * Gets the current user ID from security context.
     * Returns a placeholder for now - should be implemented with proper security integration.
     */
    private UUID getCurrentUserId() {
        // This would typically come from SecurityContextHolder
        // For now, we'll use the authenticated user approach with @AuthenticationPrincipal
        // In a real implementation, you'd get this from:
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal()

        // Placeholder - actual implementation depends on your CustomUserDetails class
        return null;
    }

    /**
     * Extracts user ID from UserDetails.
     *
     * @param userDetails the user details
     * @return user ID or null
     */
    private UUID extractUserId(UserDetails userDetails) {
        if (Objects.isNull(userDetails)) {
            return null;
        }

        // If using custom UserDetails with ID
        if (userDetails instanceof com.rewine.backend.configuration.security.CustomUserDetails customDetails) {
            return customDetails.getId();
        }

        // Fallback - try to parse username as UUID or lookup user
        // This should be customized based on your actual implementation
        return null;
    }
}


package com.rewine.backend.controller;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.CreateReviewRequest;
import com.rewine.backend.dto.response.ReviewResponse;
import com.rewine.backend.model.enums.ReviewFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Interface for review endpoints.
 */
@Tag(name = "Reviews", description = "Wine review management endpoints")
public interface IReviewController {

    /**
     * Gets reviews for a wine with filtering.
     *
     * @param wineId the wine ID
     * @param filter the filter type
     * @param page   page number
     * @param size   page size
     * @return paginated reviews
     */
    @Operation(
            summary = "Get wine reviews",
            description = "Get reviews for a wine with optional filtering (recent, featured, mine, top_rated)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved"),
            @ApiResponse(responseCode = "404", description = "Wine not found")
    })
    ResponseEntity<PageResponse<ReviewResponse>> getWineReviews(
            @Parameter(description = "Wine ID", required = true) UUID wineId,
            @Parameter(description = "Filter type") ReviewFilter filter,
            @Parameter(description = "Page number") int page,
            @Parameter(description = "Page size") int size
    );

    /**
     * Creates a review for a wine.
     *
     * @param wineId  the wine ID
     * @param request the review request
     * @return the created review
     */
    @Operation(
            summary = "Create a review",
            description = "Create a new review for a wine (authenticated)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Review created",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Wine not found"),
            @ApiResponse(responseCode = "409", description = "Already reviewed")
    })
    ResponseEntity<ReviewResponse> createReview(
            @Parameter(description = "Wine ID", required = true) UUID wineId,
            CreateReviewRequest request
    );

    /**
     * Updates a review.
     *
     * @param reviewId the review ID
     * @param request  the update request
     * @return the updated review
     */
    @Operation(
            summary = "Update a review",
            description = "Update your own review",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not your review"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Review ID", required = true) UUID reviewId,
            CreateReviewRequest request
    );

    /**
     * Deletes a review.
     *
     * @param reviewId the review ID
     * @return no content
     */
    @Operation(
            summary = "Delete a review",
            description = "Delete your own review",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not your review"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    ResponseEntity<Void> deleteReview(
            @Parameter(description = "Review ID", required = true) UUID reviewId
    );

    /**
     * Toggles like on a review.
     *
     * @param reviewId the review ID
     * @return like status
     */
    @Operation(
            summary = "Toggle review like",
            description = "Like or unlike a review",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like toggled"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    ResponseEntity<Boolean> toggleLike(
            @Parameter(description = "Review ID", required = true) UUID reviewId
    );
}


package com.rewine.backend.dto.response;

import com.rewine.backend.model.entity.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Response DTO for a review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private UUID id;
    private UUID wineId;
    private BigDecimal rating;
    private String title;
    private String comment;
    private Boolean isVerified;
    private Integer helpfulCount;
    private Integer likesCount;
    private Integer commentsCount;
    private ReviewerInfo reviewer;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Whether the current user has liked this review.
     */
    private Boolean likedByCurrentUser;

    /**
     * Nested reviewer info.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewerInfo {
        private UUID id;
        private String username;
        private String displayName;
        private String avatarUrl;
        private Integer totalReviews;
    }

    /**
     * Creates a response from a review entity.
     *
     * @param entity the review entity
     * @return the response DTO
     */
    public static ReviewResponse fromEntity(ReviewEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        ReviewerInfo reviewerInfo = null;
        if (Objects.nonNull(entity.getUser())) {
            reviewerInfo = ReviewerInfo.builder()
                    .id(entity.getUser().getId())
                    .username(entity.getUser().getUsername())
                    .displayName(Objects.nonNull(entity.getUser().getName())
                            ? entity.getUser().getName()
                            : entity.getUser().getUsername())
                    .avatarUrl(entity.getUser().getAvatarUrl())
                    .build();
        }

        return ReviewResponse.builder()
                .id(entity.getId())
                .wineId(entity.getWineId())
                .rating(entity.getRating())
                .title(entity.getTitle())
                .comment(entity.getComment())
                .isVerified(entity.getIsVerified())
                .helpfulCount(entity.getHelpfulCount())
                .likesCount(entity.getLikesCount())
                .commentsCount(entity.getCommentsCount())
                .reviewer(reviewerInfo)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .likedByCurrentUser(false) // Set by service if needed
                .build();
    }

    /**
     * Creates a response from entity with like status.
     *
     * @param entity              the review entity
     * @param likedByCurrentUser  whether current user liked it
     * @return the response DTO
     */
    public static ReviewResponse fromEntity(ReviewEntity entity, boolean likedByCurrentUser) {
        ReviewResponse response = fromEntity(entity);
        if (Objects.nonNull(response)) {
            response.setLikedByCurrentUser(likedByCurrentUser);
        }
        return response;
    }
}


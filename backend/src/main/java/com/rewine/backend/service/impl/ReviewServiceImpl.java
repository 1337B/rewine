package com.rewine.backend.service.impl;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.CreateReviewRequest;
import com.rewine.backend.dto.response.ReviewResponse;
import com.rewine.backend.exception.ErrorCode;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.model.entity.ReviewEntity;
import com.rewine.backend.model.entity.ReviewLikeEntity;
import com.rewine.backend.model.entity.UserEntity;
import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.enums.ReviewFilter;
import com.rewine.backend.repository.IReviewLikeRepository;
import com.rewine.backend.repository.IReviewRepository;
import com.rewine.backend.repository.IUserRepository;
import com.rewine.backend.repository.IWineRepository;
import com.rewine.backend.service.IReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Review service implementation.
 */
@Service
@Transactional
public class ReviewServiceImpl implements IReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final IReviewRepository reviewRepository;
    private final IReviewLikeRepository reviewLikeRepository;
    private final IWineRepository wineRepository;
    private final IUserRepository userRepository;

    public ReviewServiceImpl(
            IReviewRepository reviewRepository,
            IReviewLikeRepository reviewLikeRepository,
            IWineRepository wineRepository,
            IUserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.wineRepository = wineRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponse createReview(UUID wineId, UUID userId, CreateReviewRequest request) {
        LOGGER.info("Creating review for wine {} by user {}", wineId, userId);

        // Validate wine exists
        WineEntity wine = wineRepository.findById(wineId)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.WINE_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "Wine not found with ID: " + wineId
                ));

        // Validate user exists
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "User not found with ID: " + userId
                ));

        // Check if user already reviewed this wine
        if (reviewRepository.existsByWine_IdAndUser_Id(wineId, userId)) {
            throw new RewineException(
                    ErrorCode.CONFLICT,
                    HttpStatus.CONFLICT,
                    "You have already reviewed this wine"
            );
        }

        // Create review
        ReviewEntity review = ReviewEntity.builder()
                .wine(wine)
                .user(user)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .isVerified(false)
                .helpfulCount(0)
                .build();

        ReviewEntity savedReview = reviewRepository.save(review);
        LOGGER.info("Created review {} for wine {}", savedReview.getId(), wineId);

        // Update wine average rating
        updateWineRating(wineId);

        return ReviewResponse.fromEntity(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> listReviews(UUID wineId, ReviewFilter filter, UUID userId, Pageable pageable) {
        LOGGER.info("Listing reviews for wine {} with filter {}", wineId, filter);

        // Validate wine exists
        if (!wineRepository.existsById(wineId)) {
            throw new RewineException(
                    ErrorCode.WINE_NOT_FOUND,
                    HttpStatus.NOT_FOUND,
                    "Wine not found with ID: " + wineId
            );
        }

        Page<ReviewEntity> page;
        ReviewFilter effectiveFilter = Objects.nonNull(filter) ? filter : ReviewFilter.RECENT;

        switch (effectiveFilter) {
            case FEATURED:
                page = reviewRepository.findFeaturedByWineId(wineId, pageable);
                break;
            case MINE:
                if (Objects.isNull(userId)) {
                    // Return empty if no user
                    page = new PageImpl<>(Collections.emptyList(), pageable, 0);
                } else {
                    // Get user's review for this wine
                    page = reviewRepository.findByWine_IdAndUser_Id(wineId, userId)
                            .map(review -> new PageImpl<>(List.of(review), pageable, 1))
                            .orElse(new PageImpl<>(Collections.emptyList(), pageable, 0));
                }
                break;
            case TOP_RATED:
                page = reviewRepository.findByWine_Id(wineId, pageable);
                break;
            case RECENT:
            default:
                page = reviewRepository.findByWine_IdOrderByCreatedAtDesc(wineId, pageable);
                break;
        }

        // Get liked review IDs if user is logged in
        Set<UUID> likedReviewIds = getLikedReviewIds(userId, page.getContent());

        // Map to DTOs
        List<ReviewResponse> content = page.getContent().stream()
                .map(entity -> ReviewResponse.fromEntity(entity, likedReviewIds.contains(entity.getId())))
                .toList();

        LOGGER.debug("Found {} reviews for wine {}", content.size(), wineId);

        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReview(UUID reviewId) {
        LOGGER.info("Getting review {}", reviewId);

        ReviewEntity review = reviewRepository.findByIdWithUserAndWine(reviewId)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "Review not found with ID: " + reviewId
                ));

        return ReviewResponse.fromEntity(review);
    }

    @Override
    public ReviewResponse updateReview(UUID reviewId, UUID userId, CreateReviewRequest request) {
        LOGGER.info("Updating review {} by user {}", reviewId, userId);

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "Review not found with ID: " + reviewId
                ));

        // Check ownership
        if (!review.getUserId().equals(userId)) {
            throw new RewineException(
                    ErrorCode.FORBIDDEN,
                    HttpStatus.FORBIDDEN,
                    "You can only edit your own reviews"
            );
        }

        // Update fields
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());

        ReviewEntity updatedReview = reviewRepository.save(review);
        LOGGER.info("Updated review {}", reviewId);

        // Update wine average rating
        updateWineRating(review.getWineId());

        return ReviewResponse.fromEntity(updatedReview);
    }

    @Override
    public void deleteReview(UUID reviewId, UUID userId) {
        LOGGER.info("Deleting review {} by user {}", reviewId, userId);

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "Review not found with ID: " + reviewId
                ));

        // Check ownership (admin check can be added later with roles)
        if (!review.getUserId().equals(userId)) {
            throw new RewineException(
                    ErrorCode.FORBIDDEN,
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own reviews"
            );
        }

        UUID wineId = review.getWineId();
        reviewRepository.delete(review);
        LOGGER.info("Deleted review {}", reviewId);

        // Update wine average rating
        updateWineRating(wineId);
    }

    @Override
    public boolean toggleLike(UUID reviewId, UUID userId) {
        LOGGER.info("Toggling like on review {} by user {}", reviewId, userId);

        // Validate review exists
        if (!reviewRepository.existsById(reviewId)) {
            throw new RewineException(
                    ErrorCode.NOT_FOUND,
                    HttpStatus.NOT_FOUND,
                    "Review not found with ID: " + reviewId
            );
        }

        // Validate user exists
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "User not found with ID: " + userId
                ));

        ReviewLikeEntity.ReviewLikeId likeId = new ReviewLikeEntity.ReviewLikeId(reviewId, userId);

        if (reviewLikeRepository.existsById(likeId)) {
            // Unlike
            reviewLikeRepository.deleteById(likeId);
            LOGGER.debug("Removed like from review {}", reviewId);
            return false;
        } else {
            // Like
            ReviewEntity review = reviewRepository.findById(reviewId).orElseThrow();
            ReviewLikeEntity like = new ReviewLikeEntity();
            like.setId(likeId);
            like.setReview(review);
            like.setUser(user);
            reviewLikeRepository.save(like);
            LOGGER.debug("Added like to review {}", reviewId);
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(UUID wineId) {
        return reviewRepository.calculateAverageRating(wineId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getReviewCount(UUID wineId) {
        return reviewRepository.countByWine_Id(wineId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(UUID wineId, UUID userId) {
        return reviewRepository.existsByWine_IdAndUser_Id(wineId, userId);
    }

    /**
     * Gets the set of review IDs liked by the user.
     */
    private Set<UUID> getLikedReviewIds(UUID userId, List<ReviewEntity> reviews) {
        if (Objects.isNull(userId) || reviews.isEmpty()) {
            return Collections.emptySet();
        }

        List<UUID> reviewIds = reviews.stream()
                .map(ReviewEntity::getId)
                .toList();

        return reviewLikeRepository.findReviewIdsLikedByUser(userId).stream()
                .filter(reviewIds::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Updates the wine's average rating and review count.
     */
    private void updateWineRating(UUID wineId) {
        Double avgRating = reviewRepository.calculateAverageRating(wineId);
        long count = reviewRepository.countByWine_Id(wineId);

        wineRepository.findById(wineId).ifPresent(wine -> {
            wine.setRatingAverage(Objects.nonNull(avgRating)
                    ? java.math.BigDecimal.valueOf(avgRating)
                    : null);
            wine.setRatingCount((int) count);
            wineRepository.save(wine);
            LOGGER.debug("Updated wine {} rating: avg={}, count={}", wineId, avgRating, count);
        });
    }
}


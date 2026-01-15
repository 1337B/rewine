package com.rewine.backend.utils.builder.impl;

import com.rewine.backend.dto.response.ReviewResponse;
import com.rewine.backend.dto.response.WineDetailsResponse;
import com.rewine.backend.dto.response.WineDetailsResponse.RatingDistribution;
import com.rewine.backend.dto.response.WineDetailsResponse.UserWineData;
import com.rewine.backend.exception.ErrorCode;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.model.entity.ReviewEntity;
import com.rewine.backend.model.entity.WineAiProfileEntity;
import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.enums.AiProfileStatus;
import com.rewine.backend.repository.IReviewLikeRepository;
import com.rewine.backend.repository.IReviewRepository;
import com.rewine.backend.repository.IWineAiProfileRepository;
import com.rewine.backend.repository.IWineRepository;
import com.rewine.backend.utils.builder.IWineDetailsAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of wine details aggregator.
 * Combines multiple data sources into a rich wine details response.
 */
@Component
@Transactional(readOnly = true)
public class WineDetailsAggregatorImpl implements IWineDetailsAggregator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineDetailsAggregatorImpl.class);

    /**
     * Default number of featured reviews to include in the preview.
     */
    private static final int DEFAULT_FEATURED_REVIEWS_COUNT = 4;

    /**
     * Default language for AI profile lookups.
     */
    private static final String DEFAULT_LANGUAGE = "es-AR";

    private final IWineRepository wineRepository;
    private final IReviewRepository reviewRepository;
    private final IReviewLikeRepository reviewLikeRepository;
    private final IWineAiProfileRepository wineAiProfileRepository;

    public WineDetailsAggregatorImpl(
            IWineRepository wineRepository,
            IReviewRepository reviewRepository,
            IReviewLikeRepository reviewLikeRepository,
            IWineAiProfileRepository wineAiProfileRepository) {
        this.wineRepository = wineRepository;
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.wineAiProfileRepository = wineAiProfileRepository;
    }

    @Override
    public WineDetailsResponse aggregate(UUID wineId, UUID userId) {
        LOGGER.info("Aggregating wine details for wineId={}, userId={}", wineId, userId);

        // Validate input
        if (Objects.isNull(wineId)) {
            throw new RewineException(
                    ErrorCode.VALIDATION_ERROR,
                    HttpStatus.BAD_REQUEST,
                    "Wine ID is required"
            );
        }

        // Load wine with winery eagerly
        WineEntity wine = loadWine(wineId);
        LOGGER.debug("Loaded wine: {} ({})", wine.getName(), wine.getWineType());

        // Build base response from entity
        WineDetailsResponse response = WineDetailsResponse.fromEntity(wine);

        // Aggregate rating distribution
        RatingDistribution ratingDistribution = buildRatingDistribution(wineId);
        response.setRatingDistribution(ratingDistribution);
        LOGGER.debug("Rating distribution: {} total reviews", ratingDistribution.getTotalReviews());

        // Aggregate featured reviews preview
        List<ReviewResponse> featuredReviews = buildFeaturedReviewsPreview(wineId, userId);
        response.setFeaturedReviews(featuredReviews);
        LOGGER.debug("Featured reviews preview: {} reviews", featuredReviews.size());

        // Aggregate user-specific data if user is authenticated
        if (Objects.nonNull(userId)) {
            UserWineData userWineData = buildUserWineData(wineId, userId);
            response.setUserWineData(userWineData);
            LOGGER.debug("User wine data: hasReviewed={}", userWineData.getHasReviewed());
        }

        // Set AI profile status and generatedAt
        resolveAiProfileInfo(wineId, response);

        LOGGER.info("Successfully aggregated wine details for wineId={}", wineId);
        return response;
    }

    /**
     * Loads the wine entity with winery eagerly loaded.
     *
     * @param wineId the wine ID
     * @return the wine entity
     */
    private WineEntity loadWine(UUID wineId) {
        return wineRepository.findByIdWithWinery(wineId)
                .orElseThrow(() -> {
                    LOGGER.warn("Wine not found with ID: {}", wineId);
                    return new RewineException(
                            ErrorCode.WINE_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "Wine not found with ID: " + wineId
                    );
                });
    }

    /**
     * Minimum star rating value.
     */
    private static final int MIN_STAR_RATING = 1;

    /**
     * Maximum star rating value.
     */
    private static final int MAX_STAR_RATING = 5;

    /**
     * Builds the rating distribution for a wine.
     *
     * @param wineId the wine ID
     * @return the rating distribution
     */
    private RatingDistribution buildRatingDistribution(UUID wineId) {
        List<Object[]> rawDistribution = reviewRepository.countByRatingDistribution(wineId);

        Map<Integer, Long> distribution = new HashMap<>();
        // Initialize all ratings to 0
        for (int i = MIN_STAR_RATING; i <= MAX_STAR_RATING; i++) {
            distribution.put(i, 0L);
        }

        // Populate from query results
        for (Object[] row : rawDistribution) {
            if (Objects.nonNull(row) && row.length >= 2
                    && Objects.nonNull(row[0]) && Objects.nonNull(row[1])) {
                Integer rating = (Integer) row[0];
                Long count = (Long) row[1];
                if (rating >= MIN_STAR_RATING && rating <= MAX_STAR_RATING) {
                    distribution.put(rating, count);
                }
            }
        }

        return RatingDistribution.fromMap(distribution);
    }

    /**
     * Builds the featured reviews preview for a wine.
     *
     * @param wineId the wine ID
     * @param userId the optional user ID for determining if user liked each review
     * @return list of featured review responses
     */
    private List<ReviewResponse> buildFeaturedReviewsPreview(UUID wineId, UUID userId) {
        PageRequest pageRequest = PageRequest.of(0, DEFAULT_FEATURED_REVIEWS_COUNT);
        List<ReviewEntity> featuredReviews = reviewRepository.findFeaturedByWineId(wineId, pageRequest).getContent();

        if (featuredReviews.isEmpty()) {
            return Collections.emptyList();
        }

        // Get liked review IDs if user is authenticated
        Set<UUID> likedReviewIds = getLikedReviewIds(userId, featuredReviews);

        return featuredReviews.stream()
                .map(entity -> ReviewResponse.fromEntity(entity, likedReviewIds.contains(entity.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Gets the set of review IDs that the user has liked.
     *
     * @param userId  the user ID
     * @param reviews the list of reviews to check
     * @return set of liked review IDs
     */
    private Set<UUID> getLikedReviewIds(UUID userId, List<ReviewEntity> reviews) {
        if (Objects.isNull(userId) || reviews.isEmpty()) {
            return Collections.emptySet();
        }

        List<UUID> reviewIds = reviews.stream()
                .map(ReviewEntity::getId)
                .collect(Collectors.toList());

        // Get all likes for this user
        List<UUID> allLikedByUser = reviewLikeRepository.findReviewIdsLikedByUser(userId);

        // Filter to only include the reviews we're checking
        return reviewIds.stream()
                .filter(allLikedByUser::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Builds user-specific wine data.
     *
     * @param wineId the wine ID
     * @param userId the user ID
     * @return the user wine data
     */
    private UserWineData buildUserWineData(UUID wineId, UUID userId) {
        // Check if user has reviewed this wine
        boolean hasReviewed = reviewRepository.existsByWine_IdAndUser_Id(wineId, userId);
        UUID userReviewId = null;

        if (hasReviewed) {
            userReviewId = reviewRepository.findByWine_IdAndUser_Id(wineId, userId)
                    .map(ReviewEntity::getId)
                    .orElse(null);
        }

        // Placeholder for cellar and wishlist - these features would require additional repositories
        Boolean inCellar = null; // TODO: Implement when cellar feature is ready
        Boolean inWishlist = null; // TODO: Implement when wishlist feature is ready

        return UserWineData.builder()
                .hasReviewed(hasReviewed)
                .userReviewId(userReviewId)
                .inCellar(inCellar)
                .inWishlist(inWishlist)
                .build();
    }

    /**
     * Resolves the AI profile status and generation timestamp for a wine.
     * Checks the database for an existing AI profile in the default language.
     *
     * @param wineId   the wine ID
     * @param response the response to update with AI profile info
     */
    private void resolveAiProfileInfo(UUID wineId, WineDetailsResponse response) {
        LOGGER.debug("Checking AI profile status for wineId={}", wineId);

        Optional<WineAiProfileEntity> aiProfile = wineAiProfileRepository
                .findByWineIdAndLanguage(wineId, DEFAULT_LANGUAGE);

        if (aiProfile.isPresent()) {
            response.setAiProfileStatus(AiProfileStatus.GENERATED);
            response.setAiProfileGeneratedAt(aiProfile.get().getCreatedAt());
            LOGGER.debug("AI profile found for wineId={}, generatedAt={}",
                    wineId, aiProfile.get().getCreatedAt());
        } else {
            response.setAiProfileStatus(AiProfileStatus.NOT_REQUESTED);
            response.setAiProfileGeneratedAt(null);
            LOGGER.debug("No AI profile found for wineId={}", wineId);
        }
    }
}


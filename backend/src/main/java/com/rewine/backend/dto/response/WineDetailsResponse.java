package com.rewine.backend.dto.response;

import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.entity.WineryEntity;
import com.rewine.backend.model.enums.AiProfileStatus;
import com.rewine.backend.model.enums.WineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Detailed response for single wine view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WineDetailsResponse {

    private UUID id;
    private String name;
    private Integer vintage;
    private WineType wineType;
    private String style;
    private List<String> grapes;
    private List<String> allergens;
    private String descriptionEs;
    private String descriptionEn;
    private BigDecimal alcoholContent;
    private Integer servingTempMin;
    private Integer servingTempMax;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String imageUrl;
    private BigDecimal ratingAverage;
    private Integer ratingCount;
    private Boolean isFeatured;
    private Instant createdAt;

    // Winery information
    private WineryInfo winery;

    // Aggregated rating distribution (counts per star: 1->count, 2->count, etc.)
    private RatingDistribution ratingDistribution;

    // Featured reviews preview (top N reviews)
    private List<ReviewResponse> featuredReviews;

    // User-specific data
    private UserWineData userWineData;

    // AI profile status
    private AiProfileStatus aiProfileStatus;

    /**
     * Rating distribution showing counts per star rating.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingDistribution {
        /** Rating value constants. */
        private static final int RATING_ONE_STAR = 1;
        private static final int RATING_TWO_STARS = 2;
        private static final int RATING_THREE_STARS = 3;
        private static final int RATING_FOUR_STARS = 4;
        private static final int RATING_FIVE_STARS = 5;

        private Integer oneStar;
        private Integer twoStars;
        private Integer threeStars;
        private Integer fourStars;
        private Integer fiveStars;
        private Integer totalReviews;

        /**
         * Creates rating distribution from a map of rating to count.
         *
         * @param distribution map of rating (1-5) to count
         * @return the rating distribution
         */
        public static RatingDistribution fromMap(Map<Integer, Long> distribution) {
            return RatingDistribution.builder()
                    .oneStar(distribution.getOrDefault(RATING_ONE_STAR, 0L).intValue())
                    .twoStars(distribution.getOrDefault(RATING_TWO_STARS, 0L).intValue())
                    .threeStars(distribution.getOrDefault(RATING_THREE_STARS, 0L).intValue())
                    .fourStars(distribution.getOrDefault(RATING_FOUR_STARS, 0L).intValue())
                    .fiveStars(distribution.getOrDefault(RATING_FIVE_STARS, 0L).intValue())
                    .totalReviews(distribution.values().stream().mapToInt(Long::intValue).sum())
                    .build();
        }
    }

    /**
     * User-specific data for this wine.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserWineData {
        private Boolean hasReviewed;
        private UUID userReviewId;
        private Boolean inCellar;
        private Boolean inWishlist;
    }

    /**
     * Winery information nested object.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WineryInfo {
        private UUID id;
        private String name;
        private String country;
        private String region;
        private String subregion;
        private String description;
        private String websiteUrl;
        private String logoUrl;
        private Integer established;

        /**
         * Creates winery info from entity.
         *
         * @param entity the winery entity
         * @return the winery info
         */
        public static WineryInfo fromEntity(WineryEntity entity) {
            if (Objects.isNull(entity)) {
                return null;
            }

            return WineryInfo.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .country(entity.getCountry())
                    .region(entity.getRegion())
                    .subregion(entity.getSubregion())
                    .description(entity.getDescription())
                    .websiteUrl(entity.getWebsiteUrl())
                    .logoUrl(entity.getLogoUrl())
                    .established(entity.getEstablished())
                    .build();
        }
    }

    /**
     * Creates a detailed response from a wine entity.
     *
     * @param entity the wine entity
     * @return the detailed response
     */
    public static WineDetailsResponse fromEntity(WineEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return WineDetailsResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .vintage(entity.getVintage())
                .wineType(entity.getWineType())
                .style(entity.getStyle())
                .grapes(entity.getGrapes())
                .allergens(entity.getAllergens())
                .descriptionEs(entity.getDescriptionEs())
                .descriptionEn(entity.getDescriptionEn())
                .alcoholContent(entity.getAlcoholContent())
                .servingTempMin(entity.getServingTempMin())
                .servingTempMax(entity.getServingTempMax())
                .priceMin(entity.getPriceMin())
                .priceMax(entity.getPriceMax())
                .imageUrl(entity.getImageUrl())
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .isFeatured(entity.getIsFeatured())
                .createdAt(entity.getCreatedAt())
                .winery(WineryInfo.fromEntity(entity.getWinery()))
                .build();
    }
}


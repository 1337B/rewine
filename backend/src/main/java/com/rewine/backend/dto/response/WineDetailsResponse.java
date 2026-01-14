package com.rewine.backend.dto.response;

import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.entity.WineryEntity;
import com.rewine.backend.model.enums.WineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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


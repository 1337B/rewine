package com.rewine.backend.dto.response;

import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.enums.WineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Summary response for wine listings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WineSummaryResponse {

    private UUID id;
    private String name;
    private Integer vintage;
    private WineType wineType;
    private String style;
    private String wineryName;
    private String region;
    private String country;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private BigDecimal ratingAverage;
    private Integer ratingCount;
    private String imageUrl;
    private Boolean isFeatured;

    /**
     * Creates a summary response from a wine entity.
     *
     * @param entity the wine entity
     * @return the summary response
     */
    public static WineSummaryResponse fromEntity(WineEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return WineSummaryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .vintage(entity.getVintage())
                .wineType(entity.getWineType())
                .style(entity.getStyle())
                .wineryName(entity.getWineryName())
                .region(entity.getRegion())
                .country(entity.getCountry())
                .priceMin(entity.getPriceMin())
                .priceMax(entity.getPriceMax())
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .imageUrl(entity.getImageUrl())
                .isFeatured(entity.getIsFeatured())
                .build();
    }
}


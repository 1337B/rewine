package com.rewine.backend.dto.response;

import com.rewine.backend.model.entity.WineRouteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Summary response DTO for wine route listings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Wine route summary for list views")
public class WineRouteSummaryResponse {

    @Schema(description = "Route unique identifier")
    private UUID id;

    @Schema(description = "Route name", example = "Ruta del Malbec - Luján de Cuyo")
    private String name;

    @Schema(description = "Brief description")
    private String description;

    @Schema(description = "Country", example = "Argentina")
    private String country;

    @Schema(description = "Region", example = "Mendoza")
    private String region;

    @Schema(description = "Subregion", example = "Luján de Cuyo")
    private String subregion;

    @Schema(description = "Estimated duration in minutes", example = "480")
    private Integer estimatedDuration;

    @Schema(description = "Estimated days to complete", example = "2")
    private Integer estimatedDays;

    @Schema(description = "Total distance in kilometers", example = "85.5")
    private Double totalDistance;

    @Schema(description = "Difficulty level", example = "easy")
    private String difficulty;

    @Schema(description = "Cover image URL")
    private String imageUrl;

    @Schema(description = "Route status", example = "active")
    private String status;

    @Schema(description = "Number of wineries on this route")
    private Integer wineryCount;

    @Schema(description = "Number of stops on this route")
    private Integer stopCount;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    /**
     * Creates a summary response from a WineRouteEntity.
     *
     * @param entity the route entity
     * @return the summary response
     */
    public static WineRouteSummaryResponse fromEntity(WineRouteEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return WineRouteSummaryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(truncateDescription(entity.getDescription(), 200))
                .country(entity.getCountry())
                .region(entity.getRegion())
                .subregion(entity.getSubregion())
                .estimatedDuration(entity.getEstimatedDuration())
                .estimatedDays(entity.getEstimatedDays())
                .totalDistance(entity.getTotalDistance())
                .difficulty(entity.getDifficulty())
                .imageUrl(entity.getImageUrl())
                .status(entity.getStatus())
                .wineryCount(Objects.nonNull(entity.getWineries()) ? entity.getWineries().size() : 0)
                .stopCount(Objects.nonNull(entity.getStops()) ? entity.getStops().size() : 0)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private static final int TRUNCATION_SUFFIX_LENGTH = 3;

    private static String truncateDescription(String description, int maxLength) {
        if (Objects.isNull(description) || description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength - TRUNCATION_SUFFIX_LENGTH) + "...";
    }
}


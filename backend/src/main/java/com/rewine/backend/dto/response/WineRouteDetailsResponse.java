package com.rewine.backend.dto.response;

import com.rewine.backend.model.entity.WineRouteEntity;
import com.rewine.backend.model.entity.WineRouteStopEntity;
import com.rewine.backend.model.entity.WineryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Detailed response DTO for wine route with all information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed wine route information")
public class WineRouteDetailsResponse {

    private static final int DESCRIPTION_MAX_LENGTH = 200;
    private static final int WINERY_DESCRIPTION_MAX_LENGTH = 150;
    private static final int TRUNCATION_SUFFIX_LENGTH = 3;

    @Schema(description = "Route unique identifier")
    private UUID id;

    @Schema(description = "Route name", example = "Ruta del Malbec - Luján de Cuyo")
    private String name;

    @Schema(description = "Full description")
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

    @Schema(description = "Recommended wine types")
    private List<String> recommendedWineTypes;

    @Schema(description = "Stops along the route")
    private List<RouteStopResponse> stops;

    @Schema(description = "Wineries on this route")
    private List<RouteWineryResponse> wineries;

    @Schema(description = "Creator information")
    private RouteCreatorResponse createdBy;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp")
    private Instant updatedAt;

    /**
     * Creates a detailed response from a WineRouteEntity.
     *
     * @param entity the route entity
     * @return the details response
     */
    public static WineRouteDetailsResponse fromEntity(WineRouteEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        List<RouteStopResponse> stopResponses = new ArrayList<>();
        if (Objects.nonNull(entity.getStops())) {
            stopResponses = entity.getStops().stream()
                    .sorted(Comparator.comparingInt(WineRouteStopEntity::getStopOrder))
                    .map(RouteStopResponse::fromEntity)
                    .toList();
        }

        List<RouteWineryResponse> wineryResponses = new ArrayList<>();
        if (Objects.nonNull(entity.getWineries())) {
            wineryResponses = entity.getWineries().stream()
                    .map(RouteWineryResponse::fromEntity)
                    .toList();
        }

        List<String> wineTypes = parseWineTypes(entity.getRecommendedWineTypesJson());

        RouteCreatorResponse creatorResponse = null;
        if (Objects.nonNull(entity.getCreatedBy())) {
            creatorResponse = RouteCreatorResponse.builder()
                    .id(entity.getCreatedBy().getId())
                    .username(entity.getCreatedBy().getUsername())
                    .name(entity.getCreatedBy().getName())
                    .build();
        }

        return WineRouteDetailsResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .country(entity.getCountry())
                .region(entity.getRegion())
                .subregion(entity.getSubregion())
                .estimatedDuration(entity.getEstimatedDuration())
                .estimatedDays(entity.getEstimatedDays())
                .totalDistance(entity.getTotalDistance())
                .difficulty(entity.getDifficulty())
                .imageUrl(entity.getImageUrl())
                .status(entity.getStatus())
                .recommendedWineTypes(wineTypes)
                .stops(stopResponses)
                .wineries(wineryResponses)
                .createdBy(creatorResponse)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private static List<String> parseWineTypes(String json) {
        if (Objects.isNull(json) || json.isBlank()) {
            return new ArrayList<>();
        }
        // Simple JSON array parsing
        String cleaned = json.replace("[", "").replace("]", "").replace("\"", "");
        if (cleaned.isBlank()) {
            return new ArrayList<>();
        }
        return List.of(cleaned.split(",\\s*"));
    }

    /**
     * Route stop response.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "A stop on the wine route")
    public static class RouteStopResponse {

        @Schema(description = "Stop unique identifier")
        private UUID id;

        @Schema(description = "Stop name")
        private String name;

        @Schema(description = "Stop description")
        private String description;

        @Schema(description = "Stop type (winery, restaurant, viewpoint, etc.)")
        private String type;

        @Schema(description = "Stop address")
        private String address;

        @Schema(description = "Latitude coordinate")
        private BigDecimal latitude;

        @Schema(description = "Longitude coordinate")
        private BigDecimal longitude;

        @Schema(description = "Order in the route (1-based)")
        private Integer stopOrder;

        @Schema(description = "Estimated time at this stop in minutes")
        private Integer estimatedDuration;

        public static RouteStopResponse fromEntity(WineRouteStopEntity entity) {
            if (Objects.isNull(entity)) {
                return null;
            }
            return RouteStopResponse.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .type(entity.getType())
                    .address(entity.getAddress())
                    .latitude(entity.getLatitude())
                    .longitude(entity.getLongitude())
                    .stopOrder(entity.getStopOrder())
                    .estimatedDuration(entity.getEstimatedDuration())
                    .build();
        }
    }

    /**
     * Winery response for route context.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Winery included in the route")
    public static class RouteWineryResponse {

        @Schema(description = "Winery unique identifier")
        private UUID id;

        @Schema(description = "Winery name")
        private String name;

        @Schema(description = "Country")
        private String country;

        @Schema(description = "Region")
        private String region;

        @Schema(description = "Subregion")
        private String subregion;

        @Schema(description = "Brief description")
        private String description;

        @Schema(description = "Logo URL")
        private String logoUrl;

        @Schema(description = "Website URL")
        private String websiteUrl;

        @Schema(description = "Year established")
        private Integer established;

        public static RouteWineryResponse fromEntity(WineryEntity entity) {
            if (Objects.isNull(entity)) {
                return null;
            }
            return RouteWineryResponse.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .country(entity.getCountry())
                    .region(entity.getRegion())
                    .subregion(entity.getSubregion())
                    .description(truncateDescription(entity.getDescription(), WINERY_DESCRIPTION_MAX_LENGTH))
                    .logoUrl(entity.getLogoUrl())
                    .websiteUrl(entity.getWebsiteUrl())
                    .established(entity.getEstablished())
                    .build();
        }

        private static String truncateDescription(String description, int maxLength) {
            if (Objects.isNull(description) || description.length() <= maxLength) {
                return description;
            }
            return description.substring(0, maxLength - TRUNCATION_SUFFIX_LENGTH) + "...";
        }
    }

    /**
     * Route creator information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Route creator information")
    public static class RouteCreatorResponse {

        @Schema(description = "Creator user ID")
        private UUID id;

        @Schema(description = "Creator username")
        private String username;

        @Schema(description = "Creator display name")
        private String name;
    }
}


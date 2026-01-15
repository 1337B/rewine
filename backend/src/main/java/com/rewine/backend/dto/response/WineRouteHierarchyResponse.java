package com.rewine.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for hierarchical location browsing (countries -> regions -> subregions).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hierarchical location data for wine route browsing")
public class WineRouteHierarchyResponse {

    @Schema(description = "List of countries with wine routes")
    private List<CountryNode> countries;

    /**
     * Country node with regions.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Country with its regions")
    public static class CountryNode {

        @Schema(description = "Country name", example = "Argentina")
        private String name;

        @Schema(description = "Number of routes in this country")
        private Long routeCount;

        @Schema(description = "Regions within this country")
        private List<RegionNode> regions;
    }

    /**
     * Region node with subregions.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Region with its subregions")
    public static class RegionNode {

        @Schema(description = "Region name", example = "Mendoza")
        private String name;

        @Schema(description = "Number of routes in this region")
        private Long routeCount;

        @Schema(description = "Subregions within this region")
        private List<SubregionNode> subregions;
    }

    /**
     * Subregion node.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Subregion")
    public static class SubregionNode {

        @Schema(description = "Subregion name", example = "Luján de Cuyo")
        private String name;

        @Schema(description = "Number of routes in this subregion")
        private Long routeCount;
    }
}


package com.rewine.backend.controller;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.response.WineRouteDetailsResponse;
import com.rewine.backend.dto.response.WineRouteHierarchyResponse;
import com.rewine.backend.dto.response.WineRouteSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * Interface for wine route endpoints.
 * Provides hierarchical browsing and route retrieval functionality.
 */
@Tag(name = "Wine Routes", description = "Wine route browsing and management")
public interface IWineRouteController {

    // =========================================================================
    // Hierarchical Browsing
    // =========================================================================

    @Operation(
            summary = "Get location hierarchy",
            description = "Returns the complete hierarchy of countries, regions, and subregions with wine routes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hierarchy retrieved successfully")
    })
    ResponseEntity<WineRouteHierarchyResponse> getHierarchy();

    @Operation(
            summary = "List countries",
            description = "Returns all countries that have active wine routes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Countries retrieved successfully")
    })
    ResponseEntity<List<String>> listCountries();

    @Operation(
            summary = "List regions",
            description = "Returns all regions within a country that have active wine routes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Regions retrieved successfully")
    })
    ResponseEntity<List<String>> listRegions(
            @Parameter(description = "Country name", example = "Argentina") String country
    );

    @Operation(
            summary = "List subregions",
            description = "Returns all subregions within a country and region that have active wine routes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subregions retrieved successfully")
    })
    ResponseEntity<List<String>> listSubregions(
            @Parameter(description = "Country name", example = "Argentina") String country,
            @Parameter(description = "Region name", example = "Mendoza") String region
    );

    // =========================================================================
    // Route Listing & Details
    // =========================================================================

    @Operation(
            summary = "List wine routes",
            description = "Returns paginated list of wine routes with optional filters"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Routes retrieved successfully")
    })
    ResponseEntity<PageResponse<WineRouteSummaryResponse>> listRoutes(
            @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
            @Parameter(description = "Filter by region") @RequestParam(required = false) String region,
            @Parameter(description = "Filter by subregion") @RequestParam(required = false) String subregion,
            @Parameter(description = "Search query") @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    );

    @Operation(
            summary = "Get route details",
            description = "Returns detailed information about a specific wine route"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Route details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Route not found")
    })
    ResponseEntity<WineRouteDetailsResponse> getRouteDetails(
            @Parameter(description = "Route ID") UUID routeId
    );
}


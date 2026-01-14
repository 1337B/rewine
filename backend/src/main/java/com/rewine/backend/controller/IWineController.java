package com.rewine.backend.controller;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.WineSearchRequest;
import com.rewine.backend.dto.response.WineDetailsResponse;
import com.rewine.backend.dto.response.WineSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Interface for wine endpoints.
 */
@Tag(name = "Wines", description = "Wine catalog management endpoints")
public interface IWineController {

    /**
     * Searches wines with filters and pagination.
     *
     * @param request the search request with filters
     * @param page    page number (0-indexed)
     * @param size    page size
     * @return paginated wine list
     */
    @Operation(
            summary = "Search wines",
            description = "Search and filter wines with pagination support"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Wines found",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<PageResponse<WineSummaryResponse>> searchWines(
            @Parameter(description = "Search and filter criteria") WineSearchRequest request,
            @Parameter(description = "Page number (0-indexed)") int page,
            @Parameter(description = "Page size") int size
    );

    /**
     * Gets wine details by ID.
     *
     * @param id the wine ID
     * @return the wine details
     */
    @Operation(
            summary = "Get wine details",
            description = "Get detailed information about a specific wine"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Wine found",
                    content = @Content(schema = @Schema(implementation = WineDetailsResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Wine not found")
    })
    ResponseEntity<WineDetailsResponse> getWineDetails(
            @Parameter(description = "Wine ID", required = true) UUID id
    );

    /**
     * Gets featured wines.
     *
     * @param page page number
     * @param size page size
     * @return featured wines
     */
    @Operation(
            summary = "Get featured wines",
            description = "Get a list of featured wines"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Featured wines retrieved")
    })
    ResponseEntity<PageResponse<WineSummaryResponse>> getFeaturedWines(
            @Parameter(description = "Page number") int page,
            @Parameter(description = "Page size") int size
    );

    /**
     * Gets top-rated wines.
     *
     * @param page page number
     * @param size page size
     * @return top-rated wines
     */
    @Operation(
            summary = "Get top-rated wines",
            description = "Get wines sorted by rating"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top-rated wines retrieved")
    })
    ResponseEntity<PageResponse<WineSummaryResponse>> getTopRatedWines(
            @Parameter(description = "Page number") int page,
            @Parameter(description = "Page size") int size
    );

    /**
     * Gets recently added wines.
     *
     * @param page page number
     * @param size page size
     * @return recent wines
     */
    @Operation(
            summary = "Get recent wines",
            description = "Get recently added wines"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recent wines retrieved")
    })
    ResponseEntity<PageResponse<WineSummaryResponse>> getRecentWines(
            @Parameter(description = "Page number") int page,
            @Parameter(description = "Page size") int size
    );
}


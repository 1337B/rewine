package com.rewine.backend.controller;

import com.rewine.backend.dto.request.WineComparisonRequest;
import com.rewine.backend.dto.response.WineComparisonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

/**
 * Interface for wine comparison endpoints.
 */
@Tag(name = "Wine Comparisons", description = "AI-generated wine comparison endpoints")
public interface IWineComparisonController {

    /**
     * Compares two wines using AI.
     * If a cached comparison exists, returns it immediately.
     * Otherwise, generates a new comparison, caches it, and returns it.
     *
     * @param request the comparison request containing wine IDs and options
     * @return the AI comparison result
     */
    @Operation(
            summary = "Compare two wines",
            description = "Generates an AI comparison between two wines. "
                    + "Returns cached comparison if available, otherwise generates and caches a new one. "
                    + "The wine pair is normalized internally to prevent duplicate comparisons.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comparison retrieved or generated",
                    content = @Content(schema = @Schema(implementation = WineComparisonResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request (same wine ID for both)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "One or both wines not found"),
            @ApiResponse(responseCode = "503", description = "AI service unavailable")
    })
    ResponseEntity<WineComparisonResponse> compareWines(WineComparisonRequest request);
}


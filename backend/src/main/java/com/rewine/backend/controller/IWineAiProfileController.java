package com.rewine.backend.controller;

import com.rewine.backend.dto.common.AiCacheStatusResponse;
import com.rewine.backend.dto.request.WineAiProfileGenerateRequest;
import com.rewine.backend.dto.response.WineAiProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Interface for wine AI profile endpoints.
 */
@Tag(name = "Wine AI Profiles", description = "AI-generated wine profile management endpoints")
public interface IWineAiProfileController {

    /**
     * Gets an AI-generated profile for a wine.
     * If the profile exists in cache, returns it immediately.
     * Otherwise, generates a new profile, caches it, and returns it.
     *
     * @param wineId   the wine ID
     * @param language the language code (default: "es-AR")
     * @return the AI profile
     */
    @Operation(
            summary = "Get AI profile for a wine",
            description = "Retrieves the AI-generated profile for a wine. "
                    + "Returns cached profile if available, otherwise generates and caches a new one.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AI profile retrieved or generated",
                    content = @Content(schema = @Schema(implementation = WineAiProfileResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Wine not found"),
            @ApiResponse(responseCode = "503", description = "AI service unavailable")
    })
    ResponseEntity<WineAiProfileResponse> getWineAiProfile(
            @Parameter(description = "Wine ID", required = true) UUID wineId,
            @Parameter(description = "Language code (e.g., 'es-AR', 'en-US')") String language
    );

    /**
     * Generates or regenerates an AI profile for a wine.
     *
     * @param wineId  the wine ID
     * @param request the generation request with options
     * @return the generated AI profile
     */
    @Operation(
            summary = "Generate AI profile for a wine",
            description = "Generates a new AI profile for a wine. "
                    + "Can optionally force regeneration of an existing profile.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AI profile generated",
                    content = @Content(schema = @Schema(implementation = WineAiProfileResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Wine not found"),
            @ApiResponse(responseCode = "503", description = "AI service unavailable")
    })
    ResponseEntity<WineAiProfileResponse> generateWineAiProfile(
            @Parameter(description = "Wine ID", required = true) UUID wineId,
            @Parameter(description = "Generation request") WineAiProfileGenerateRequest request
    );

    /**
     * Gets the AI profile cache status for a wine.
     *
     * @param wineId   the wine ID
     * @param language the language code to check
     * @return the cache status
     */
    @Operation(
            summary = "Get AI profile cache status",
            description = "Checks if an AI profile exists for a wine and language combination.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cache status retrieved",
                    content = @Content(schema = @Schema(implementation = AiCacheStatusResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Wine not found")
    })
    ResponseEntity<AiCacheStatusResponse> getAiProfileStatus(
            @Parameter(description = "Wine ID", required = true) UUID wineId,
            @Parameter(description = "Language code (e.g., 'es-AR', 'en-US')") String language
    );
}


package com.rewine.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request for generating a wine comparison.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to compare two wines")
public class WineComparisonRequest {

    /**
     * The ID of the first wine to compare.
     */
    @NotNull(message = "First wine ID is required")
    @Schema(description = "ID of the first wine", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID wineAId;

    /**
     * The ID of the second wine to compare.
     */
    @NotNull(message = "Second wine ID is required")
    @Schema(description = "ID of the second wine", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID wineBId;

    /**
     * The language code for the comparison (e.g., "es-AR", "en-US").
     * Defaults to "es-AR" if not provided.
     */
    @Schema(description = "Language code (e.g., 'es-AR', 'en-US')", example = "es-AR")
    private String language;

    /**
     * Whether to force regeneration even if a cached comparison exists.
     */
    @Schema(description = "Force regeneration of comparison", defaultValue = "false")
    @Builder.Default
    private boolean forceRegenerate = false;
}


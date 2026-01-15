package com.rewine.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to generate an AI profile for a wine.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WineAiProfileGenerateRequest {

    /**
     * The language code for the profile generation.
     * Supported formats: "es-AR", "en-US", "es", "en", etc.
     * Default is "es-AR" if not specified.
     */
    @NotBlank(message = "Language code is required")
    @Pattern(
            regexp = "^[a-z]{2}(-[A-Z]{2})?$",
            message = "Invalid language code format. Use ISO format like 'es', 'en-US', 'es-AR'"
    )
    @Builder.Default
    private String language = "es-AR";

    /**
     * Whether to force regeneration even if a cached profile exists.
     * Default is false (use cached profile if available).
     */
    @Builder.Default
    private Boolean forceRegenerate = false;
}


package com.rewine.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response containing an AI-generated wine profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WineAiProfileResponse {

    /**
     * The wine ID this profile belongs to.
     */
    private UUID wineId;

    /**
     * The wine name for reference.
     */
    private String wineName;

    /**
     * The language code of this profile (e.g., "es-AR", "en-US").
     */
    private String language;

    /**
     * When this profile was generated.
     */
    private Instant generatedAt;

    /**
     * AI-generated summary of the wine.
     */
    private String summary;

    /**
     * AI-generated detailed tasting notes.
     */
    private TastingNotes tastingNotes;

    /**
     * AI-generated food pairing suggestions.
     */
    private List<String> foodPairings;

    /**
     * AI-generated occasion suggestions.
     */
    private List<String> occasions;

    /**
     * AI-generated fun facts about this wine or its origin.
     */
    private List<String> funFacts;

    /**
     * AI-generated serving recommendations.
     */
    private ServingRecommendations servingRecommendations;

    /**
     * The complete raw profile JSON for custom client-side processing.
     */
    private Map<String, Object> rawProfile;

    /**
     * Tasting notes structure.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TastingNotes {
        private String appearance;
        private String aroma;
        private String palate;
        private String finish;
    }

    /**
     * Serving recommendations structure.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServingRecommendations {
        private String temperature;
        private String decanting;
        private String glassType;
        private String storageTips;
    }
}


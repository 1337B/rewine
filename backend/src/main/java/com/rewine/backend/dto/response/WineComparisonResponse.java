package com.rewine.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response containing an AI-generated wine comparison.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "AI-generated wine comparison result")
public class WineComparisonResponse {

    /**
     * The ID of the first wine in the comparison.
     */
    @Schema(description = "ID of the first wine")
    private UUID wineAId;

    /**
     * The name of the first wine.
     */
    @Schema(description = "Name of the first wine")
    private String wineAName;

    /**
     * The ID of the second wine in the comparison.
     */
    @Schema(description = "ID of the second wine")
    private UUID wineBId;

    /**
     * The name of the second wine.
     */
    @Schema(description = "Name of the second wine")
    private String wineBName;

    /**
     * The language code of this comparison.
     */
    @Schema(description = "Language code of the comparison", example = "es-AR")
    private String language;

    /**
     * When this comparison was generated.
     */
    @Schema(description = "Timestamp when comparison was generated")
    private Instant generatedAt;

    /**
     * Whether this result was retrieved from cache.
     */
    @Schema(description = "Whether result was from cache")
    private boolean cached;

    /**
     * AI-generated overall comparison summary.
     */
    @Schema(description = "Overall comparison summary")
    private String summary;

    /**
     * Side-by-side comparison of key attributes.
     */
    @Schema(description = "Side-by-side attribute comparison")
    private AttributeComparison attributeComparison;

    /**
     * Key similarities between the wines.
     */
    @Schema(description = "Similarities between the wines")
    private List<String> similarities;

    /**
     * Key differences between the wines.
     */
    @Schema(description = "Differences between the wines")
    private List<String> differences;

    /**
     * Food pairing recommendations for each wine.
     */
    @Schema(description = "Food pairing comparison")
    private FoodPairingComparison foodPairings;

    /**
     * Occasion recommendations.
     */
    @Schema(description = "Occasion recommendations")
    private OccasionComparison occasions;

    /**
     * Price/value comparison if available.
     */
    @Schema(description = "Value assessment")
    private String valueAssessment;

    /**
     * AI recommendation on which wine to choose based on preferences.
     */
    @Schema(description = "Recommendation based on preferences")
    private RecommendationSummary recommendation;

    /**
     * The complete raw comparison JSON for custom processing.
     */
    @Schema(description = "Raw comparison JSON")
    private Map<String, Object> rawComparison;

    /**
     * Side-by-side attribute comparison structure.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Attribute comparison between wines")
    public static class AttributeComparison {
        @Schema(description = "Appearance comparison")
        private WineAttribute appearance;

        @Schema(description = "Aroma comparison")
        private WineAttribute aroma;

        @Schema(description = "Palate comparison")
        private WineAttribute palate;

        @Schema(description = "Finish comparison")
        private WineAttribute finish;

        @Schema(description = "Body comparison")
        private WineAttribute body;

        @Schema(description = "Acidity comparison")
        private WineAttribute acidity;

        @Schema(description = "Tannins comparison (for reds)")
        private WineAttribute tannins;
    }

    /**
     * Single attribute comparison between two wines.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Single attribute comparison")
    public static class WineAttribute {
        @Schema(description = "Description for wine A")
        private String wineA;

        @Schema(description = "Description for wine B")
        private String wineB;

        @Schema(description = "Comparative note")
        private String comparison;
    }

    /**
     * Food pairing comparison.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Food pairing comparison")
    public static class FoodPairingComparison {
        @Schema(description = "Best pairings for wine A")
        private List<String> wineA;

        @Schema(description = "Best pairings for wine B")
        private List<String> wineB;

        @Schema(description = "Pairings that work with both wines")
        private List<String> shared;
    }

    /**
     * Occasion comparison.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Occasion recommendations")
    public static class OccasionComparison {
        @Schema(description = "Best occasions for wine A")
        private List<String> wineA;

        @Schema(description = "Best occasions for wine B")
        private List<String> wineB;
    }

    /**
     * Recommendation summary.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Recommendation summary")
    public static class RecommendationSummary {
        @Schema(description = "Choose wine A if...")
        private String chooseWineAIf;

        @Schema(description = "Choose wine B if...")
        private String chooseWineBIf;

        @Schema(description = "Overall recommendation")
        private String overallNote;
    }
}


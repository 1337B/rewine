package com.rewine.backend.service.orchestration;

import com.rewine.backend.dto.response.WineComparisonResponse;

import java.util.UUID;

/**
 * Orchestrator for wine AI comparison generation and retrieval.
 * Handles caching logic to avoid redundant AI calls.
 */
public interface IWineComparisonOrchestrator {

    /**
     * Gets or generates an AI comparison for a wine pair.
     * If a cached comparison exists for the wine pair + language, returns it.
     * Otherwise, generates a new comparison, caches it, and returns it.
     *
     * Wine pair is normalized internally (wineA < wineB) to prevent duplicates.
     *
     * @param wineId1  the first wine ID
     * @param wineId2  the second wine ID
     * @param language the language code (e.g., "es-AR", "en-US")
     * @return the comparison response with cached flag
     */
    WineComparisonResponse getOrGenerateComparison(UUID wineId1, UUID wineId2, String language);

    /**
     * Forces regeneration of an AI comparison for a wine pair.
     * Overwrites any existing cached comparison.
     *
     * Wine pair is normalized internally (wineA < wineB) to prevent duplicates.
     *
     * @param wineId1  the first wine ID
     * @param wineId2  the second wine ID
     * @param language the language code
     * @return the newly generated comparison response
     */
    WineComparisonResponse forceRegenerateComparison(UUID wineId1, UUID wineId2, String language);

    /**
     * Checks if a comparison exists for a wine pair and language.
     *
     * Wine pair is normalized internally (wineA < wineB).
     *
     * @param wineId1  the first wine ID
     * @param wineId2  the second wine ID
     * @param language the language code
     * @return true if comparison exists
     */
    boolean comparisonExists(UUID wineId1, UUID wineId2, String language);
}


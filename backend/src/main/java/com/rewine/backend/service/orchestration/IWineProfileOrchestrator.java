package com.rewine.backend.service.orchestration;

import com.rewine.backend.dto.common.AiCacheStatusResponse;
import com.rewine.backend.dto.response.WineAiProfileResponse;

import java.util.UUID;

/**
 * Orchestrator for wine AI profile generation and retrieval.
 * Handles caching logic to avoid redundant AI calls.
 */
public interface IWineProfileOrchestrator {

    /**
     * Gets or generates an AI profile for a wine.
     * If a cached profile exists for the wine+language, returns it.
     * Otherwise, generates a new profile, caches it, and returns it.
     *
     * @param wineId   the wine ID
     * @param language the language code (e.g., "es-AR", "en-US")
     * @return the AI profile response
     */
    WineAiProfileResponse getOrGenerateProfile(UUID wineId, String language);

    /**
     * Forces regeneration of an AI profile for a wine.
     * Overwrites any existing cached profile.
     *
     * @param wineId   the wine ID
     * @param language the language code
     * @return the newly generated AI profile response
     */
    WineAiProfileResponse forceRegenerateProfile(UUID wineId, String language);

    /**
     * Gets the cache status for a wine's AI profile.
     *
     * @param wineId   the wine ID
     * @param language the language code to check (optional, checks all if null)
     * @return the cache status response
     */
    AiCacheStatusResponse getCacheStatus(UUID wineId, String language);

    /**
     * Checks if a profile exists for a wine and language.
     *
     * @param wineId   the wine ID
     * @param language the language code
     * @return true if profile exists
     */
    boolean profileExists(UUID wineId, String language);
}


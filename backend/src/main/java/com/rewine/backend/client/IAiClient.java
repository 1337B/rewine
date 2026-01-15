package com.rewine.backend.client;

import com.rewine.backend.model.entity.WineEntity;

import java.util.Map;

/**
 * Client interface for AI profile generation services.
 * Implementations can connect to various AI providers (OpenAI, Claude, etc.).
 */
public interface IAiClient {

    /**
     * Generates an AI profile for a wine.
     *
     * @param wine     the wine entity with all relevant data
     * @param language the target language code (e.g., "es-AR", "en-US")
     * @return a map containing the generated profile data
     */
    Map<String, Object> generateWineProfile(WineEntity wine, String language);

    /**
     * Generates an AI comparison between two wines.
     *
     * @param wineA    the first wine entity
     * @param wineB    the second wine entity
     * @param language the target language code (e.g., "es-AR", "en-US")
     * @return a map containing the generated comparison data
     */
    Map<String, Object> generateWineComparison(WineEntity wineA, WineEntity wineB, String language);

    /**
     * Checks if the AI service is available.
     *
     * @return true if the service is available
     */
    boolean isAvailable();

    /**
     * Gets the name of the AI provider.
     *
     * @return the provider name (e.g., "OpenAI", "Mock")
     */
    String getProviderName();
}


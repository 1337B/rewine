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


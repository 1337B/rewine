package com.rewine.backend.service.prompt;

import com.rewine.backend.model.entity.WineEntity;

/**
 * Service for building AI prompts for wine-related operations.
 * <p>
 * This is considered business logic because it encodes domain knowledge
 * about how wine information should be presented and what attributes
 * are important for AI-generated content.
 * </p>
 */
public interface IWinePromptService {

    /**
     * Builds a prompt for generating a wine profile.
     *
     * @param wine     the wine entity with all relevant data
     * @param language the target language code (e.g., "es-AR", "en-US")
     * @return the formatted prompt string
     */
    String buildProfilePrompt(WineEntity wine, String language);

    /**
     * Builds a prompt for comparing two wines.
     *
     * @param wineA    the first wine entity
     * @param wineB    the second wine entity
     * @param language the target language code (e.g., "es-AR", "en-US")
     * @return the formatted prompt string
     */
    String buildComparisonPrompt(WineEntity wineA, WineEntity wineB, String language);

    /**
     * Builds a system message for wine profile generation.
     *
     * @param language the target language code
     * @return the system message string
     */
    String buildProfileSystemMessage(String language);

    /**
     * Builds a system message for wine comparison.
     *
     * @param language the target language code
     * @return the system message string
     */
    String buildComparisonSystemMessage(String language);

    /**
     * Gets the JSON schema for wine profile responses.
     *
     * @return the JSON schema string
     */
    String getProfileJsonSchema();

    /**
     * Gets the JSON schema for wine comparison responses.
     *
     * @return the JSON schema string
     */
    String getComparisonJsonSchema();
}


package com.rewine.backend.service.orchestration;

import com.rewine.backend.client.IAiClient;
import com.rewine.backend.dto.common.AiCacheStatusResponse;
import com.rewine.backend.dto.response.WineAiProfileResponse;
import com.rewine.backend.exception.ResourceNotFoundException;
import com.rewine.backend.exception.ServiceUnavailableException;
import com.rewine.backend.model.entity.WineAiProfileEntity;
import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.enums.AiProfileStatus;
import com.rewine.backend.repository.IWineAiProfileRepository;
import com.rewine.backend.repository.IWineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the wine profile orchestrator.
 * Manages the lifecycle of AI-generated wine profiles with caching.
 */
@Service
public class WineProfileOrchestratorImpl implements IWineProfileOrchestrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineProfileOrchestratorImpl.class);
    private static final String DEFAULT_LANGUAGE = "es-AR";

    private final IWineRepository wineRepository;
    private final IWineAiProfileRepository wineAiProfileRepository;
    private final IAiClient aiClient;

    public WineProfileOrchestratorImpl(
            IWineRepository wineRepository,
            IWineAiProfileRepository wineAiProfileRepository,
            IAiClient aiClient) {
        this.wineRepository = wineRepository;
        this.wineAiProfileRepository = wineAiProfileRepository;
        this.aiClient = aiClient;
    }

    @Override
    @Transactional
    public WineAiProfileResponse getOrGenerateProfile(UUID wineId, String language) {
        String normalizedLanguage = normalizeLanguage(language);

        LOGGER.info("Getting or generating AI profile for wine: {}, language: {}", wineId, normalizedLanguage);

        // Step 1: Check if cached profile exists
        Optional<WineAiProfileEntity> existingProfile = wineAiProfileRepository
                .findByWineIdAndLanguage(wineId, normalizedLanguage);

        if (existingProfile.isPresent()) {
            LOGGER.info("Found cached AI profile for wine: {}, language: {}", wineId, normalizedLanguage);
            WineAiProfileResponse response = mapToResponse(existingProfile.get());
            response.setCached(true);
            return response;
        }

        // Step 2: Profile doesn't exist, need to generate
        LOGGER.info("No cached profile found. Generating new AI profile for wine: {}", wineId);
        return generateAndPersistProfile(wineId, normalizedLanguage);
    }

    @Override
    @Transactional
    public WineAiProfileResponse forceRegenerateProfile(UUID wineId, String language) {
        String normalizedLanguage = normalizeLanguage(language);

        LOGGER.info("Force regenerating AI profile for wine: {}, language: {}", wineId, normalizedLanguage);

        // Delete existing profile if present
        Optional<WineAiProfileEntity> existingProfile = wineAiProfileRepository
                .findByWineIdAndLanguage(wineId, normalizedLanguage);

        if (existingProfile.isPresent()) {
            LOGGER.debug("Deleting existing profile for wine: {}, language: {}", wineId, normalizedLanguage);
            wineAiProfileRepository.delete(existingProfile.get());
        }

        // Generate new profile
        return generateAndPersistProfile(wineId, normalizedLanguage);
    }

    @Override
    @Transactional(readOnly = true)
    public AiCacheStatusResponse getCacheStatus(UUID wineId, String language) {
        LOGGER.debug("Checking AI profile cache status for wine: {}", wineId);

        // Verify wine exists
        if (!wineRepository.existsById(wineId)) {
            throw new ResourceNotFoundException("Wine", "id", wineId);
        }

        String normalizedLanguage = normalizeLanguage(language);
        boolean hasRequestedLanguage = wineAiProfileRepository
                .existsByWineIdAndLanguage(wineId, normalizedLanguage);

        // Get list of available languages for this wine
        List<String> availableLanguages = new ArrayList<>();
        if (hasRequestedLanguage) {
            availableLanguages.add(normalizedLanguage);
        }

        AiProfileStatus status = hasRequestedLanguage
                ? AiProfileStatus.GENERATED
                : AiProfileStatus.NOT_REQUESTED;

        // If profile exists, get the generation date
        Optional<WineAiProfileEntity> profile = hasRequestedLanguage
                ? wineAiProfileRepository.findByWineIdAndLanguage(wineId, normalizedLanguage)
                : Optional.empty();

        return AiCacheStatusResponse.builder()
                .wineId(wineId)
                .status(status)
                .generatedAt(profile.map(WineAiProfileEntity::getCreatedAt).orElse(null))
                .availableLanguages(availableLanguages)
                .hasRequestedLanguage(hasRequestedLanguage)
                .requestedLanguage(normalizedLanguage)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean profileExists(UUID wineId, String language) {
        String normalizedLanguage = normalizeLanguage(language);
        boolean exists = wineAiProfileRepository.existsByWineIdAndLanguage(wineId, normalizedLanguage);
        LOGGER.debug("Profile exists check for wine: {}, language: {} -> {}", wineId, normalizedLanguage, exists);
        return exists;
    }

    /**
     * Generates a new AI profile and persists it to the database.
     */
    private WineAiProfileResponse generateAndPersistProfile(UUID wineId, String language) {
        // Step 1: Fetch wine with all needed data
        WineEntity wine = wineRepository.findByIdWithWinery(wineId)
                .orElseThrow(() -> new ResourceNotFoundException("Wine", "id", wineId));

        LOGGER.info("Fetched wine for AI generation: {} ({})", wine.getName(), wine.getId());

        // Step 2: Check if AI service is available
        if (!aiClient.isAvailable()) {
            LOGGER.error("AI service is not available. Provider: {}", aiClient.getProviderName());
            throw ServiceUnavailableException.forAi();
        }

        LOGGER.info("Using AI provider: {}", aiClient.getProviderName());

        // Step 3: Generate profile via AI client
        LOGGER.info("Calling AI client to generate profile...");
        Map<String, Object> profileJson = aiClient.generateWineProfile(wine, language);
        LOGGER.info("AI profile generated successfully with {} fields", profileJson.size());

        // Step 4: Create and persist the entity
        WineAiProfileEntity profileEntity = WineAiProfileEntity.builder()
                .wine(wine)
                .language(language)
                .profileJson(profileJson)
                .build();

        WineAiProfileEntity savedProfile = wineAiProfileRepository.save(profileEntity);
        LOGGER.info("Persisted AI profile with ID: {} for wine: {}", savedProfile.getId(), wineId);

        // Step 5: Map to response (newly generated, not cached)
        WineAiProfileResponse response = mapToResponse(savedProfile, wine.getName());
        response.setCached(false);
        return response;
    }

    /**
     * Maps a profile entity to the response DTO.
     */
    private WineAiProfileResponse mapToResponse(WineAiProfileEntity entity) {
        String wineName = Objects.nonNull(entity.getWine())
                ? entity.getWine().getName()
                : null;
        return mapToResponse(entity, wineName);
    }

    /**
     * Maps a profile entity to the response DTO with wine name.
     */
    @SuppressWarnings("unchecked")
    private WineAiProfileResponse mapToResponse(WineAiProfileEntity entity, String wineName) {
        Map<String, Object> json = entity.getProfileJson();

        WineAiProfileResponse.WineAiProfileResponseBuilder builder = WineAiProfileResponse.builder()
                .wineId(entity.getWine().getId())
                .wineName(wineName)
                .language(entity.getLanguage())
                .generatedAt(entity.getCreatedAt())
                .rawProfile(json);

        // Extract structured fields from JSON
        if (Objects.nonNull(json)) {
            // Summary
            if (json.containsKey("summary")) {
                builder.summary((String) json.get("summary"));
            }

            // Tasting notes
            if (json.containsKey("tastingNotes")) {
                Map<String, String> tastingNotesMap = (Map<String, String>) json.get("tastingNotes");
                builder.tastingNotes(WineAiProfileResponse.TastingNotes.builder()
                        .appearance(tastingNotesMap.get("appearance"))
                        .aroma(tastingNotesMap.get("aroma"))
                        .palate(tastingNotesMap.get("palate"))
                        .finish(tastingNotesMap.get("finish"))
                        .build());
            }

            // Food pairings
            if (json.containsKey("foodPairings")) {
                builder.foodPairings((List<String>) json.get("foodPairings"));
            }

            // Occasions
            if (json.containsKey("occasions")) {
                builder.occasions((List<String>) json.get("occasions"));
            }

            // Fun facts
            if (json.containsKey("funFacts")) {
                builder.funFacts((List<String>) json.get("funFacts"));
            }

            // Serving recommendations
            if (json.containsKey("servingRecommendations")) {
                Map<String, String> servingMap = (Map<String, String>) json.get("servingRecommendations");
                builder.servingRecommendations(WineAiProfileResponse.ServingRecommendations.builder()
                        .temperature(servingMap.get("temperature"))
                        .decanting(servingMap.get("decanting"))
                        .glassType(servingMap.get("glassType"))
                        .storageTips(servingMap.get("storageTips"))
                        .build());
            }
        }

        return builder.build();
    }

    /**
     * Normalizes the language code.
     */
    private String normalizeLanguage(String language) {
        if (Objects.isNull(language) || language.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        return language.trim();
    }
}


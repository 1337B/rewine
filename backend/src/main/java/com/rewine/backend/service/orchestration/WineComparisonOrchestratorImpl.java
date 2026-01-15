package com.rewine.backend.service.orchestration;

import com.rewine.backend.client.IAiClient;
import com.rewine.backend.dto.response.WineComparisonResponse;
import com.rewine.backend.exception.ResourceNotFoundException;
import com.rewine.backend.model.entity.WineComparisonEntity;
import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.repository.IWineComparisonRepository;
import com.rewine.backend.repository.IWineRepository;
import com.rewine.backend.utils.validation.IWinePairNormalizer;
import com.rewine.backend.utils.validation.IWinePairNormalizer.NormalizedWinePair;
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
 * Implementation of the wine comparison orchestrator.
 * Manages the lifecycle of AI-generated wine comparisons with caching.
 */
@Service
public class WineComparisonOrchestratorImpl implements IWineComparisonOrchestrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineComparisonOrchestratorImpl.class);
    private static final String DEFAULT_LANGUAGE = "es-AR";

    private final IWineRepository wineRepository;
    private final IWineComparisonRepository wineComparisonRepository;
    private final IAiClient aiClient;
    private final IWinePairNormalizer winePairNormalizer;

    public WineComparisonOrchestratorImpl(
            IWineRepository wineRepository,
            IWineComparisonRepository wineComparisonRepository,
            IAiClient aiClient,
            IWinePairNormalizer winePairNormalizer) {
        this.wineRepository = wineRepository;
        this.wineComparisonRepository = wineComparisonRepository;
        this.aiClient = aiClient;
        this.winePairNormalizer = winePairNormalizer;
    }

    @Override
    @Transactional
    public WineComparisonResponse getOrGenerateComparison(UUID wineId1, UUID wineId2, String language) {
        String normalizedLanguage = normalizeLanguage(language);

        LOGGER.info("Getting or generating AI comparison for wines: {} vs {}, language: {}",
                wineId1, wineId2, normalizedLanguage);

        // Step 1: Normalize the wine pair
        NormalizedWinePair pair = winePairNormalizer.normalize(wineId1, wineId2);
        LOGGER.debug("Normalized wine pair: wineA={}, wineB={}, wasSwapped={}",
                pair.wineAId(), pair.wineBId(), pair.wasSwapped());

        // Step 2: Check if cached comparison exists
        Optional<WineComparisonEntity> existingComparison = wineComparisonRepository
                .findByWineAIdAndWineBIdAndLanguageWithWines(
                        pair.wineAId(), pair.wineBId(), normalizedLanguage);

        if (existingComparison.isPresent()) {
            LOGGER.info("Found cached AI comparison for wine pair: {} vs {}, language: {}",
                    pair.wineAId(), pair.wineBId(), normalizedLanguage);
            return mapToResponse(existingComparison.get(), true);
        }

        // Step 3: Comparison doesn't exist, need to generate
        LOGGER.info("No cached comparison found. Generating new AI comparison for wines: {} vs {}",
                pair.wineAId(), pair.wineBId());
        return generateAndPersistComparison(pair, normalizedLanguage);
    }

    @Override
    @Transactional
    public WineComparisonResponse forceRegenerateComparison(UUID wineId1, UUID wineId2, String language) {
        String normalizedLanguage = normalizeLanguage(language);

        LOGGER.info("Force regenerating AI comparison for wines: {} vs {}, language: {}",
                wineId1, wineId2, normalizedLanguage);

        // Step 1: Normalize the wine pair
        NormalizedWinePair pair = winePairNormalizer.normalize(wineId1, wineId2);

        // Step 2: Delete existing comparison if present
        Optional<WineComparisonEntity> existingComparison = wineComparisonRepository
                .findByWineAIdAndWineBIdAndLanguage(pair.wineAId(), pair.wineBId(), normalizedLanguage);

        if (existingComparison.isPresent()) {
            LOGGER.debug("Deleting existing comparison for wine pair: {} vs {}, language: {}",
                    pair.wineAId(), pair.wineBId(), normalizedLanguage);
            wineComparisonRepository.delete(existingComparison.get());
        }

        // Step 3: Generate new comparison
        return generateAndPersistComparison(pair, normalizedLanguage);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean comparisonExists(UUID wineId1, UUID wineId2, String language) {
        String normalizedLanguage = normalizeLanguage(language);
        NormalizedWinePair pair = winePairNormalizer.normalize(wineId1, wineId2);

        boolean exists = wineComparisonRepository
                .existsByWineAIdAndWineBIdAndLanguage(pair.wineAId(), pair.wineBId(), normalizedLanguage);

        LOGGER.debug("Comparison exists check for wines: {} vs {}, language: {} -> {}",
                pair.wineAId(), pair.wineBId(), normalizedLanguage, exists);
        return exists;
    }

    /**
     * Generates a new AI comparison and persists it to the database.
     */
    private WineComparisonResponse generateAndPersistComparison(NormalizedWinePair pair, String language) {
        // Step 1: Fetch both wines with all needed data
        WineEntity wineA = wineRepository.findByIdWithWinery(pair.wineAId())
                .orElseThrow(() -> new ResourceNotFoundException("Wine", "id", pair.wineAId()));
        WineEntity wineB = wineRepository.findByIdWithWinery(pair.wineBId())
                .orElseThrow(() -> new ResourceNotFoundException("Wine", "id", pair.wineBId()));

        LOGGER.info("Fetched wines for AI comparison: {} ({}) vs {} ({})",
                wineA.getName(), wineA.getId(), wineB.getName(), wineB.getId());

        // Step 2: Check if AI service is available
        if (!aiClient.isAvailable()) {
            LOGGER.error("AI service is not available. Provider: {}", aiClient.getProviderName());
            throw new IllegalStateException("AI service is currently unavailable");
        }

        LOGGER.info("Using AI provider: {}", aiClient.getProviderName());

        // Step 3: Generate comparison via AI client
        LOGGER.info("Calling AI client to generate comparison...");
        Map<String, Object> comparisonJson = aiClient.generateWineComparison(wineA, wineB, language);
        LOGGER.info("AI comparison generated successfully with {} fields", comparisonJson.size());

        // Step 4: Create and persist the entity
        WineComparisonEntity comparisonEntity = WineComparisonEntity.builder()
                .wineA(wineA)
                .wineB(wineB)
                .language(language)
                .comparisonJson(comparisonJson)
                .build();

        WineComparisonEntity savedComparison = wineComparisonRepository.save(comparisonEntity);
        LOGGER.info("Persisted AI comparison with ID: {} for wines: {} vs {}",
                savedComparison.getId(), pair.wineAId(), pair.wineBId());

        // Step 5: Map to response (not cached since we just generated it)
        return mapToResponse(savedComparison, wineA.getName(), wineB.getName(), false);
    }

    /**
     * Maps a comparison entity to the response DTO.
     */
    private WineComparisonResponse mapToResponse(WineComparisonEntity entity, boolean cached) {
        String wineAName = Objects.nonNull(entity.getWineA()) ? entity.getWineA().getName() : null;
        String wineBName = Objects.nonNull(entity.getWineB()) ? entity.getWineB().getName() : null;
        return mapToResponse(entity, wineAName, wineBName, cached);
    }

    /**
     * Maps a comparison entity to the response DTO with wine names.
     */
    @SuppressWarnings("unchecked")
    private WineComparisonResponse mapToResponse(
            WineComparisonEntity entity, String wineAName, String wineBName, boolean cached) {

        Map<String, Object> json = entity.getComparisonJson();

        WineComparisonResponse.WineComparisonResponseBuilder builder = WineComparisonResponse.builder()
                .wineAId(entity.getWineA().getId())
                .wineAName(wineAName)
                .wineBId(entity.getWineB().getId())
                .wineBName(wineBName)
                .language(entity.getLanguage())
                .generatedAt(entity.getCreatedAt())
                .cached(cached)
                .rawComparison(json);

        // Extract structured fields from JSON
        if (Objects.nonNull(json)) {
            // Summary
            if (json.containsKey("summary")) {
                builder.summary((String) json.get("summary"));
            }

            // Attribute comparison
            if (json.containsKey("attributeComparison")) {
                Map<String, Object> attrMap = (Map<String, Object>) json.get("attributeComparison");
                builder.attributeComparison(mapAttributeComparison(attrMap));
            }

            // Similarities
            if (json.containsKey("similarities")) {
                builder.similarities((List<String>) json.get("similarities"));
            }

            // Differences
            if (json.containsKey("differences")) {
                builder.differences((List<String>) json.get("differences"));
            }

            // Food pairings
            if (json.containsKey("foodPairings")) {
                Map<String, Object> pairingsMap = (Map<String, Object>) json.get("foodPairings");
                builder.foodPairings(mapFoodPairings(pairingsMap));
            }

            // Occasions
            if (json.containsKey("occasions")) {
                Map<String, Object> occasionsMap = (Map<String, Object>) json.get("occasions");
                builder.occasions(mapOccasions(occasionsMap));
            }

            // Value assessment
            if (json.containsKey("valueAssessment")) {
                builder.valueAssessment((String) json.get("valueAssessment"));
            }

            // Recommendation
            if (json.containsKey("recommendation")) {
                Map<String, String> recMap = (Map<String, String>) json.get("recommendation");
                builder.recommendation(mapRecommendation(recMap));
            }
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private WineComparisonResponse.AttributeComparison mapAttributeComparison(Map<String, Object> attrMap) {
        WineComparisonResponse.AttributeComparison.AttributeComparisonBuilder builder =
                WineComparisonResponse.AttributeComparison.builder();

        if (attrMap.containsKey("appearance")) {
            builder.appearance(mapWineAttribute((Map<String, String>) attrMap.get("appearance")));
        }
        if (attrMap.containsKey("aroma")) {
            builder.aroma(mapWineAttribute((Map<String, String>) attrMap.get("aroma")));
        }
        if (attrMap.containsKey("palate")) {
            builder.palate(mapWineAttribute((Map<String, String>) attrMap.get("palate")));
        }
        if (attrMap.containsKey("finish")) {
            builder.finish(mapWineAttribute((Map<String, String>) attrMap.get("finish")));
        }
        if (attrMap.containsKey("body")) {
            builder.body(mapWineAttribute((Map<String, String>) attrMap.get("body")));
        }
        if (attrMap.containsKey("acidity")) {
            builder.acidity(mapWineAttribute((Map<String, String>) attrMap.get("acidity")));
        }
        if (attrMap.containsKey("tannins")) {
            builder.tannins(mapWineAttribute((Map<String, String>) attrMap.get("tannins")));
        }

        return builder.build();
    }

    private WineComparisonResponse.WineAttribute mapWineAttribute(Map<String, String> attrMap) {
        return WineComparisonResponse.WineAttribute.builder()
                .wineA(attrMap.get("wineA"))
                .wineB(attrMap.get("wineB"))
                .comparison(attrMap.get("comparison"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private WineComparisonResponse.FoodPairingComparison mapFoodPairings(Map<String, Object> pairingsMap) {
        return WineComparisonResponse.FoodPairingComparison.builder()
                .wineA(getStringList(pairingsMap, "wineA"))
                .wineB(getStringList(pairingsMap, "wineB"))
                .shared(getStringList(pairingsMap, "shared"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private WineComparisonResponse.OccasionComparison mapOccasions(Map<String, Object> occasionsMap) {
        return WineComparisonResponse.OccasionComparison.builder()
                .wineA(getStringList(occasionsMap, "wineA"))
                .wineB(getStringList(occasionsMap, "wineB"))
                .build();
    }

    private WineComparisonResponse.RecommendationSummary mapRecommendation(Map<String, String> recMap) {
        return WineComparisonResponse.RecommendationSummary.builder()
                .chooseWineAIf(recMap.get("chooseWineAIf"))
                .chooseWineBIf(recMap.get("chooseWineBIf"))
                .overallNote(recMap.get("overallNote"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof List) {
                return (List<String>) value;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Normalizes the language code, defaulting to DEFAULT_LANGUAGE if null or empty.
     */
    private String normalizeLanguage(String language) {
        if (Objects.isNull(language) || language.trim().isEmpty()) {
            return DEFAULT_LANGUAGE;
        }
        return language.trim();
    }
}


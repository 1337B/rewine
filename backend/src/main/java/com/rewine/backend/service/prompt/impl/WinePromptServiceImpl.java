package com.rewine.backend.service.prompt.impl;

import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.entity.WineryEntity;
import com.rewine.backend.service.prompt.IWinePromptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of the wine prompt service.
 * <p>
 * Contains business logic for building AI prompts with wine domain knowledge.
 * This includes understanding what wine attributes matter, how to describe them,
 * and what makes a good prompt for wine-related AI generation.
 * </p>
 */
@Service
public class WinePromptServiceImpl implements IWinePromptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinePromptServiceImpl.class);

    private static final String PROFILE_JSON_SCHEMA = """
            {
              "summary": "string - A comprehensive description of the wine",
              "tastingNotes": {
                "appearance": "string - Visual characteristics",
                "aroma": "string - Nose/bouquet description",
                "palate": "string - Taste and mouthfeel",
                "finish": "string - Aftertaste description"
              },
              "foodPairings": ["string array - Recommended food pairings"],
              "occasions": ["string array - Suggested occasions"],
              "funFacts": ["string array - Interesting facts about the wine or region"],
              "servingRecommendations": {
                "temperature": "string - Serving temperature",
                "decanting": "string - Decanting recommendations",
                "glassType": "string - Recommended glass type",
                "storageTips": "string - Storage recommendations"
              }
            }
            """;

    private static final String COMPARISON_JSON_SCHEMA = """
            {
              "summary": "string - Overall comparison summary",
              "attributeComparison": {
                "appearance": {
                  "wineA": "string",
                  "wineB": "string",
                  "comparison": "string"
                },
                "aroma": {
                  "wineA": "string",
                  "wineB": "string",
                  "comparison": "string"
                },
                "palate": {
                  "wineA": "string",
                  "wineB": "string",
                  "comparison": "string"
                },
                "finish": {
                  "wineA": "string",
                  "wineB": "string",
                  "comparison": "string"
                }
              },
              "similarities": ["string array - Key similarities"],
              "differences": ["string array - Key differences"],
              "foodPairings": {
                "wineA": ["string array"],
                "wineB": ["string array"],
                "shared": ["string array"]
              },
              "occasions": {
                "wineA": ["string array"],
                "wineB": ["string array"]
              },
              "valueAssessment": "string - Price/quality assessment",
              "recommendation": {
                "chooseWineAIf": "string",
                "chooseWineBIf": "string",
                "overallNote": "string"
              }
            }
            """;

    @Override
    public String buildProfilePrompt(WineEntity wine, String language) {
        LOGGER.debug("Building profile prompt for wine: {} (ID: {}), language: {}",
                wine.getName(), wine.getId(), language);

        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate a detailed wine profile for the following wine:\n\n");
        prompt.append("## Wine Information\n");
        prompt.append("- **Name**: ").append(wine.getName()).append("\n");

        if (Objects.nonNull(wine.getWineType())) {
            prompt.append("- **Type**: ").append(wine.getWineType().name()).append("\n");
        }

        if (Objects.nonNull(wine.getVintage())) {
            prompt.append("- **Vintage**: ").append(wine.getVintage()).append("\n");
        }

        if (Objects.nonNull(wine.getStyle())) {
            prompt.append("- **Style**: ").append(wine.getStyle()).append("\n");
        }

        if (Objects.nonNull(wine.getGrapes()) && !wine.getGrapes().isEmpty()) {
            prompt.append("- **Grapes**: ").append(String.join(", ", wine.getGrapes())).append("\n");
        }

        if (Objects.nonNull(wine.getAlcoholContent())) {
            prompt.append("- **Alcohol Content**: ").append(wine.getAlcoholContent()).append("%\n");
        }

        // Add winery information if available
        WineryEntity winery = wine.getWinery();
        if (Objects.nonNull(winery)) {
            prompt.append("\n## Winery Information\n");
            prompt.append("- **Winery**: ").append(winery.getName()).append("\n");

            if (Objects.nonNull(winery.getRegion())) {
                prompt.append("- **Region**: ").append(winery.getRegion()).append("\n");
            }

            if (Objects.nonNull(winery.getCountry())) {
                prompt.append("- **Country**: ").append(winery.getCountry()).append("\n");
            }
        }

        // Add existing description if available
        String description = getDescriptionForLanguage(wine, language);
        if (Objects.nonNull(description) && !description.isBlank()) {
            prompt.append("\n## Existing Description\n");
            prompt.append(description).append("\n");
        }

        prompt.append("\n## Output Requirements\n");
        prompt.append("Please provide a response in **").append(getLanguageName(language)).append("**.\n");
        prompt.append("The response must be valid JSON matching this schema:\n");
        prompt.append("```json\n").append(PROFILE_JSON_SCHEMA).append("```\n");
        prompt.append("\nRespond ONLY with the JSON object, no additional text.\n");

        return prompt.toString();
    }

    @Override
    public String buildComparisonPrompt(WineEntity wineA, WineEntity wineB, String language) {
        LOGGER.debug("Building comparison prompt for wines: {} vs {} (IDs: {} vs {}), language: {}",
                wineA.getName(), wineB.getName(), wineA.getId(), wineB.getId(), language);

        StringBuilder prompt = new StringBuilder();

        prompt.append("Compare the following two wines:\n\n");

        // Wine A details
        prompt.append("## Wine A: ").append(wineA.getName()).append("\n");
        appendWineDetails(prompt, wineA);

        // Wine B details
        prompt.append("\n## Wine B: ").append(wineB.getName()).append("\n");
        appendWineDetails(prompt, wineB);

        prompt.append("\n## Output Requirements\n");
        prompt.append("Please provide a detailed comparison in **").append(getLanguageName(language)).append("**.\n");
        prompt.append("The response must be valid JSON matching this schema:\n");
        prompt.append("```json\n").append(COMPARISON_JSON_SCHEMA).append("```\n");
        prompt.append("\nRespond ONLY with the JSON object, no additional text.\n");

        return prompt.toString();
    }

    @Override
    public String buildProfileSystemMessage(String language) {
        String languageName = getLanguageName(language);

        return String.format("""
                You are a world-class sommelier and wine expert with decades of experience.
                Your task is to create engaging, informative wine profiles that help consumers
                appreciate and understand wines better.
                
                Guidelines:
                - Write in %s
                - Be descriptive and evocative, but accurate
                - Use professional wine terminology appropriately
                - Make content accessible to both novices and experts
                - Be creative with food pairings and occasion suggestions
                - Include interesting historical or regional context when relevant
                - Always respond with valid JSON only
                """, languageName);
    }

    @Override
    public String buildComparisonSystemMessage(String language) {
        String languageName = getLanguageName(language);

        return String.format("""
                You are a world-class sommelier and wine expert with decades of experience.
                Your task is to provide insightful wine comparisons that help consumers
                understand the differences and similarities between wines.
                
                Guidelines:
                - Write in %s
                - Be fair and objective in comparisons
                - Highlight both similarities and differences
                - Provide practical recommendations for when to choose each wine
                - Consider price/value when relevant
                - Use professional wine terminology appropriately
                - Make content accessible to both novices and experts
                - Always respond with valid JSON only
                """, languageName);
    }

    @Override
    public String getProfileJsonSchema() {
        return PROFILE_JSON_SCHEMA;
    }

    @Override
    public String getComparisonJsonSchema() {
        return COMPARISON_JSON_SCHEMA;
    }

    /**
     * Appends wine details to the prompt builder.
     */
    private void appendWineDetails(StringBuilder prompt, WineEntity wine) {
        if (Objects.nonNull(wine.getWineType())) {
            prompt.append("- **Type**: ").append(wine.getWineType().name()).append("\n");
        }

        if (Objects.nonNull(wine.getVintage())) {
            prompt.append("- **Vintage**: ").append(wine.getVintage()).append("\n");
        }

        if (Objects.nonNull(wine.getStyle())) {
            prompt.append("- **Style**: ").append(wine.getStyle()).append("\n");
        }

        if (Objects.nonNull(wine.getGrapes()) && !wine.getGrapes().isEmpty()) {
            prompt.append("- **Grapes**: ").append(String.join(", ", wine.getGrapes())).append("\n");
        }

        if (Objects.nonNull(wine.getAlcoholContent())) {
            prompt.append("- **Alcohol**: ").append(wine.getAlcoholContent()).append("%\n");
        }

        WineryEntity winery = wine.getWinery();
        if (Objects.nonNull(winery)) {
            prompt.append("- **Winery**: ").append(winery.getName()).append("\n");
            if (Objects.nonNull(winery.getRegion())) {
                prompt.append("- **Region**: ").append(winery.getRegion()).append("\n");
            }
        }
    }

    /**
     * Gets the description in the target language, falling back to available.
     */
    private String getDescriptionForLanguage(WineEntity wine, String language) {
        if (language.startsWith("es")) {
            return Objects.nonNull(wine.getDescriptionEs())
                    ? wine.getDescriptionEs()
                    : wine.getDescriptionEn();
        } else {
            return Objects.nonNull(wine.getDescriptionEn())
                    ? wine.getDescriptionEn()
                    : wine.getDescriptionEs();
        }
    }

    /**
     * Gets the full language name from the code.
     */
    private String getLanguageName(String languageCode) {
        if (Objects.isNull(languageCode)) {
            return "Spanish (Argentina)";
        }

        return switch (languageCode.toLowerCase()) {
            case "en-us", "en" -> "English (United States)";
            case "en-gb" -> "English (United Kingdom)";
            case "es-ar" -> "Spanish (Argentina)";
            case "es-es" -> "Spanish (Spain)";
            case "es" -> "Spanish";
            default -> "Spanish (Argentina)";
        };
    }
}


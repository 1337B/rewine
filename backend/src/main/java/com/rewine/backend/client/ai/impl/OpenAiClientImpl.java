package com.rewine.backend.client.ai.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewine.backend.client.IAiClient;
import com.rewine.backend.client.http.IHttpClientFactory;
import com.rewine.backend.configuration.properties.AiProperties;
import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.service.prompt.IWinePromptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * OpenAI implementation of the AI client.
 * Falls back to mock responses when API key is not configured.
 */
@Component
@ConditionalOnProperty(
        name = "rewine.ai.provider",
        havingValue = "openai"
)
public class OpenAiClientImpl implements IAiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAiClientImpl.class);
    private static final String CHAT_COMPLETIONS_ENDPOINT = "/chat/completions";
    private static final int JSON_MARKDOWN_PREFIX_LENGTH = 7;
    private static final int CODE_BLOCK_PREFIX_LENGTH = 3;
    private static final int RETRY_BASE_DELAY_MS = 1000;

    private final AiProperties aiProperties;
    private final IWinePromptService winePromptService;
    private final IHttpClientFactory httpClientFactory;
    private final ObjectMapper objectMapper;
    private final MockResponseGenerator mockResponseGenerator;

    private RestClient restClient;

    public OpenAiClientImpl(
            AiProperties aiProperties,
            IWinePromptService winePromptService,
            IHttpClientFactory httpClientFactory,
            ObjectMapper objectMapper) {
        this.aiProperties = aiProperties;
        this.winePromptService = winePromptService;
        this.httpClientFactory = httpClientFactory;
        this.objectMapper = objectMapper;
        this.mockResponseGenerator = new MockResponseGenerator();

        initializeClient();
    }

    private void initializeClient() {
        if (aiProperties.isOpenAiConfigured()) {
            LOGGER.info("Initializing OpenAI client with base URL: {}",
                    aiProperties.getOpenai().getBaseUrl());
            this.restClient = httpClientFactory.createAiClient(
                    aiProperties.getOpenai().getBaseUrl()
            );
        } else {
            LOGGER.warn("OpenAI API key not configured. Will use mock responses.");
            this.restClient = null;
        }
    }

    @Override
    public Map<String, Object> generateWineProfile(WineEntity wine, String language) {
        LOGGER.info("Generating wine profile for: {} (ID: {}), language: {}",
                wine.getName(), wine.getId(), language);

        // Fall back to mock if not configured
        if (!aiProperties.isOpenAiConfigured()) {
            LOGGER.info("Using mock response for wine profile (API key not configured)");
            return mockResponseGenerator.generateMockProfile(wine, language);
        }

        try {
            String systemMessage = winePromptService.buildProfileSystemMessage(language);
            String userPrompt = winePromptService.buildProfilePrompt(wine, language);

            String response = callOpenAiApi(systemMessage, userPrompt);
            Map<String, Object> parsedResponse = parseJsonResponse(response);

            LOGGER.info("Successfully generated AI profile for wine: {}", wine.getName());
            return parsedResponse;

        } catch (Exception e) {
            LOGGER.error("Failed to generate AI profile for wine: {}. Falling back to mock. Error: {}",
                    wine.getName(), e.getMessage());
            return mockResponseGenerator.generateMockProfile(wine, language);
        }
    }

    @Override
    public Map<String, Object> generateWineComparison(WineEntity wineA, WineEntity wineB, String language) {
        LOGGER.info("Generating wine comparison for: {} vs {} (IDs: {} vs {}), language: {}",
                wineA.getName(), wineB.getName(), wineA.getId(), wineB.getId(), language);

        // Fall back to mock if not configured
        if (!aiProperties.isOpenAiConfigured()) {
            LOGGER.info("Using mock response for wine comparison (API key not configured)");
            return mockResponseGenerator.generateMockComparison(wineA, wineB, language);
        }

        try {
            String systemMessage = winePromptService.buildComparisonSystemMessage(language);
            String userPrompt = winePromptService.buildComparisonPrompt(wineA, wineB, language);

            String response = callOpenAiApi(systemMessage, userPrompt);
            Map<String, Object> parsedResponse = parseJsonResponse(response);

            LOGGER.info("Successfully generated AI comparison for wines: {} vs {}",
                    wineA.getName(), wineB.getName());
            return parsedResponse;

        } catch (Exception e) {
            LOGGER.error("Failed to generate AI comparison for wines: {} vs {}. Falling back to mock. Error: {}",
                    wineA.getName(), wineB.getName(), e.getMessage());
            return mockResponseGenerator.generateMockComparison(wineA, wineB, language);
        }
    }

    @Override
    public boolean isAvailable() {
        return aiProperties.isEnabled();
    }

    @Override
    public String getProviderName() {
        return aiProperties.isOpenAiConfigured() ? "OpenAI" : "OpenAI (Mock Fallback)";
    }

    /**
     * Calls the OpenAI Chat Completions API.
     */
    private String callOpenAiApi(String systemMessage, String userPrompt) {
        LOGGER.debug("Calling OpenAI API with model: {}", aiProperties.getOpenai().getModel());

        Map<String, Object> requestBody = buildRequestBody(systemMessage, userPrompt);
        int maxRetries = aiProperties.getHttp().getMaxRetries();
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                String responseBody = restClient.post()
                        .uri(CHAT_COMPLETIONS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + aiProperties.getOpenai().getApiKey())
                        .body(requestBody)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            LOGGER.error("OpenAI API client error: {} - {}",
                                    response.getStatusCode(), response.getStatusText());
                            throw new OpenAiApiException("Client error: " + response.getStatusCode());
                        })
                        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                            LOGGER.error("OpenAI API server error: {} - {}",
                                    response.getStatusCode(), response.getStatusText());
                            throw new OpenAiApiException("Server error: " + response.getStatusCode());
                        })
                        .body(String.class);

                return extractContentFromResponse(responseBody);

            } catch (JsonProcessingException e) {
                LOGGER.error("Failed to parse OpenAI response: {}", e.getMessage());
                throw new OpenAiApiException("Failed to parse OpenAI API response", e);
            } catch (OpenAiApiException | RestClientException e) {
                lastException = e;
                attempt++;

                if (attempt <= maxRetries && isRetryableError(e)) {
                    LOGGER.warn("OpenAI API call failed (attempt {}/{}). Retrying... Error: {}",
                            attempt, maxRetries + 1, e.getMessage());
                    waitBeforeRetry(attempt);
                } else {
                    break;
                }
            }
        }

        throw new RuntimeException("OpenAI API call failed after " + (maxRetries + 1) + " attempts",
                lastException);
    }

    /**
     * Builds the request body for the OpenAI API.
     */
    private Map<String, Object> buildRequestBody(String systemMessage, String userPrompt) {
        AiProperties.OpenAiConfig openaiConfig = aiProperties.getOpenai();

        Map<String, Object> body = new HashMap<>();
        body.put("model", openaiConfig.getModel());
        body.put("max_tokens", openaiConfig.getMaxTokens());
        body.put("temperature", openaiConfig.getTemperature());

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemMessage),
                Map.of("role", "user", "content", userPrompt)
        );
        body.put("messages", messages);

        // Request JSON response format
        body.put("response_format", Map.of("type", "json_object"));

        return body;
    }

    /**
     * Extracts the content from the OpenAI API response.
     */
    private String extractContentFromResponse(String responseBody) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(responseBody);

        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            throw new OpenAiApiException("No choices in OpenAI response");
        }

        JsonNode content = choices.get(0).path("message").path("content");
        if (content.isMissingNode() || content.isNull()) {
            throw new OpenAiApiException("No content in OpenAI response");
        }

        String contentStr = content.asText();
        LOGGER.debug("Extracted content from OpenAI response ({} characters)", contentStr.length());
        return contentStr;
    }

    /**
     * Parses the JSON response from OpenAI.
     */
    private Map<String, Object> parseJsonResponse(String jsonResponse) {
        try {
            // Clean up potential markdown code blocks
            String cleanJson = jsonResponse.trim();
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.substring(JSON_MARKDOWN_PREFIX_LENGTH);
            }
            if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.substring(CODE_BLOCK_PREFIX_LENGTH);
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.substring(0, cleanJson.length() - CODE_BLOCK_PREFIX_LENGTH);
            }
            cleanJson = cleanJson.trim();

            return objectMapper.readValue(cleanJson, new TypeReference<>() { });
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse JSON response: {}", e.getMessage());
            throw new OpenAiApiException("Failed to parse AI response as JSON", e);
        }
    }

    /**
     * Checks if an error is retryable.
     */
    private boolean isRetryableError(Exception e) {
        if (e instanceof OpenAiApiException apiException) {
            String message = apiException.getMessage();
            // Retry on server errors or rate limits
            return message.contains("5xx") || message.contains("429") || message.contains("Server error");
        }
        return e instanceof RestClientException;
    }

    /**
     * Waits before retrying with exponential backoff.
     */
    private void waitBeforeRetry(int attempt) {
        try {
            long waitMs = (long) Math.pow(2, attempt) * RETRY_BASE_DELAY_MS; // Exponential backoff
            LOGGER.debug("Waiting {}ms before retry", waitMs);
            Thread.sleep(waitMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Custom exception for OpenAI API errors.
     */
    private static class OpenAiApiException extends RuntimeException {
        public OpenAiApiException(String message) {
            super(message);
        }

        public OpenAiApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Generates mock responses when API is not configured.
     */
    private static class MockResponseGenerator {

        public Map<String, Object> generateMockProfile(WineEntity wine, String language) {
            boolean isSpanish = Objects.nonNull(language) && language.startsWith("es");
            Map<String, Object> profile = new HashMap<>();

            String wineName = wine.getName();
            String wineType = Objects.nonNull(wine.getWineType()) ? wine.getWineType().name().toLowerCase() : "wine";

            // Summary
            if (isSpanish) {
                profile.put("summary", String.format(
                        "El %s es un vino %s excepcional que destaca por su carácter único y elegancia. "
                                + "Con una personalidad distintiva, este vino refleja la pasión y el cuidado de sus creadores.",
                        wineName, translateWineType(wineType, true)));
            } else {
                profile.put("summary", String.format(
                        "The %s is an exceptional %s wine that stands out for its unique character and elegance. "
                                + "With a distinctive personality, this wine reflects the passion and care of its creators.",
                        wineName, wineType));
            }

            // Tasting Notes
            Map<String, String> tastingNotes = new HashMap<>();
            if (isSpanish) {
                tastingNotes.put("appearance", "Color brillante con reflejos característicos de la variedad.");
                tastingNotes.put("aroma", "Aromas complejos con notas frutales y un toque especiado.");
                tastingNotes.put("palate", "En boca presenta un equilibrio excelente con taninos sedosos.");
                tastingNotes.put("finish", "Final largo y persistente con notas de fruta madura.");
            } else {
                tastingNotes.put("appearance", "Brilliant color with characteristic reflections of the variety.");
                tastingNotes.put("aroma", "Complex aromas with fruity notes and a spicy touch.");
                tastingNotes.put("palate", "On the palate, it presents excellent balance with silky tannins.");
                tastingNotes.put("finish", "Long and persistent finish with ripe fruit notes.");
            }
            profile.put("tastingNotes", tastingNotes);

            // Food Pairings
            List<String> foodPairings = isSpanish
                    ? List.of("Carnes rojas a la parrilla", "Pastas con salsas robustas", "Quesos maduros")
                    : List.of("Grilled red meats", "Pasta with robust sauces", "Aged cheeses");
            profile.put("foodPairings", foodPairings);

            // Occasions
            List<String> occasions = isSpanish
                    ? List.of("Cenas especiales con amigos", "Celebraciones familiares", "Momentos de reflexión")
                    : List.of("Special dinners with friends", "Family celebrations", "Moments of reflection");
            profile.put("occasions", occasions);

            // Fun Facts
            List<String> funFacts = isSpanish
                    ? List.of("La región tiene más de 150 años de tradición vitivinícola.",
                    "Este estilo de vino es uno de los más apreciados mundialmente.")
                    : List.of("The region has over 150 years of winemaking tradition.",
                    "This style of wine is one of the most appreciated worldwide.");
            profile.put("funFacts", funFacts);

            // Serving Recommendations
            Map<String, String> servingRecs = new HashMap<>();
            if (isSpanish) {
                servingRecs.put("temperature", "Servir entre 16-18°C");
                servingRecs.put("decanting", "Se recomienda decantar 30 minutos antes de servir");
                servingRecs.put("glassType", "Copa de vino tinto amplia tipo Bordeaux");
                servingRecs.put("storageTips", "Conservar en lugar fresco y oscuro, horizontal");
            } else {
                servingRecs.put("temperature", "Serve between 16-18°C (61-64°F)");
                servingRecs.put("decanting", "Recommended to decant 30 minutes before serving");
                servingRecs.put("glassType", "Large red wine glass, Bordeaux style");
                servingRecs.put("storageTips", "Store in a cool, dark place, horizontally");
            }
            profile.put("servingRecommendations", servingRecs);

            return profile;
        }

        public Map<String, Object> generateMockComparison(WineEntity wineA, WineEntity wineB, String language) {
            boolean isSpanish = Objects.nonNull(language) && language.startsWith("es");
            Map<String, Object> comparison = new HashMap<>();

            // Summary
            if (isSpanish) {
                comparison.put("summary", String.format(
                        "Comparando %s con %s, encontramos dos vinos con personalidades únicas. "
                                + "Ambos representan excelentes ejemplos de la tradición vitivinícola.",
                        wineA.getName(), wineB.getName()));
            } else {
                comparison.put("summary", String.format(
                        "Comparing %s with %s, we find two wines with unique personalities. "
                                + "Both represent excellent examples of winemaking tradition.",
                        wineA.getName(), wineB.getName()));
            }

            // Attribute Comparison
            Map<String, Map<String, String>> attributes = new HashMap<>();
            attributes.put("appearance", createAttrMap(isSpanish,
                    "Color profundo con reflejos brillantes", "Deep color with brilliant reflections",
                    "Color intenso con tonos característicos", "Intense color with characteristic tones",
                    "Ambos presentan excelente claridad", "Both show excellent clarity"));
            attributes.put("aroma", createAttrMap(isSpanish,
                    "Aromas frutales con notas especiadas", "Fruity aromas with spicy notes",
                    "Bouquet complejo con matices florales", "Complex bouquet with floral nuances",
                    "Intensidades aromáticas similares", "Similar aromatic intensities"));
            attributes.put("palate", createAttrMap(isSpanish,
                    "Estructura robusta con taninos firmes", "Robust structure with firm tannins",
                    "Cuerpo elegante con acidez equilibrada", "Elegant body with balanced acidity",
                    "Diferentes perfiles de boca", "Different palate profiles"));
            attributes.put("finish", createAttrMap(isSpanish,
                    "Final largo y persistente", "Long and persistent finish",
                    "Retrogusto prolongado y agradable", "Prolonged and pleasant aftertaste",
                    "Ambos con finales memorables", "Both with memorable finishes"));
            comparison.put("attributeComparison", attributes);

            // Similarities
            List<String> similarities = isSpanish
                    ? List.of("Ambos vinos provienen de viñedos de alta calidad",
                    "Comparten un perfil de envejecimiento similar")
                    : List.of("Both wines come from high-quality vineyards",
                    "They share a similar aging profile");
            comparison.put("similarities", similarities);

            // Differences
            List<String> differences = isSpanish
                    ? List.of(wineA.getName() + " tiene mayor intensidad tánica",
                    wineB.getName() + " presenta más notas frutales")
                    : List.of(wineA.getName() + " has greater tannic intensity",
                    wineB.getName() + " presents more fruity notes");
            comparison.put("differences", differences);

            // Food Pairings
            Map<String, Object> foodPairings = new HashMap<>();
            foodPairings.put("wineA", isSpanish
                    ? List.of("Asado argentino", "Cordero al horno")
                    : List.of("Argentine asado", "Roasted lamb"));
            foodPairings.put("wineB", isSpanish
                    ? List.of("Pasta con ragú", "Ternera a la parrilla")
                    : List.of("Pasta with ragú", "Grilled beef"));
            foodPairings.put("shared", isSpanish
                    ? List.of("Carnes rojas", "Quesos semiduros")
                    : List.of("Red meats", "Semi-hard cheeses"));
            comparison.put("foodPairings", foodPairings);

            // Occasions
            Map<String, Object> occasions = new HashMap<>();
            occasions.put("wineA", isSpanish
                    ? List.of("Celebraciones formales", "Aniversarios")
                    : List.of("Formal celebrations", "Anniversaries"));
            occasions.put("wineB", isSpanish
                    ? List.of("Reuniones con amigos", "Cenas románticas")
                    : List.of("Gatherings with friends", "Romantic dinners"));
            comparison.put("occasions", occasions);

            // Value Assessment
            comparison.put("valueAssessment", isSpanish
                    ? "Ambos vinos ofrecen una excelente relación calidad-precio."
                    : "Both wines offer excellent value for money.");

            // Recommendation
            Map<String, String> recommendation = new HashMap<>();
            if (isSpanish) {
                recommendation.put("chooseWineAIf",
                        "Elige " + wineA.getName() + " si prefieres vinos con mayor estructura");
                recommendation.put("chooseWineBIf",
                        "Elige " + wineB.getName() + " si buscas un vino más accesible");
                recommendation.put("overallNote",
                        "Ambos son excelentes opciones que satisfarán a cualquier amante del vino");
            } else {
                recommendation.put("chooseWineAIf",
                        "Choose " + wineA.getName() + " if you prefer wines with more structure");
                recommendation.put("chooseWineBIf",
                        "Choose " + wineB.getName() + " if you're looking for a more accessible wine");
                recommendation.put("overallNote",
                        "Both are excellent choices that will satisfy any wine lover");
            }
            comparison.put("recommendation", recommendation);

            return comparison;
        }

        private Map<String, String> createAttrMap(boolean isSpanish,
                                                   String aEs, String aEn,
                                                   String bEs, String bEn,
                                                   String cEs, String cEn) {
            Map<String, String> map = new HashMap<>();
            map.put("wineA", isSpanish ? aEs : aEn);
            map.put("wineB", isSpanish ? bEs : bEn);
            map.put("comparison", isSpanish ? cEs : cEn);
            return map;
        }

        private static String translateWineType(String wineType, boolean toSpanish) {
            if (!toSpanish) {
                return wineType;
            }
            return switch (wineType.toLowerCase()) {
                case "red" -> "tinto";
                case "white" -> "blanco";
                case "rose" -> "rosado";
                case "sparkling" -> "espumante";
                case "dessert" -> "dulce";
                case "fortified" -> "fortificado";
                case "orange" -> "naranja";
                default -> wineType;
            };
        }
    }
}


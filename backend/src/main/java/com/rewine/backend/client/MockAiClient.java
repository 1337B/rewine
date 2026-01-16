package com.rewine.backend.client;

import com.rewine.backend.configuration.properties.AiProperties;
import com.rewine.backend.model.entity.WineEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Mock implementation of the AI client for development and testing.
 * Generates realistic-looking wine profiles without calling an external AI service.
 */
@Component
@ConditionalOnProperty(
        name = "rewine.ai.provider",
        havingValue = "mock",
        matchIfMissing = true
)
public class MockAiClient implements IAiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockAiClient.class);

    private final AiProperties aiProperties;

    public MockAiClient(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    @Override
    public Map<String, Object> generateWineProfile(WineEntity wine, String language) {
        LOGGER.info("Generating mock AI profile for wine: {} (ID: {}), language: {}",
                wine.getName(), wine.getId(), language);

        Map<String, Object> profile = new HashMap<>();

        // Generate summary based on wine data
        String summary = generateSummary(wine, language);
        profile.put("summary", summary);

        // Generate tasting notes
        Map<String, String> tastingNotes = generateTastingNotes(wine, language);
        profile.put("tastingNotes", tastingNotes);

        // Generate food pairings
        List<String> foodPairings = generateFoodPairings(wine, language);
        profile.put("foodPairings", foodPairings);

        // Generate occasions
        List<String> occasions = generateOccasions(wine, language);
        profile.put("occasions", occasions);

        // Generate fun facts
        List<String> funFacts = generateFunFacts(wine, language);
        profile.put("funFacts", funFacts);

        // Generate serving recommendations
        Map<String, String> servingRecommendations = generateServingRecommendations(wine, language);
        profile.put("servingRecommendations", servingRecommendations);

        LOGGER.debug("Generated mock profile with {} sections for wine: {}",
                profile.size(), wine.getName());

        return profile;
    }

    @Override
    public boolean isAvailable() {
        return aiProperties.isEnabled();
    }

    @Override
    public String getProviderName() {
        return "Mock";
    }

    @Override
    public Map<String, Object> generateWineComparison(WineEntity wineA, WineEntity wineB, String language) {
        LOGGER.info("Generating mock AI comparison for wines: {} vs {} (IDs: {} vs {}), language: {}",
                wineA.getName(), wineB.getName(), wineA.getId(), wineB.getId(), language);

        Map<String, Object> comparison = new HashMap<>();
        boolean isSpanish = language.startsWith("es");

        // Generate summary
        comparison.put("summary", generateComparisonSummary(wineA, wineB, isSpanish));

        // Generate attribute comparison
        comparison.put("attributeComparison", generateAttributeComparison(wineA, wineB, isSpanish));

        // Generate similarities
        comparison.put("similarities", generateSimilarities(wineA, wineB, isSpanish));

        // Generate differences
        comparison.put("differences", generateDifferences(wineA, wineB, isSpanish));

        // Generate food pairing comparison
        comparison.put("foodPairings", generateFoodPairingComparison(wineA, wineB, isSpanish));

        // Generate occasion comparison
        comparison.put("occasions", generateOccasionComparison(wineA, wineB, isSpanish));

        // Generate value assessment
        comparison.put("valueAssessment", generateValueAssessment(wineA, wineB, isSpanish));

        // Generate recommendation
        comparison.put("recommendation", generateRecommendation(wineA, wineB, isSpanish));

        LOGGER.debug("Generated mock comparison with {} sections for wines: {} vs {}",
                comparison.size(), wineA.getName(), wineB.getName());

        return comparison;
    }

    private String generateComparisonSummary(WineEntity wineA, WineEntity wineB, boolean isSpanish) {
        if (isSpanish) {
            return String.format(
                "Comparando %s con %s, encontramos dos vinos con personalidades únicas. "
                + "Ambos representan excelentes ejemplos de la tradición vitivinícola, "
                + "pero ofrecen experiencias distintas para el paladar del conocedor.",
                wineA.getName(), wineB.getName()
            );
        } else {
            return String.format(
                "Comparing %s with %s, we find two wines with unique personalities. "
                + "Both represent excellent examples of winemaking tradition, "
                + "but offer distinct experiences for the connoisseur's palate.",
                wineA.getName(), wineB.getName()
            );
        }
    }

    private Map<String, Object> generateAttributeComparison(WineEntity wineA, WineEntity wineB, boolean isSpanish) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("appearance", createAttributeMap(
                isSpanish ? "Color profundo con reflejos brillantes" : "Deep color with brilliant reflections",
                isSpanish ? "Color intenso con tonos característicos" : "Intense color with characteristic tones",
                isSpanish ? "Ambos presentan excelente claridad" : "Both show excellent clarity"
        ));

        attributes.put("aroma", createAttributeMap(
                isSpanish ? "Aromas frutales con notas especiadas" : "Fruity aromas with spicy notes",
                isSpanish ? "Bouquet complejo con matices florales" : "Complex bouquet with floral nuances",
                isSpanish ? "Intensidades aromáticas similares" : "Similar aromatic intensities"
        ));

        attributes.put("palate", createAttributeMap(
                isSpanish ? "Estructura robusta con taninos firmes" : "Robust structure with firm tannins",
                isSpanish ? "Cuerpo elegante con acidez equilibrada" : "Elegant body with balanced acidity",
                isSpanish ? "Diferentes perfiles de boca" : "Different palate profiles"
        ));

        attributes.put("finish", createAttributeMap(
                isSpanish ? "Final largo y persistente" : "Long and persistent finish",
                isSpanish ? "Retrogusto prolongado y agradable" : "Prolonged and pleasant aftertaste",
                isSpanish ? "Ambos con finales memorables" : "Both with memorable finishes"
        ));

        return attributes;
    }

    private Map<String, String> createAttributeMap(String wineA, String wineB, String comparison) {
        Map<String, String> attr = new HashMap<>();
        attr.put("wineA", wineA);
        attr.put("wineB", wineB);
        attr.put("comparison", comparison);
        return attr;
    }

    private List<String> generateSimilarities(WineEntity wineA, WineEntity wineB, boolean isSpanish) {
        List<String> similarities = new ArrayList<>();
        if (isSpanish) {
            similarities.add("Ambos vinos provienen de viñedos de alta calidad");
            similarities.add("Comparten un perfil de envejecimiento similar");
            similarities.add("Excelente potencial de guarda");
            similarities.add("Ideales para acompañar carnes");
        } else {
            similarities.add("Both wines come from high-quality vineyards");
            similarities.add("They share a similar aging profile");
            similarities.add("Excellent cellaring potential");
            similarities.add("Ideal for pairing with meats");
        }
        return similarities;
    }

    private List<String> generateDifferences(WineEntity wineA, WineEntity wineB, boolean isSpanish) {
        List<String> differences = new ArrayList<>();
        if (isSpanish) {
            differences.add(String.format("%s tiene mayor intensidad tánica", wineA.getName()));
            differences.add(String.format("%s presenta más notas frutales", wineB.getName()));
            differences.add("Diferentes perfiles de acidez");
            differences.add("Variaciones en el cuerpo y estructura");
        } else {
            differences.add(String.format("%s has greater tannic intensity", wineA.getName()));
            differences.add(String.format("%s presents more fruity notes", wineB.getName()));
            differences.add("Different acidity profiles");
            differences.add("Variations in body and structure");
        }
        return differences;
    }

    private Map<String, Object> generateFoodPairingComparison(WineEntity wineA, WineEntity wineB, boolean isSpanish) {
        Map<String, Object> pairings = new HashMap<>();
        if (isSpanish) {
            pairings.put("wineA", List.of("Asado argentino", "Cordero al horno", "Quesos curados"));
            pairings.put("wineB", List.of("Pasta con ragú", "Ternera a la parrilla", "Hongos salteados"));
            pairings.put("shared", List.of("Carnes rojas", "Empanadas", "Quesos semiduros"));
        } else {
            pairings.put("wineA", List.of("Argentine asado", "Roasted lamb", "Aged cheeses"));
            pairings.put("wineB", List.of("Pasta with ragú", "Grilled beef", "Sautéed mushrooms"));
            pairings.put("shared", List.of("Red meats", "Empanadas", "Semi-hard cheeses"));
        }
        return pairings;
    }

    private Map<String, Object> generateOccasionComparison(WineEntity wineA, WineEntity wineB, boolean isSpanish) {
        Map<String, Object> occasions = new HashMap<>();
        if (isSpanish) {
            occasions.put("wineA", List.of("Celebraciones formales", "Cenas de negocios", "Aniversarios"));
            occasions.put("wineB", List.of("Reuniones con amigos", "Asados de fin de semana", "Cenas románticas"));
        } else {
            occasions.put("wineA", List.of("Formal celebrations", "Business dinners", "Anniversaries"));
            occasions.put("wineB", List.of("Gatherings with friends", "Weekend barbecues", "Romantic dinners"));
        }
        return occasions;
    }

    private String generateValueAssessment(WineEntity wineA, WineEntity wineB, boolean isSpanish) {
        if (isSpanish) {
            return "Ambos vinos ofrecen una excelente relación calidad-precio, "
                + "representando el valor de sus respectivas bodegas y regiones.";
        } else {
            return "Both wines offer excellent value for money, "
                + "representing the worth of their respective wineries and regions.";
        }
    }

    private Map<String, String> generateRecommendation(WineEntity wineA, WineEntity wineB, boolean isSpanish) {
        Map<String, String> recommendation = new HashMap<>();
        if (isSpanish) {
            recommendation.put("chooseWineAIf", String.format(
                    "Elige %s si prefieres vinos con mayor estructura y taninos pronunciados",
                    wineA.getName()));
            recommendation.put("chooseWineBIf", String.format(
                    "Elige %s si buscas un vino más accesible y frutal",
                    wineB.getName()));
            recommendation.put("overallNote",
                    "Ambos son excelentes opciones que satisfarán a cualquier amante del vino");
        } else {
            recommendation.put("chooseWineAIf", String.format(
                    "Choose %s if you prefer wines with more structure and pronounced tannins",
                    wineA.getName()));
            recommendation.put("chooseWineBIf", String.format(
                    "Choose %s if you're looking for a more accessible and fruity wine",
                    wineB.getName()));
            recommendation.put("overallNote",
                    "Both are excellent choices that will satisfy any wine lover");
        }
        return recommendation;
    }

    private String generateSummary(WineEntity wine, String language) {
        boolean isSpanish = language.startsWith("es");
        String wineName = wine.getName();
        String wineType = Objects.nonNull(wine.getWineType()) ? wine.getWineType().name().toLowerCase() : "wine";

        if (isSpanish) {
            return String.format(
                "El %s es un vino %s excepcional que destaca por su carácter único y elegancia. "
                + "Con una personalidad distintiva, este vino refleja la pasión y el cuidado "
                + "de sus creadores. Ideal para quienes aprecian los vinos con historia y calidad.",
                wineName, translateWineType(wineType, true)
            );
        } else {
            return String.format(
                "The %s is an exceptional %s wine that stands out for its unique character and elegance. "
                + "With a distinctive personality, this wine reflects the passion and care of its creators. "
                + "Ideal for those who appreciate wines with history and quality.",
                wineName, wineType
            );
        }
    }

    private Map<String, String> generateTastingNotes(WineEntity wine, String language) {
        Map<String, String> notes = new HashMap<>();
        boolean isSpanish = language.startsWith("es");

        if (isSpanish) {
            notes.put("appearance", "Color brillante con reflejos característicos de la variedad.");
            notes.put("aroma", "Aromas complejos con notas frutales y un toque especiado.");
            notes.put("palate", "En boca presenta un equilibrio excelente con taninos sedosos.");
            notes.put("finish", "Final largo y persistente con notas de fruta madura.");
        } else {
            notes.put("appearance", "Brilliant color with characteristic reflections of the variety.");
            notes.put("aroma", "Complex aromas with fruity notes and a spicy touch.");
            notes.put("palate", "On the palate, it presents excellent balance with silky tannins.");
            notes.put("finish", "Long and persistent finish with ripe fruit notes.");
        }

        return notes;
    }

    private List<String> generateFoodPairings(WineEntity wine, String language) {
        List<String> pairings = new ArrayList<>();
        boolean isSpanish = language.startsWith("es");

        if (isSpanish) {
            pairings.add("Carnes rojas a la parrilla");
            pairings.add("Pastas con salsas robustas");
            pairings.add("Quesos maduros");
            pairings.add("Risotto de hongos");
            pairings.add("Empanadas argentinas");
        } else {
            pairings.add("Grilled red meats");
            pairings.add("Pasta with robust sauces");
            pairings.add("Aged cheeses");
            pairings.add("Mushroom risotto");
            pairings.add("Argentine empanadas");
        }

        return pairings;
    }

    private List<String> generateOccasions(WineEntity wine, String language) {
        List<String> occasions = new ArrayList<>();
        boolean isSpanish = language.startsWith("es");

        if (isSpanish) {
            occasions.add("Cenas especiales con amigos");
            occasions.add("Celebraciones familiares");
            occasions.add("Maridajes gastronómicos");
            occasions.add("Momentos de reflexión");
        } else {
            occasions.add("Special dinners with friends");
            occasions.add("Family celebrations");
            occasions.add("Gastronomic pairings");
            occasions.add("Moments of reflection");
        }

        return occasions;
    }

    private List<String> generateFunFacts(WineEntity wine, String language) {
        List<String> facts = new ArrayList<>();
        boolean isSpanish = language.startsWith("es");

        if (isSpanish) {
            facts.add("La región donde se produce este vino tiene más de 150 años de tradición vitivinícola.");
            facts.add("Las uvas se cosechan a mano para asegurar la máxima calidad.");
            facts.add("El proceso de vinificación combina técnicas tradicionales con tecnología moderna.");
        } else {
            facts.add("The region where this wine is produced has over 150 years of winemaking tradition.");
            facts.add("The grapes are harvested by hand to ensure maximum quality.");
            facts.add("The winemaking process combines traditional techniques with modern technology.");
        }

        return facts;
    }

    private Map<String, String> generateServingRecommendations(WineEntity wine, String language) {
        Map<String, String> recommendations = new HashMap<>();
        boolean isSpanish = language.startsWith("es");

        // Use wine's serving temp if available, otherwise use defaults
        String tempRange;
        if (Objects.nonNull(wine.getServingTempMin()) && Objects.nonNull(wine.getServingTempMax())) {
            tempRange = wine.getServingTempMin() + "-" + wine.getServingTempMax() + "°C";
        } else {
            tempRange = "16-18°C";
        }

        if (isSpanish) {
            recommendations.put("temperature", "Servir entre " + tempRange);
            recommendations.put("decanting", "Se recomienda decantar 30 minutos antes de servir");
            recommendations.put("glassType", "Copa de vino tinto amplia tipo Bordeaux");
            recommendations.put("storageTips", "Conservar en lugar fresco y oscuro, temperatura constante");
        } else {
            recommendations.put("temperature", "Serve between " + tempRange);
            recommendations.put("decanting", "Recommended to decant 30 minutes before serving");
            recommendations.put("glassType", "Wide Bordeaux-style red wine glass");
            recommendations.put("storageTips", "Store in a cool, dark place at constant temperature");
        }

        return recommendations;
    }

    private String translateWineType(String type, boolean isSpanish) {
        if (!isSpanish) {
            return type;
        }

        return switch (type.toLowerCase()) {
            case "red" -> "tinto";
            case "white" -> "blanco";
            case "rose" -> "rosado";
            case "sparkling" -> "espumante";
            case "dessert" -> "de postre";
            case "fortified" -> "fortificado";
            case "orange" -> "naranja";
            default -> type;
        };
    }
}


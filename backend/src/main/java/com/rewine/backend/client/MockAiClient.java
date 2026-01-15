package com.rewine.backend.client;

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
        return true;
    }

    @Override
    public String getProviderName() {
        return "Mock";
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


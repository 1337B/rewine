package com.rewine.backend.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Maps service configuration properties.
 * Loaded from application.yml under 'rewine.maps' prefix.
 */
@Component
@ConfigurationProperties(prefix = "rewine.maps")
@Validated
public class MapsProperties {

    private static final int DEFAULT_MAP_WIDTH = 640;
    private static final int DEFAULT_MAP_HEIGHT = 400;

    /**
     * Whether maps features are enabled.
     */
    private boolean enabled = false;

    /**
     * Maps provider (google, openstreetmap).
     */
    private String provider = "openstreetmap";

    /**
     * Google Maps API key.
     */
    private String apiKey;

    /**
     * Default map style (roadmap, satellite, terrain, hybrid).
     */
    private String defaultMapStyle = "roadmap";

    /**
     * Default image width for static maps.
     */
    private int defaultImageWidth = DEFAULT_MAP_WIDTH;

    /**
     * Default image height for static maps.
     */
    private int defaultImageHeight = DEFAULT_MAP_HEIGHT;

    // Getters and setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDefaultMapStyle() {
        return defaultMapStyle;
    }

    public void setDefaultMapStyle(String defaultMapStyle) {
        this.defaultMapStyle = defaultMapStyle;
    }

    public int getDefaultImageWidth() {
        return defaultImageWidth;
    }

    public void setDefaultImageWidth(int defaultImageWidth) {
        this.defaultImageWidth = defaultImageWidth;
    }

    public int getDefaultImageHeight() {
        return defaultImageHeight;
    }

    public void setDefaultImageHeight(int defaultImageHeight) {
        this.defaultImageHeight = defaultImageHeight;
    }

    @Override
    public String toString() {
        return "MapsProperties{"
                + "enabled=" + enabled
                + ", provider='" + provider + '\''
                + ", apiKeyConfigured=" + (Objects.nonNull(apiKey) && !apiKey.isBlank())
                + ", defaultMapStyle='" + defaultMapStyle + '\''
                + ", defaultImageWidth=" + defaultImageWidth
                + ", defaultImageHeight=" + defaultImageHeight
                + '}';
    }
}


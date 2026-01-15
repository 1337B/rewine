package com.rewine.backend.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

/**
 * AI service configuration properties.
 * Loaded from application.yml under 'rewine.ai' prefix.
 */
@Component
@ConfigurationProperties(prefix = "rewine.ai")
@Validated
public class AiProperties {

    // Time conversion constants
    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 10;
    private static final int DEFAULT_READ_TIMEOUT_SECONDS = 60;
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final int DEFAULT_MAX_TOKENS = 2000;
    private static final double DEFAULT_TEMPERATURE = 0.7;

    /**
     * AI provider type (mock, openai).
     */
    @NotBlank
    private String provider = "mock";

    /**
     * Whether AI features are enabled.
     */
    private boolean enabled = true;

    /**
     * OpenAI-specific configuration.
     */
    private OpenAiConfig openai = new OpenAiConfig();

    /**
     * HTTP client configuration for AI calls.
     */
    private HttpConfig http = new HttpConfig();

    // Getters and setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public OpenAiConfig getOpenai() {
        return openai;
    }

    public void setOpenai(OpenAiConfig openai) {
        this.openai = openai;
    }

    public HttpConfig getHttp() {
        return http;
    }

    public void setHttp(HttpConfig http) {
        this.http = http;
    }

    /**
     * Checks if OpenAI is properly configured with an API key.
     *
     * @return true if API key is present and non-empty
     */
    public boolean isOpenAiConfigured() {
        return Objects.nonNull(openai)
                && Objects.nonNull(openai.getApiKey())
                && !openai.getApiKey().isBlank()
                && !openai.getApiKey().equals("not-configured");
    }

    /**
     * Checks if the provider is set to OpenAI.
     *
     * @return true if provider is openai
     */
    public boolean isOpenAiProvider() {
        return "openai".equalsIgnoreCase(provider);
    }

    /**
     * OpenAI-specific configuration.
     */
    public static class OpenAiConfig {
        /**
         * OpenAI API base URL.
         */
        private String baseUrl = "https://api.openai.com/v1";

        /**
         * OpenAI API key (from environment variable).
         */
        private String apiKey = "not-configured";

        /**
         * OpenAI model to use.
         */
        private String model = "gpt-4o-mini";

        /**
         * Maximum tokens for completion.
         */
        @Positive
        private int maxTokens = DEFAULT_MAX_TOKENS;

        /**
         * Temperature for generation (0.0 - 2.0).
         */
        private double temperature = DEFAULT_TEMPERATURE;

        // Getters and setters
        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
    }

    /**
     * HTTP client configuration.
     */
    public static class HttpConfig {
        /**
         * Connection timeout in seconds.
         */
        @Positive
        private int connectTimeoutSeconds = DEFAULT_CONNECT_TIMEOUT_SECONDS;

        /**
         * Read timeout in seconds.
         */
        @Positive
        private int readTimeoutSeconds = DEFAULT_READ_TIMEOUT_SECONDS;

        /**
         * Maximum retries for transient errors.
         */
        @Positive
        private int maxRetries = DEFAULT_MAX_RETRIES;

        /**
         * Whether to enable request/response logging.
         */
        private boolean loggingEnabled = true;

        // Getters and setters
        public int getConnectTimeoutSeconds() {
            return connectTimeoutSeconds;
        }

        public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
            this.connectTimeoutSeconds = connectTimeoutSeconds;
        }

        public int getReadTimeoutSeconds() {
            return readTimeoutSeconds;
        }

        public void setReadTimeoutSeconds(int readTimeoutSeconds) {
            this.readTimeoutSeconds = readTimeoutSeconds;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        public boolean isLoggingEnabled() {
            return loggingEnabled;
        }

        public void setLoggingEnabled(boolean loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
        }
    }

    @Override
    public String toString() {
        return "AiProperties{"
                + "provider='" + provider + '\''
                + ", enabled=" + enabled
                + ", openaiConfigured=" + isOpenAiConfigured()
                + ", model='" + (Objects.nonNull(openai) ? openai.getModel() : "null") + '\''
                + '}';
    }
}


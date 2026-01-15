package com.rewine.backend.client.http;

import org.springframework.web.client.RestClient;

/**
 * Factory interface for creating configured HTTP clients.
 * Provides RestClient instances with proper timeout and retry configurations.
 */
public interface IHttpClientFactory {

    /**
     * Creates a RestClient configured for AI service calls.
     *
     * @param baseUrl the base URL for the service
     * @return a configured RestClient instance
     */
    RestClient createAiClient(String baseUrl);

    /**
     * Creates a RestClient with custom timeout settings.
     *
     * @param baseUrl              the base URL for the service
     * @param connectTimeoutSeconds connection timeout in seconds
     * @param readTimeoutSeconds    read timeout in seconds
     * @return a configured RestClient instance
     */
    RestClient createClient(String baseUrl, int connectTimeoutSeconds, int readTimeoutSeconds);
}


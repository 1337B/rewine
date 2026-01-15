package com.rewine.backend.client.http.impl;

import com.rewine.backend.client.http.IHttpClientFactory;
import com.rewine.backend.configuration.properties.AiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of HTTP client factory using Spring RestClient.
 * Provides configured clients with timeouts, logging, and retry support.
 */
@Component
public class HttpClientFactoryImpl implements IHttpClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientFactoryImpl.class);

    private final AiProperties aiProperties;

    public HttpClientFactoryImpl(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    @Override
    public RestClient createAiClient(String baseUrl) {
        AiProperties.HttpConfig httpConfig = aiProperties.getHttp();
        return createClient(
                baseUrl,
                httpConfig.getConnectTimeoutSeconds(),
                httpConfig.getReadTimeoutSeconds()
        );
    }

    @Override
    public RestClient createClient(String baseUrl, int connectTimeoutSeconds, int readTimeoutSeconds) {
        LOGGER.debug("Creating RestClient for baseUrl: {}, connectTimeout: {}s, readTimeout: {}s",
                baseUrl, connectTimeoutSeconds, readTimeoutSeconds);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory);

        // Add logging interceptor if enabled
        if (aiProperties.getHttp().isLoggingEnabled()) {
            builder.requestInterceptor(new LoggingInterceptor());
        }

        return builder.build();
    }

    /**
     * HTTP request/response logging interceptor.
     * Logs request details without exposing sensitive data like API keys.
     */
    private static class LoggingInterceptor implements ClientHttpRequestInterceptor {

        private static final Logger HTTP_LOGGER = LoggerFactory.getLogger("com.rewine.backend.client.http");
        private static final String MASKED_VALUE = "[REDACTED]";

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

            logRequest(request, body);

            long startTime = System.nanoTime();
            ClientHttpResponse response;

            try {
                response = execution.execute(request, body);
            } catch (IOException e) {
                long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
                HTTP_LOGGER.error("HTTP request failed after {}ms: {} {} - {}",
                        duration, request.getMethod(), request.getURI(), e.getMessage());
                throw e;
            }

            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            logResponse(request, response, duration);

            return response;
        }

        private void logRequest(HttpRequest request, byte[] body) {
            if (HTTP_LOGGER.isDebugEnabled()) {
                HTTP_LOGGER.debug("HTTP Request: {} {} (body size: {} bytes)",
                        request.getMethod(),
                        maskSensitiveParams(request.getURI().toString()),
                        Objects.nonNull(body) ? body.length : 0);
            }
        }

        private void logResponse(HttpRequest request, ClientHttpResponse response, long durationMs) {
            try {
                if (response.getStatusCode().is2xxSuccessful()) {
                    HTTP_LOGGER.info("HTTP Response: {} {} -> {} ({}ms)",
                            request.getMethod(),
                            maskSensitiveParams(request.getURI().toString()),
                            response.getStatusCode().value(),
                            durationMs);
                } else {
                    HTTP_LOGGER.warn("HTTP Response: {} {} -> {} ({}ms)",
                            request.getMethod(),
                            maskSensitiveParams(request.getURI().toString()),
                            response.getStatusCode().value(),
                            durationMs);
                }
            } catch (IOException e) {
                HTTP_LOGGER.warn("Failed to log response status: {}", e.getMessage());
            }
        }

        /**
         * Masks sensitive parameters in URLs (e.g., API keys).
         */
        private String maskSensitiveParams(String url) {
            if (Objects.isNull(url)) {
                return null;
            }
            // Mask any api_key or key parameters
            return url.replaceAll("(api_key|key|apikey|token)=[^&]+", "$1=" + MASKED_VALUE);
        }
    }
}


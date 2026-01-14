package com.rewine.backend.configuration.openapi;

/**
 * Interface for OpenAPI/Swagger configuration.
 */
public interface IOpenApiConfig {

    /**
     * Returns the API title.
     */
    String getApiTitle();

    /**
     * Returns the API version.
     */
    String getApiVersion();

    /**
     * Returns the API description.
     */
    String getApiDescription();
}


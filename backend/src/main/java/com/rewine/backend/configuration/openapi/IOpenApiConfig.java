package com.rewine.backend.configuration.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;

/**
 * Interface for OpenAPI/Swagger configuration.
 */
public interface IOpenApiConfig {

    /**
     * Returns the API title.
     *
     * @return API title
     */
    String getApiTitle();

    /**
     * Returns the API version.
     *
     * @return API version
     */
    String getApiVersion();

    /**
     * Returns the API description.
     *
     * @return API description
     */
    String getApiDescription();

    /**
     * Returns the API contact name.
     *
     * @return Contact name
     */
    String getContactName();

    /**
     * Returns the API contact email.
     *
     * @return Contact email
     */
    String getContactEmail();

    /**
     * Creates the custom OpenAPI specification.
     *
     * @return OpenAPI configuration
     */
    OpenAPI customOpenApi();

    /**
     * Creates a grouped OpenAPI for public endpoints.
     *
     * @return GroupedOpenApi for public endpoints
     */
    GroupedOpenApi publicApi();

    /**
     * Creates a grouped OpenAPI for admin endpoints.
     *
     * @return GroupedOpenApi for admin endpoints
     */
    GroupedOpenApi adminApi();
}


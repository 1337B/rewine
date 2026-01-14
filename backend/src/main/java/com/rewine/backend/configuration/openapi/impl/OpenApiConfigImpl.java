package com.rewine.backend.configuration.openapi.impl;

import com.rewine.backend.configuration.openapi.IOpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration implementation.
 */
@Configuration
public class OpenApiConfigImpl implements IOpenApiConfig {

    @Value("${spring.application.name:Rewine API}")
    private String applicationName;

    @Value("${spring.application.version:0.0.1}")
    private String applicationVersion;

    private static final String API_DESCRIPTION = "Wine Discovery Platform - Backend REST API";

    @Override
    public String getApiTitle() {
        return applicationName;
    }

    @Override
    public String getApiVersion() {
        return applicationVersion;
    }

    @Override
    public String getApiDescription() {
        return API_DESCRIPTION;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(getApiTitle())
                        .version(getApiVersion())
                        .description(getApiDescription())
                        .contact(new Contact()
                                .name("Rewine Team")
                                .email("support@rewine.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://rewine.com")))
                .servers(List.of(
                        new Server().url("/api/v1").description("API v1")
                ));
    }
}


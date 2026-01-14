package com.rewine.backend.configuration.openapi.impl;

import com.rewine.backend.configuration.openapi.IOpenApiConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration implementation.
 * Provides API documentation with JWT security scheme and grouped endpoints.
 */
@Configuration
public class OpenApiConfigImpl implements IOpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    private static final String API_DESCRIPTION = "Wine Discovery Platform - Backend REST API. "
            + "This API provides endpoints for wine discovery, user management, events, and wine routes.";

    @Value("${spring.application.name:Rewine API}")
    private String applicationName;

    @Value("${spring.application.version:0.0.1-SNAPSHOT}")
    private String applicationVersion;

    @Value("${api.contact.name:Rewine Team}")
    private String contactName;

    @Value("${api.contact.email:support@rewine.com}")
    private String contactEmail;

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

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

    @Override
    public String getContactName() {
        return contactName;
    }

    @Override
    public String getContactEmail() {
        return contactEmail;
    }

    @Bean
    @Override
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(buildApiInfo())
                .servers(buildServers())
                .components(buildComponents())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    @Bean
    @Override
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .displayName("Public API")
                .pathsToMatch(
                        "/auth/**",
                        "/wines/**",
                        "/events/**",
                        "/wine-routes/**"
                )
                .pathsToExclude("/admin/**")
                .build();
    }

    @Bean
    @Override
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("Admin API")
                .pathsToMatch("/admin/**")
                .build();
    }

    /**
     * Builds the API info section.
     */
    private Info buildApiInfo() {
        return new Info()
                .title(getApiTitle())
                .version(getApiVersion())
                .description(getApiDescription())
                .contact(new Contact()
                        .name(getContactName())
                        .email(getContactEmail())
                        .url("https://rewine.com"))
                .license(new License()
                        .name("Private License")
                        .url("https://rewine.com/license"))
                .termsOfService("https://rewine.com/terms");
    }

    /**
     * Builds the server list.
     * Uses the context-path so Swagger UI generates correct URLs.
     */
    private List<Server> buildServers() {
        return List.of(
                new Server()
                        .url(contextPath)
                        .description("Current Server")
        );
    }

    /**
     * Builds the components including security schemes.
     */
    private Components buildComponents() {
        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Authorization header using the Bearer scheme. "
                                + "Enter your token in the format: Bearer {token}"));
    }
}


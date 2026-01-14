package com.rewine.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Rewine Backend Application
 *
 * Main entry point for the Wine Discovery Platform backend API.
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.rewine.backend.configuration.properties")
public class RewineBackendApp {

    public static void main(String[] args) {
        SpringApplication.run(RewineBackendApp.class, args);
    }
}


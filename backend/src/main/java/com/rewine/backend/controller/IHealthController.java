package com.rewine.backend.controller;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Interface for health and system endpoints.
 */
public interface IHealthController {

    /**
     * Health check endpoint.
     * @return "OK" if the service is healthy
     */
    ResponseEntity<String> health();

    /**
     * Version information endpoint.
     * @return Application name and version
     */
    ResponseEntity<Map<String, String>> version();
}


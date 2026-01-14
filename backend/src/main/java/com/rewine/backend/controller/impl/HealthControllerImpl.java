package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IHealthController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health and system endpoints implementation.
 */
@RestController
@Tag(name = "Health", description = "Health check and system information endpoints")
public class HealthControllerImpl implements IHealthController {

    @Value("${spring.application.name:rewine-backend}")
    private String applicationName;

    @Value("${spring.application.version:0.0.1-SNAPSHOT}")
    private String applicationVersion;

    @Override
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns OK if the service is healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @Override
    @GetMapping("/version")
    @Operation(summary = "Version info", description = "Returns application name and version")
    public ResponseEntity<Map<String, String>> version() {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("name", applicationName);
        versionInfo.put("version", applicationVersion);
        return ResponseEntity.ok(versionInfo);
    }
}


package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IWineAiProfileController;
import com.rewine.backend.dto.common.AiCacheStatusResponse;
import com.rewine.backend.dto.request.WineAiProfileGenerateRequest;
import com.rewine.backend.dto.response.WineAiProfileResponse;
import com.rewine.backend.service.orchestration.IWineProfileOrchestrator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

/**
 * REST controller for wine AI profile endpoints.
 */
@RestController
@RequestMapping("/wines/{wineId}/ai-profile")
public class WineAiProfileControllerImpl implements IWineAiProfileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineAiProfileControllerImpl.class);
    private static final String DEFAULT_LANGUAGE = "es-AR";

    private final IWineProfileOrchestrator wineProfileOrchestrator;

    public WineAiProfileControllerImpl(IWineProfileOrchestrator wineProfileOrchestrator) {
        this.wineProfileOrchestrator = wineProfileOrchestrator;
    }

    @Override
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WineAiProfileResponse> getWineAiProfile(
            @PathVariable UUID wineId,
            @RequestParam(required = false, defaultValue = DEFAULT_LANGUAGE) String language) {

        LOGGER.info("GET /wines/{}/ai-profile - Fetching AI profile, language: {}", wineId, language);

        WineAiProfileResponse response = wineProfileOrchestrator.getOrGenerateProfile(wineId, language);

        LOGGER.info("AI profile retrieved for wine: {}, cached: {}",
                wineId, Objects.nonNull(response.getGeneratedAt()));

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WineAiProfileResponse> generateWineAiProfile(
            @PathVariable UUID wineId,
            @Valid @RequestBody(required = false) WineAiProfileGenerateRequest request) {

        // Use defaults if no request body provided
        if (Objects.isNull(request)) {
            request = WineAiProfileGenerateRequest.builder()
                    .language(DEFAULT_LANGUAGE)
                    .forceRegenerate(false)
                    .build();
        }

        LOGGER.info("POST /wines/{}/ai-profile - Generating AI profile, language: {}, force: {}",
                wineId, request.getLanguage(), request.getForceRegenerate());

        WineAiProfileResponse response;

        if (Boolean.TRUE.equals(request.getForceRegenerate())) {
            LOGGER.info("Force regeneration requested for wine: {}", wineId);
            response = wineProfileOrchestrator.forceRegenerateProfile(wineId, request.getLanguage());
        } else {
            response = wineProfileOrchestrator.getOrGenerateProfile(wineId, request.getLanguage());
        }

        LOGGER.info("AI profile generated for wine: {}", wineId);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiCacheStatusResponse> getAiProfileStatus(
            @PathVariable UUID wineId,
            @RequestParam(required = false, defaultValue = DEFAULT_LANGUAGE) String language) {

        LOGGER.info("GET /wines/{}/ai-profile/status - Checking AI profile status, language: {}",
                wineId, language);

        AiCacheStatusResponse response = wineProfileOrchestrator.getCacheStatus(wineId, language);

        LOGGER.debug("AI profile status for wine {}: {}", wineId, response.getStatus());

        return ResponseEntity.ok(response);
    }
}


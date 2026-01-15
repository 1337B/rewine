package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IWineComparisonController;
import com.rewine.backend.dto.request.WineComparisonRequest;
import com.rewine.backend.dto.response.WineComparisonResponse;
import com.rewine.backend.service.orchestration.IWineComparisonOrchestrator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * REST controller for wine comparison endpoints.
 */
@RestController
@RequestMapping("/wines/compare")
public class WineComparisonControllerImpl implements IWineComparisonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineComparisonControllerImpl.class);
    private static final String DEFAULT_LANGUAGE = "es-AR";

    private final IWineComparisonOrchestrator wineComparisonOrchestrator;

    public WineComparisonControllerImpl(IWineComparisonOrchestrator wineComparisonOrchestrator) {
        this.wineComparisonOrchestrator = wineComparisonOrchestrator;
    }

    @Override
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WineComparisonResponse> compareWines(
            @Valid @RequestBody WineComparisonRequest request) {

        // Default language if not provided
        String language = Objects.nonNull(request.getLanguage()) && !request.getLanguage().isEmpty()
                ? request.getLanguage()
                : DEFAULT_LANGUAGE;

        LOGGER.info("POST /wines/compare - Comparing wines: {} vs {}, language: {}, forceRegenerate: {}",
                request.getWineAId(), request.getWineBId(), language, request.isForceRegenerate());

        WineComparisonResponse response;

        if (request.isForceRegenerate()) {
            LOGGER.info("Force regeneration requested for wine comparison: {} vs {}",
                    request.getWineAId(), request.getWineBId());
            response = wineComparisonOrchestrator.forceRegenerateComparison(
                    request.getWineAId(), request.getWineBId(), language);
        } else {
            response = wineComparisonOrchestrator.getOrGenerateComparison(
                    request.getWineAId(), request.getWineBId(), language);
        }

        LOGGER.info("Comparison completed for wines: {} vs {}, cached: {}",
                request.getWineAId(), request.getWineBId(), response.isCached());

        return ResponseEntity.ok(response);
    }
}


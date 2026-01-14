package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IWineController;
import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.WineSearchRequest;
import com.rewine.backend.dto.response.WineDetailsResponse;
import com.rewine.backend.dto.response.WineSummaryResponse;
import com.rewine.backend.model.enums.WineType;
import com.rewine.backend.service.IWineQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * REST controller for wine endpoints.
 */
@RestController
@RequestMapping("/wines")
public class WineControllerImpl implements IWineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineControllerImpl.class);
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final IWineQueryService wineQueryService;

    public WineControllerImpl(IWineQueryService wineQueryService) {
        this.wineQueryService = wineQueryService;
    }

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<WineSummaryResponse>> searchWines(
            @Valid @ModelAttribute WineSearchRequest request,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        LOGGER.info("GET /wines - Search wines request: page={}, size={}, filters={}", page, size, request);

        // Ensure page size is within bounds
        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        PageResponse<WineSummaryResponse> response = wineQueryService.searchWines(
                request,
                PageRequest.of(page, pageSize)
        );

        LOGGER.debug("Returning {} wines", response.getContent().size());

        return ResponseEntity.ok(response);
    }

    /**
     * Alternative search endpoint with individual query parameters.
     * This provides a more explicit API for clients.
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<WineSummaryResponse>> searchWinesExplicit(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) WineType wineType,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Integer vintage,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) WineSearchRequest.SortField sortBy,
            @RequestParam(required = false) WineSearchRequest.SortDirection sortDirection,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        LOGGER.info("GET /wines/search - Explicit search: search={}, type={}, country={}", search, wineType, country);

        WineSearchRequest request = WineSearchRequest.builder()
                .search(search)
                .wineType(wineType)
                .country(country)
                .region(region)
                .vintage(vintage)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(minRating)
                .featured(featured)
                .sortBy(Objects.nonNull(sortBy) ? sortBy : WineSearchRequest.SortField.NAME)
                .sortDirection(Objects.nonNull(sortDirection) ? sortDirection : WineSearchRequest.SortDirection.ASC)
                .build();

        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        PageResponse<WineSummaryResponse> response = wineQueryService.searchWines(
                request,
                PageRequest.of(page, pageSize)
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<WineDetailsResponse> getWineDetails(@PathVariable UUID id) {
        LOGGER.info("GET /wines/{} - Get wine details", id);

        WineDetailsResponse response = wineQueryService.getWineDetails(id);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/featured")
    public ResponseEntity<PageResponse<WineSummaryResponse>> getFeaturedWines(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        LOGGER.info("GET /wines/featured - page={}, size={}", page, size);

        PageResponse<WineSummaryResponse> response = wineQueryService.getFeaturedWines(
                PageRequest.of(page, size)
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/top-rated")
    public ResponseEntity<PageResponse<WineSummaryResponse>> getTopRatedWines(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        LOGGER.info("GET /wines/top-rated - page={}, size={}", page, size);

        PageResponse<WineSummaryResponse> response = wineQueryService.getTopRatedWines(
                PageRequest.of(page, size)
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/recent")
    public ResponseEntity<PageResponse<WineSummaryResponse>> getRecentWines(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        LOGGER.info("GET /wines/recent - page={}, size={}", page, size);

        PageResponse<WineSummaryResponse> response = wineQueryService.getRecentWines(
                PageRequest.of(page, size)
        );

        return ResponseEntity.ok(response);
    }
}


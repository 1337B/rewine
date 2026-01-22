package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IWineRouteController;
import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.response.WineRouteDetailsResponse;
import com.rewine.backend.dto.response.WineRouteHierarchyResponse;
import com.rewine.backend.dto.response.WineRouteSummaryResponse;
import com.rewine.backend.service.IWineRouteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Wine route endpoints implementation.
 * Provides hierarchical browsing and route retrieval functionality.
 */
@RestController
@RequestMapping("/wine-routes")
@Tag(name = "Wine Routes", description = "Wine route management endpoints")
@RequiredArgsConstructor
@Slf4j
public class WineRouteControllerImpl implements IWineRouteController {

    private final IWineRouteService wineRouteService;

    // =========================================================================
    // Hierarchical Browsing
    // =========================================================================

    @Override
    @GetMapping("/hierarchy")
    public ResponseEntity<WineRouteHierarchyResponse> getHierarchy() {
        log.info("GET /wine-routes/hierarchy - Retrieving location hierarchy");
        WineRouteHierarchyResponse hierarchy = wineRouteService.getHierarchy();
        return ResponseEntity.ok(hierarchy);
    }

    @Override
    @GetMapping("/countries")
    public ResponseEntity<List<String>> listCountries() {
        log.info("GET /wine-routes/countries - Listing all countries");
        List<String> countries = wineRouteService.listCountries();
        return ResponseEntity.ok(countries);
    }

    @Override
    @GetMapping("/countries/{country}/regions")
    public ResponseEntity<List<String>> listRegions(
            @PathVariable String country) {
        log.info("GET /wine-routes/countries/{}/regions - Listing regions", country);
        List<String> regions = wineRouteService.listRegions(country);
        return ResponseEntity.ok(regions);
    }

    @Override
    @GetMapping("/countries/{country}/regions/{region}/subregions")
    public ResponseEntity<List<String>> listSubregions(
            @PathVariable String country,
            @PathVariable String region) {
        log.info("GET /wine-routes/countries/{}/regions/{}/subregions - Listing subregions",
                country, region);
        List<String> subregions = wineRouteService.listSubregions(country, region);
        return ResponseEntity.ok(subregions);
    }

    // =========================================================================
    // Route Listing & Details
    // =========================================================================

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<WineRouteSummaryResponse>> listRoutes(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String subregion,
            @RequestParam(required = false) String search,
            Pageable pageable) {

        log.info("GET /wine-routes - Listing routes (country={}, region={}, subregion={}, search={})",
                country, region, subregion, search);

        Page<WineRouteSummaryResponse> routes;

        if (Objects.nonNull(search) && !search.isBlank()) {
            routes = wineRouteService.searchRoutes(search, pageable);
        } else {
            routes = wineRouteService.listRoutes(country, region, subregion, pageable);
        }

        return ResponseEntity.ok(PageResponse.from(routes));
    }

    @Override
    @GetMapping("/{routeId}")
    public ResponseEntity<WineRouteDetailsResponse> getRouteDetails(
            @PathVariable UUID routeId) {
        log.info("GET /wine-routes/{} - Getting route details", routeId);
        WineRouteDetailsResponse details = wineRouteService.getRouteDetails(routeId);
        return ResponseEntity.ok(details);
    }
}


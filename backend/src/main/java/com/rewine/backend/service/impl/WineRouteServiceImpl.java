package com.rewine.backend.service.impl;

import com.rewine.backend.dto.response.WineRouteDetailsResponse;
import com.rewine.backend.dto.response.WineRouteHierarchyResponse;
import com.rewine.backend.dto.response.WineRouteHierarchyResponse.CountryNode;
import com.rewine.backend.dto.response.WineRouteHierarchyResponse.RegionNode;
import com.rewine.backend.dto.response.WineRouteHierarchyResponse.SubregionNode;
import com.rewine.backend.dto.response.WineRouteSummaryResponse;
import com.rewine.backend.exception.ResourceNotFoundException;
import com.rewine.backend.model.entity.WineRouteEntity;
import com.rewine.backend.repository.IWineRouteRepository;
import com.rewine.backend.service.IWineRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Wine route service implementation.
 * Provides hierarchical browsing and route retrieval functionality.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WineRouteServiceImpl implements IWineRouteService {

    private final IWineRouteRepository wineRouteRepository;

    // =========================================================================
    // Hierarchical Browsing
    // =========================================================================

    @Override
    public WineRouteHierarchyResponse getHierarchy() {
        log.debug("Building wine route hierarchy");

        List<String> countries = wineRouteRepository.findDistinctCountries();
        List<CountryNode> countryNodes = new ArrayList<>();

        for (String country : countries) {
            long countryRouteCount = wineRouteRepository.countActiveByCountry(country);
            List<String> regions = wineRouteRepository.findDistinctRegionsByCountry(country);
            List<RegionNode> regionNodes = new ArrayList<>();

            for (String region : regions) {
                long regionRouteCount = wineRouteRepository.countActiveByCountryAndRegion(country, region);
                List<String> subregions = wineRouteRepository
                        .findDistinctSubregionsByCountryAndRegion(country, region);
                List<SubregionNode> subregionNodes = subregions.stream()
                        .map(subregion -> SubregionNode.builder()
                                .name(subregion)
                                .routeCount(0L) // Could add count query if needed
                                .build())
                        .toList();

                regionNodes.add(RegionNode.builder()
                        .name(region)
                        .routeCount(regionRouteCount)
                        .subregions(subregionNodes)
                        .build());
            }

            countryNodes.add(CountryNode.builder()
                    .name(country)
                    .routeCount(countryRouteCount)
                    .regions(regionNodes)
                    .build());
        }

        log.info("Built hierarchy with {} countries", countryNodes.size());
        return WineRouteHierarchyResponse.builder()
                .countries(countryNodes)
                .build();
    }

    @Override
    public List<String> listCountries() {
        log.debug("Listing all countries with wine routes");
        List<String> countries = wineRouteRepository.findDistinctCountries();
        log.info("Found {} countries", countries.size());
        return countries;
    }

    @Override
    public List<String> listRegions(String country) {
        log.debug("Listing regions for country: {}", country);
        List<String> regions = wineRouteRepository.findDistinctRegionsByCountry(country);
        log.info("Found {} regions in {}", regions.size(), country);
        return regions;
    }

    @Override
    public List<String> listSubregions(String country, String region) {
        log.debug("Listing subregions for country: {}, region: {}", country, region);
        List<String> subregions = wineRouteRepository
                .findDistinctSubregionsByCountryAndRegion(country, region);
        log.info("Found {} subregions in {}/{}", subregions.size(), country, region);
        return subregions;
    }

    // =========================================================================
    // Route Listing & Details
    // =========================================================================

    @Override
    public Page<WineRouteSummaryResponse> listRoutes(
            String country,
            String region,
            String subregion,
            Pageable pageable) {

        log.debug("Listing routes - country: {}, region: {}, subregion: {}",
                country, region, subregion);

        Page<WineRouteEntity> routes;

        if (Objects.nonNull(country) && Objects.nonNull(region) && Objects.nonNull(subregion)) {
            routes = wineRouteRepository.findActiveByCountryAndRegionAndSubregion(
                    country, region, subregion, pageable);
        } else if (Objects.nonNull(country) && Objects.nonNull(region)) {
            routes = wineRouteRepository.findActiveByCountryAndRegion(country, region, pageable);
        } else if (Objects.nonNull(country)) {
            routes = wineRouteRepository.findActiveByCountry(country, pageable);
        } else {
            routes = wineRouteRepository.findAllActive(pageable);
        }

        log.info("Found {} routes (page {} of {})",
                routes.getNumberOfElements(),
                routes.getNumber() + 1,
                routes.getTotalPages());

        return routes.map(WineRouteSummaryResponse::fromEntity);
    }

    @Override
    public WineRouteDetailsResponse getRouteDetails(UUID routeId) {
        log.debug("Getting route details for ID: {}", routeId);

        WineRouteEntity route = wineRouteRepository.findByIdWithWineries(routeId)
                .orElseThrow(() -> {
                    log.warn("Wine route not found: {}", routeId);
                    return new ResourceNotFoundException("Wine route not found: " + routeId);
                });

        log.info("Retrieved route details: {} ({})", route.getName(), route.getId());
        return WineRouteDetailsResponse.fromEntity(route);
    }

    @Override
    public Page<WineRouteSummaryResponse> searchRoutes(String query, Pageable pageable) {
        log.debug("Searching routes with query: {}", query);

        if (Objects.isNull(query) || query.isBlank()) {
            return listRoutes(null, null, null, pageable);
        }

        Page<WineRouteEntity> routes = wineRouteRepository.search(query.trim(), pageable);
        log.info("Search found {} routes for query: '{}'", routes.getTotalElements(), query);

        return routes.map(WineRouteSummaryResponse::fromEntity);
    }
}


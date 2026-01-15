package com.rewine.backend.service;

import com.rewine.backend.dto.response.WineRouteDetailsResponse;
import com.rewine.backend.dto.response.WineRouteHierarchyResponse;
import com.rewine.backend.dto.response.WineRouteSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for wine route operations.
 */
public interface IWineRouteService {

    // =========================================================================
    // Hierarchical Browsing
    // =========================================================================

    /**
     * Gets the complete hierarchy of countries, regions, and subregions.
     *
     * @return the hierarchy response
     */
    WineRouteHierarchyResponse getHierarchy();

    /**
     * Gets all distinct countries with active routes.
     *
     * @return list of country names
     */
    List<String> listCountries();

    /**
     * Gets all distinct regions for a country.
     *
     * @param country the country name
     * @return list of region names
     */
    List<String> listRegions(String country);

    /**
     * Gets all distinct subregions for a country and region.
     *
     * @param country the country name
     * @param region  the region name
     * @return list of subregion names
     */
    List<String> listSubregions(String country, String region);

    // =========================================================================
    // Route Listing & Details
    // =========================================================================

    /**
     * Lists all active routes with optional filters.
     *
     * @param country   optional country filter
     * @param region    optional region filter
     * @param subregion optional subregion filter
     * @param pageable  pagination info
     * @return page of route summaries
     */
    Page<WineRouteSummaryResponse> listRoutes(
            String country,
            String region,
            String subregion,
            Pageable pageable);

    /**
     * Gets detailed information for a specific route.
     *
     * @param routeId the route ID
     * @return the route details
     */
    WineRouteDetailsResponse getRouteDetails(UUID routeId);

    /**
     * Searches routes by name or description.
     *
     * @param query    the search query
     * @param pageable pagination info
     * @return page of matching routes
     */
    Page<WineRouteSummaryResponse> searchRoutes(String query, Pageable pageable);
}


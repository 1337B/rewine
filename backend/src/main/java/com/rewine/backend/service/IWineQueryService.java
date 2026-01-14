package com.rewine.backend.service;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.WineSearchRequest;
import com.rewine.backend.dto.response.WineDetailsResponse;
import com.rewine.backend.dto.response.WineSummaryResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Interface for wine query operations (read-only).
 */
public interface IWineQueryService {

    /**
     * Searches wines with filters and pagination.
     *
     * @param request  the search request with filters
     * @param pageable pagination parameters
     * @return paginated wine summaries
     */
    PageResponse<WineSummaryResponse> searchWines(WineSearchRequest request, Pageable pageable);

    /**
     * Gets wine details by ID.
     *
     * @param id the wine ID
     * @return the wine details
     */
    WineDetailsResponse getWineDetails(UUID id);

    /**
     * Gets featured wines.
     *
     * @param pageable pagination parameters
     * @return paginated featured wines
     */
    PageResponse<WineSummaryResponse> getFeaturedWines(Pageable pageable);

    /**
     * Gets top-rated wines.
     *
     * @param pageable pagination parameters
     * @return paginated top-rated wines
     */
    PageResponse<WineSummaryResponse> getTopRatedWines(Pageable pageable);

    /**
     * Gets recently added wines.
     *
     * @param pageable pagination parameters
     * @return paginated recently added wines
     */
    PageResponse<WineSummaryResponse> getRecentWines(Pageable pageable);
}


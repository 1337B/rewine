package com.rewine.backend.utils.builder;

import com.rewine.backend.dto.response.WineDetailsResponse;

import java.util.UUID;

/**
 * Aggregator for building rich wine details responses.
 * Combines wine core info, ratings distribution, featured reviews, and AI profile status.
 */
public interface IWineDetailsAggregator {

    /**
     * Builds a rich wine details response aggregating multiple data sources.
     *
     * @param wineId the wine ID
     * @param userId the optional user ID (for personalized data like "liked by user")
     * @return the aggregated wine details response
     */
    WineDetailsResponse aggregate(UUID wineId, UUID userId);
}


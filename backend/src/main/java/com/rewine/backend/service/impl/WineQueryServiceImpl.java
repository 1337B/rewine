package com.rewine.backend.service.impl;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.WineSearchRequest;
import com.rewine.backend.dto.response.WineDetailsResponse;
import com.rewine.backend.dto.response.WineSummaryResponse;
import com.rewine.backend.exception.ErrorCode;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.repository.IWineRepository;
import com.rewine.backend.service.IWineQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of wine query service.
 */
@Service
@Transactional(readOnly = true)
public class WineQueryServiceImpl implements IWineQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WineQueryServiceImpl.class);

    private final IWineRepository wineRepository;

    public WineQueryServiceImpl(IWineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }

    @Override
    public PageResponse<WineSummaryResponse> searchWines(WineSearchRequest request, Pageable pageable) {
        LOGGER.info("Searching wines with filters: {}", request);

        // Apply sorting from request
        Pageable sortedPageable = applySorting(request, pageable);

        Page<WineEntity> page;

        // Determine which query to use based on filters
        if (Objects.nonNull(request) && request.hasFilters()) {
            if (Objects.nonNull(request.getSearch()) && !request.getSearch().isBlank()) {
                // Full text search
                LOGGER.debug("Performing text search for: {}", request.getSearch());
                page = wineRepository.searchWines(request.getSearch().trim(), sortedPageable);
            } else if (Boolean.TRUE.equals(request.getFeatured())) {
                // Featured wines only
                LOGGER.debug("Fetching featured wines");
                page = wineRepository.findByIsFeaturedTrueAndIsActiveTrue(sortedPageable);
            } else {
                // Advanced filtering
                LOGGER.debug("Applying advanced filters");
                page = wineRepository.findByFilters(
                        request.getWineType(),
                        request.getCountry(),
                        request.getRegion(),
                        request.getMinPrice(),
                        request.getMaxPrice(),
                        request.getMinRating(),
                        request.getVintage(),
                        sortedPageable
                );
            }
        } else {
            // No filters - return all active wines
            LOGGER.debug("Fetching all active wines");
            page = wineRepository.findByIsActiveTrueOrderByCreatedAtDesc(sortedPageable);
        }

        // Map to DTOs
        List<WineSummaryResponse> content = page.getContent().stream()
                .map(WineSummaryResponse::fromEntity)
                .toList();

        LOGGER.info("Found {} wines (page {} of {})", content.size(), page.getNumber(), page.getTotalPages());

        return PageResponse.of(page, content);
    }

    @Override
    public WineDetailsResponse getWineDetails(UUID id) {
        LOGGER.info("Getting wine details for ID: {}", id);

        if (Objects.isNull(id)) {
            throw new RewineException(
                    ErrorCode.VALIDATION_ERROR,
                    HttpStatus.BAD_REQUEST,
                    "Wine ID is required"
            );
        }

        WineEntity wine = wineRepository.findByIdWithWinery(id)
                .orElseThrow(() -> {
                    LOGGER.warn("Wine not found with ID: {}", id);
                    return new RewineException(
                            ErrorCode.WINE_NOT_FOUND,
                            HttpStatus.NOT_FOUND,
                            "Wine not found with ID: " + id
                    );
                });

        LOGGER.debug("Found wine: {} ({})", wine.getName(), wine.getWineType());

        return WineDetailsResponse.fromEntity(wine);
    }

    @Override
    public PageResponse<WineSummaryResponse> getFeaturedWines(Pageable pageable) {
        LOGGER.info("Getting featured wines");

        Page<WineEntity> page = wineRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable);

        List<WineSummaryResponse> content = page.getContent().stream()
                .map(WineSummaryResponse::fromEntity)
                .toList();

        LOGGER.info("Found {} featured wines", content.size());

        return PageResponse.of(page, content);
    }

    @Override
    public PageResponse<WineSummaryResponse> getTopRatedWines(Pageable pageable) {
        LOGGER.info("Getting top-rated wines");

        Page<WineEntity> page = wineRepository.findTopRated(pageable);

        List<WineSummaryResponse> content = page.getContent().stream()
                .map(WineSummaryResponse::fromEntity)
                .toList();

        LOGGER.info("Found {} top-rated wines", content.size());

        return PageResponse.of(page, content);
    }

    @Override
    public PageResponse<WineSummaryResponse> getRecentWines(Pageable pageable) {
        LOGGER.info("Getting recently added wines");

        Page<WineEntity> page = wineRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);

        List<WineSummaryResponse> content = page.getContent().stream()
                .map(WineSummaryResponse::fromEntity)
                .toList();

        LOGGER.info("Found {} recent wines", content.size());

        return PageResponse.of(page, content);
    }

    /**
     * Applies sorting from the search request to the pageable.
     *
     * @param request  the search request
     * @param pageable the original pageable
     * @return pageable with sorting applied
     */
    private Pageable applySorting(WineSearchRequest request, Pageable pageable) {
        if (Objects.isNull(request) || Objects.isNull(request.getSortBy())) {
            return pageable;
        }

        Sort.Direction direction = request.getSortDirection() == WineSearchRequest.SortDirection.DESC
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, request.getSortBy().getFieldName());

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}


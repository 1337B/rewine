package com.rewine.backend.repository;

import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.enums.WineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Wine entity with advanced search capabilities.
 */
@Repository
public interface IWineRepository extends JpaRepository<WineEntity, UUID>, JpaSpecificationExecutor<WineEntity> {

    /**
     * Finds a wine by ID with winery eagerly loaded.
     *
     * @param id the wine ID
     * @return optional containing wine with winery
     */
    @Query("SELECT w FROM WineEntity w LEFT JOIN FETCH w.winery WHERE w.id = :id")
    Optional<WineEntity> findByIdWithWinery(@Param("id") UUID id);

    /**
     * Finds wines by type.
     *
     * @param wineType the wine type
     * @param pageable pagination info
     * @return page of wines
     */
    Page<WineEntity> findByWineTypeAndIsActiveTrue(WineType wineType, Pageable pageable);

    /**
     * Finds wines by winery ID.
     *
     * @param wineryId the winery ID
     * @param pageable pagination info
     * @return page of wines
     */
    Page<WineEntity> findByWineryIdAndIsActiveTrue(UUID wineryId, Pageable pageable);

    /**
     * Searches wines by name (case-insensitive).
     *
     * @param name     the search term
     * @param pageable pagination info
     * @return page of wines matching the name
     */
    @Query("SELECT w FROM WineEntity w WHERE w.isActive = true AND LOWER(w.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<WineEntity> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Searches wines by multiple criteria.
     *
     * @param search   the search term (matches name, winery name, or description)
     * @param pageable pagination info
     * @return page of matching wines
     */
    @Query("SELECT w FROM WineEntity w LEFT JOIN w.winery wr WHERE w.isActive = true AND ("
            + "LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(wr.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(w.descriptionEn) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<WineEntity> searchWines(@Param("search") String search, Pageable pageable);

    /**
     * Finds wines with advanced filtering.
     *
     * @param wineType   optional wine type filter
     * @param country    optional country filter
     * @param region     optional region filter
     * @param minPrice   optional minimum price filter
     * @param maxPrice   optional maximum price filter
     * @param minRating  optional minimum rating filter
     * @param vintage    optional vintage filter
     * @param pageable   pagination info
     * @return page of matching wines
     */
    @Query("SELECT w FROM WineEntity w LEFT JOIN w.winery wr WHERE w.isActive = true "
            + "AND (:wineType IS NULL OR w.wineType = :wineType) "
            + "AND (:country IS NULL OR wr.country = :country) "
            + "AND (:region IS NULL OR wr.region = :region) "
            + "AND (:minPrice IS NULL OR w.priceMin >= :minPrice) "
            + "AND (:maxPrice IS NULL OR w.priceMax <= :maxPrice) "
            + "AND (:minRating IS NULL OR w.ratingAverage >= :minRating) "
            + "AND (:vintage IS NULL OR w.vintage = :vintage)")
    Page<WineEntity> findByFilters(
            @Param("wineType") WineType wineType,
            @Param("country") String country,
            @Param("region") String region,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") BigDecimal minRating,
            @Param("vintage") Integer vintage,
            Pageable pageable);

    /**
     * Finds featured wines.
     *
     * @param pageable pagination info
     * @return page of featured wines
     */
    Page<WineEntity> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);

    /**
     * Finds top-rated wines.
     *
     * @param pageable pagination info
     * @return page of top-rated wines
     */
    @Query("SELECT w FROM WineEntity w WHERE w.isActive = true AND w.ratingAverage IS NOT NULL "
            + "ORDER BY w.ratingAverage DESC, w.ratingCount DESC")
    Page<WineEntity> findTopRated(Pageable pageable);

    /**
     * Finds recently added wines.
     *
     * @param pageable pagination info
     * @return page of recently added wines
     */
    Page<WineEntity> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Finds wines by IDs.
     *
     * @param ids the list of wine IDs
     * @return list of wines
     */
    List<WineEntity> findByIdIn(List<UUID> ids);

    /**
     * Counts wines by winery.
     *
     * @param wineryId the winery ID
     * @return count of wines
     */
    long countByWineryIdAndIsActiveTrue(UUID wineryId);

    /**
     * Checks if wine exists by name and winery.
     *
     * @param name     the wine name
     * @param wineryId the winery ID
     * @return true if exists
     */
    boolean existsByNameAndWineryId(String name, UUID wineryId);
}


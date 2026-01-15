package com.rewine.backend.repository;

import com.rewine.backend.model.entity.WineRouteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Wine Route entity.
 */
@Repository
public interface IWineRouteRepository extends JpaRepository<WineRouteEntity, UUID> {

    // =====================================================================
    // Basic Queries
    // =====================================================================

    Page<WineRouteEntity> findByRegion(String region, Pageable pageable);

    Page<WineRouteEntity> findByStatus(String status, Pageable pageable);

    Page<WineRouteEntity> findByCreatedById(UUID userId, Pageable pageable);

    @Query("SELECT wr FROM WineRouteEntity wr WHERE "
            + "LOWER(wr.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(wr.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<WineRouteEntity> search(@Param("search") String search, Pageable pageable);

    // =====================================================================
    // Hierarchical Browsing Queries
    // =====================================================================

    /**
     * Gets all distinct countries with active routes.
     *
     * @return list of countries
     */
    @Query("SELECT DISTINCT wr.country FROM WineRouteEntity wr "
            + "WHERE wr.status = 'active' AND wr.country IS NOT NULL "
            + "ORDER BY wr.country")
    List<String> findDistinctCountries();

    /**
     * Gets all distinct regions for a country with active routes.
     *
     * @param country the country
     * @return list of regions
     */
    @Query("SELECT DISTINCT wr.region FROM WineRouteEntity wr "
            + "WHERE wr.status = 'active' AND wr.country = :country AND wr.region IS NOT NULL "
            + "ORDER BY wr.region")
    List<String> findDistinctRegionsByCountry(@Param("country") String country);

    /**
     * Gets all distinct subregions for a country and region with active routes.
     *
     * @param country the country
     * @param region  the region
     * @return list of subregions
     */
    @Query("SELECT DISTINCT wr.subregion FROM WineRouteEntity wr "
            + "WHERE wr.status = 'active' AND wr.country = :country AND wr.region = :region "
            + "AND wr.subregion IS NOT NULL "
            + "ORDER BY wr.subregion")
    List<String> findDistinctSubregionsByCountryAndRegion(
            @Param("country") String country,
            @Param("region") String region);

    // =====================================================================
    // Filtered Queries
    // =====================================================================

    /**
     * Finds active routes by country.
     *
     * @param country  the country
     * @param pageable pagination
     * @return page of routes
     */
    @Query("SELECT wr FROM WineRouteEntity wr "
            + "WHERE wr.status = 'active' AND wr.country = :country")
    Page<WineRouteEntity> findActiveByCountry(
            @Param("country") String country,
            Pageable pageable);

    /**
     * Finds active routes by country and region.
     *
     * @param country  the country
     * @param region   the region
     * @param pageable pagination
     * @return page of routes
     */
    @Query("SELECT wr FROM WineRouteEntity wr "
            + "WHERE wr.status = 'active' AND wr.country = :country AND wr.region = :region")
    Page<WineRouteEntity> findActiveByCountryAndRegion(
            @Param("country") String country,
            @Param("region") String region,
            Pageable pageable);

    /**
     * Finds active routes by country, region, and subregion.
     *
     * @param country   the country
     * @param region    the region
     * @param subregion the subregion
     * @param pageable  pagination
     * @return page of routes
     */
    @Query("SELECT wr FROM WineRouteEntity wr "
            + "WHERE wr.status = 'active' AND wr.country = :country "
            + "AND wr.region = :region AND wr.subregion = :subregion")
    Page<WineRouteEntity> findActiveByCountryAndRegionAndSubregion(
            @Param("country") String country,
            @Param("region") String region,
            @Param("subregion") String subregion,
            Pageable pageable);

    /**
     * Finds all active routes.
     *
     * @param pageable pagination
     * @return page of active routes
     */
    @Query("SELECT wr FROM WineRouteEntity wr WHERE wr.status = 'active'")
    Page<WineRouteEntity> findAllActive(Pageable pageable);

    /**
     * Finds a route by ID with wineries eagerly loaded.
     *
     * @param id the route ID
     * @return optional route with wineries
     */
    @Query("SELECT wr FROM WineRouteEntity wr "
            + "LEFT JOIN FETCH wr.wineries "
            + "WHERE wr.id = :id")
    Optional<WineRouteEntity> findByIdWithWineries(@Param("id") UUID id);

    /**
     * Counts active routes by country.
     *
     * @param country the country
     * @return count of routes
     */
    @Query("SELECT COUNT(wr) FROM WineRouteEntity wr "
            + "WHERE wr.status = 'active' AND wr.country = :country")
    long countActiveByCountry(@Param("country") String country);

    /**
     * Counts active routes by country and region.
     *
     * @param country the country
     * @param region  the region
     * @return count of routes
     */
    @Query("SELECT COUNT(wr) FROM WineRouteEntity wr "
            + "WHERE wr.status = 'active' AND wr.country = :country AND wr.region = :region")
    long countActiveByCountryAndRegion(
            @Param("country") String country,
            @Param("region") String region);
}


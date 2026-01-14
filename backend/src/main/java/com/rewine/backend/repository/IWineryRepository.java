package com.rewine.backend.repository;

import com.rewine.backend.model.entity.WineryEntity;
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
 * Repository for Winery entity.
 */
@Repository
public interface IWineryRepository extends JpaRepository<WineryEntity, UUID> {

    /**
     * Finds a winery by name (case-insensitive).
     *
     * @param name the winery name
     * @return optional containing winery if found
     */
    Optional<WineryEntity> findByNameIgnoreCase(String name);

    /**
     * Checks if a winery exists by name (case-insensitive).
     *
     * @param name the winery name
     * @return true if exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Finds wineries by country.
     *
     * @param country  the country
     * @param pageable pagination info
     * @return page of wineries
     */
    Page<WineryEntity> findByCountry(String country, Pageable pageable);

    /**
     * Finds wineries by region.
     *
     * @param region   the region
     * @param pageable pagination info
     * @return page of wineries
     */
    Page<WineryEntity> findByRegion(String region, Pageable pageable);

    /**
     * Finds wineries by country and region.
     *
     * @param country  the country
     * @param region   the region
     * @param pageable pagination info
     * @return page of wineries
     */
    Page<WineryEntity> findByCountryAndRegion(String country, String region, Pageable pageable);

    /**
     * Searches wineries by name.
     *
     * @param search   the search term
     * @param pageable pagination info
     * @return page of matching wineries
     */
    @Query("SELECT w FROM WineryEntity w WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<WineryEntity> searchByName(@Param("search") String search, Pageable pageable);

    /**
     * Gets distinct countries.
     *
     * @return list of countries
     */
    @Query("SELECT DISTINCT w.country FROM WineryEntity w ORDER BY w.country")
    List<String> findDistinctCountries();

    /**
     * Gets distinct regions by country.
     *
     * @param country the country
     * @return list of regions
     */
    @Query("SELECT DISTINCT w.region FROM WineryEntity w WHERE w.country = :country AND w.region IS NOT NULL ORDER BY w.region")
    List<String> findDistinctRegionsByCountry(@Param("country") String country);

    /**
     * Finds wineries by IDs.
     *
     * @param ids the list of winery IDs
     * @return list of wineries
     */
    List<WineryEntity> findByIdIn(List<UUID> ids);
}


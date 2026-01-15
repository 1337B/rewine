package com.rewine.backend.repository;

import com.rewine.backend.model.entity.WineAiProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for wine AI profile entities.
 */
@Repository
public interface IWineAiProfileRepository extends JpaRepository<WineAiProfileEntity, UUID> {

    /**
     * Finds an AI profile by wine ID and language.
     *
     * @param wineId   the wine ID
     * @param language the language code (e.g., "es-AR", "en-US")
     * @return the AI profile if found
     */
    @Query("SELECT wap FROM WineAiProfileEntity wap WHERE wap.wine.id = :wineId AND wap.language = :language")
    Optional<WineAiProfileEntity> findByWineIdAndLanguage(
            @Param("wineId") UUID wineId,
            @Param("language") String language
    );

    /**
     * Checks if an AI profile exists for a wine and language combination.
     *
     * @param wineId   the wine ID
     * @param language the language code
     * @return true if profile exists
     */
    @Query("SELECT CASE WHEN COUNT(wap) > 0 THEN true ELSE false END FROM WineAiProfileEntity wap WHERE wap.wine.id = :wineId AND wap.language = :language")
    boolean existsByWineIdAndLanguage(
            @Param("wineId") UUID wineId,
            @Param("language") String language
    );

    /**
     * Finds an AI profile by wine ID and language with the wine eagerly loaded.
     *
     * @param wineId   the wine ID
     * @param language the language code
     * @return the AI profile with wine loaded
     */
    @Query("SELECT wap FROM WineAiProfileEntity wap JOIN FETCH wap.wine WHERE wap.wine.id = :wineId AND wap.language = :language")
    Optional<WineAiProfileEntity> findByWineIdAndLanguageWithWine(
            @Param("wineId") UUID wineId,
            @Param("language") String language
    );

    /**
     * Counts the number of AI profiles for a specific wine.
     *
     * @param wineId the wine ID
     * @return the count of profiles
     */
    @Query("SELECT COUNT(wap) FROM WineAiProfileEntity wap WHERE wap.wine.id = :wineId")
    long countByWineId(@Param("wineId") UUID wineId);
}


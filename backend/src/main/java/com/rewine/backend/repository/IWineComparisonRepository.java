package com.rewine.backend.repository;

import com.rewine.backend.model.entity.WineComparisonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for wine comparison entities.
 */
@Repository
public interface IWineComparisonRepository extends JpaRepository<WineComparisonEntity, UUID> {

    /**
     * Finds a comparison by normalized wine pair and language.
     * Important: wine IDs must be in normalized order (wineAId < wineBId).
     *
     * @param wineAId  the first wine ID (must be < wineBId)
     * @param wineBId  the second wine ID (must be > wineAId)
     * @param language the language code (e.g., "es-AR", "en-US")
     * @return the comparison if found
     */
    @Query("SELECT wc FROM WineComparisonEntity wc "
            + "WHERE wc.wineA.id = :wineAId "
            + "AND wc.wineB.id = :wineBId "
            + "AND wc.language = :language")
    Optional<WineComparisonEntity> findByWineAIdAndWineBIdAndLanguage(
            @Param("wineAId") UUID wineAId,
            @Param("wineBId") UUID wineBId,
            @Param("language") String language
    );

    /**
     * Checks if a comparison exists for a normalized wine pair and language.
     * Important: wine IDs must be in normalized order (wineAId < wineBId).
     *
     * @param wineAId  the first wine ID (must be < wineBId)
     * @param wineBId  the second wine ID (must be > wineAId)
     * @param language the language code
     * @return true if comparison exists
     */
    @Query("SELECT CASE WHEN COUNT(wc) > 0 THEN true ELSE false END "
            + "FROM WineComparisonEntity wc "
            + "WHERE wc.wineA.id = :wineAId "
            + "AND wc.wineB.id = :wineBId "
            + "AND wc.language = :language")
    boolean existsByWineAIdAndWineBIdAndLanguage(
            @Param("wineAId") UUID wineAId,
            @Param("wineBId") UUID wineBId,
            @Param("language") String language
    );

    /**
     * Finds a comparison with both wines eagerly loaded.
     * Important: wine IDs must be in normalized order (wineAId < wineBId).
     *
     * @param wineAId  the first wine ID (must be < wineBId)
     * @param wineBId  the second wine ID (must be > wineAId)
     * @param language the language code
     * @return the comparison with wines loaded
     */
    @Query("SELECT wc FROM WineComparisonEntity wc "
            + "JOIN FETCH wc.wineA "
            + "JOIN FETCH wc.wineB "
            + "WHERE wc.wineA.id = :wineAId "
            + "AND wc.wineB.id = :wineBId "
            + "AND wc.language = :language")
    Optional<WineComparisonEntity> findByWineAIdAndWineBIdAndLanguageWithWines(
            @Param("wineAId") UUID wineAId,
            @Param("wineBId") UUID wineBId,
            @Param("language") String language
    );
}


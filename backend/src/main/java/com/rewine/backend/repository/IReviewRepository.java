package com.rewine.backend.repository;

import com.rewine.backend.model.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Review entity.
 */
@Repository
public interface IReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    Page<ReviewEntity> findByWineId(UUID wineId, Pageable pageable);

    Page<ReviewEntity> findByUserId(UUID userId, Pageable pageable);

    Optional<ReviewEntity> findByWineIdAndUserId(UUID wineId, UUID userId);

    boolean existsByWineIdAndUserId(UUID wineId, UUID userId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.wine.id = :wineId")
    Double calculateAverageRating(@Param("wineId") UUID wineId);

    long countByWineId(UUID wineId);
}


package com.rewine.backend.repository;

import com.rewine.backend.model.entity.WineRouteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for Wine Route entity.
 */
@Repository
public interface IWineRouteRepository extends JpaRepository<WineRouteEntity, UUID> {

    Page<WineRouteEntity> findByRegion(String region, Pageable pageable);

    Page<WineRouteEntity> findByStatus(String status, Pageable pageable);

    Page<WineRouteEntity> findByCreatedById(UUID userId, Pageable pageable);

    @Query("SELECT wr FROM WineRouteEntity wr WHERE "
            + "LOWER(wr.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(wr.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<WineRouteEntity> search(@Param("search") String search, Pageable pageable);
}


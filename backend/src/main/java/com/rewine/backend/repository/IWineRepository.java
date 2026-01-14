package com.rewine.backend.repository;

import com.rewine.backend.model.entity.WineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Wine entity.
 */
@Repository
public interface IWineRepository extends JpaRepository<WineEntity, UUID> {

    Page<WineEntity> findByType(String type, Pageable pageable);

    Page<WineEntity> findByRegion(String region, Pageable pageable);

    Page<WineEntity> findByWinery(String winery, Pageable pageable);

    @Query("SELECT w FROM WineEntity w WHERE " +
           "LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.winery) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<WineEntity> search(@Param("search") String search, Pageable pageable);

    List<WineEntity> findByIdIn(List<UUID> ids);
}


package com.rewine.backend.repository;

import com.rewine.backend.model.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

/**
 * Repository for Event entity.
 */
@Repository
public interface IEventRepository extends JpaRepository<EventEntity, UUID> {

    Page<EventEntity> findByType(String type, Pageable pageable);

    Page<EventEntity> findByStatus(String status, Pageable pageable);

    Page<EventEntity> findByLocationCity(String city, Pageable pageable);

    Page<EventEntity> findByOrganizerId(UUID organizerId, Pageable pageable);

    @Query("SELECT e FROM EventEntity e WHERE e.startDate >= :startDate AND e.status = 'published'")
    Page<EventEntity> findUpcomingEvents(@Param("startDate") Instant startDate, Pageable pageable);

    @Query("SELECT e FROM EventEntity e WHERE " +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<EventEntity> search(@Param("search") String search, Pageable pageable);
}


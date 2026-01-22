package com.rewine.backend.repository;

import com.rewine.backend.model.entity.EventEntity;
import com.rewine.backend.model.enums.EventStatus;
import com.rewine.backend.model.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Event entity.
 */
@Repository
public interface IEventRepository extends JpaRepository<EventEntity, UUID> {

    /**
     * Finds an event by ID with organizer eagerly loaded.
     *
     * @param id the event ID
     * @return optional containing the event with organizer
     */
    @Query("SELECT e FROM EventEntity e LEFT JOIN FETCH e.organizer WHERE e.id = :id")
    Optional<EventEntity> findByIdWithOrganizer(@Param("id") UUID id);

    /**
     * Finds events by type.
     *
     * @param type     the event type
     * @param pageable pagination info
     * @return page of events
     */
    Page<EventEntity> findByType(EventType type, Pageable pageable);

    /**
     * Finds events by status.
     *
     * @param status   the event status
     * @param pageable pagination info
     * @return page of events
     */
    Page<EventEntity> findByStatus(EventStatus status, Pageable pageable);

    /**
     * Finds events by location city.
     *
     * @param city     the city name
     * @param pageable pagination info
     * @return page of events
     */
    Page<EventEntity> findByLocationCity(String city, Pageable pageable);

    /**
     * Finds events by organizer ID.
     *
     * @param organizerId the organizer user ID
     * @param pageable    pagination info
     * @return page of events
     */
    Page<EventEntity> findByOrganizerId(UUID organizerId, Pageable pageable);

    /**
     * Finds upcoming published events.
     *
     * @param startDate minimum start date
     * @param pageable  pagination info
     * @return page of events
     */
    @Query("SELECT e FROM EventEntity e WHERE e.startDate >= :startDate AND e.status = 'PUBLISHED' "
            + "ORDER BY e.startDate ASC")
    Page<EventEntity> findUpcomingEvents(@Param("startDate") Instant startDate, Pageable pageable);

    /**
     * Finds events within a bounding box for geolocation queries.
     * This is used as a first-pass filter before applying Haversine.
     *
     * @param minLat   minimum latitude
     * @param maxLat   maximum latitude
     * @param minLon   minimum longitude
     * @param maxLon   maximum longitude
     * @param status   event status (typically PUBLISHED)
     * @param fromDate minimum start date
     * @return list of events within the bounding box
     */
    @Query("SELECT e FROM EventEntity e LEFT JOIN FETCH e.organizer "
            + "WHERE e.latitude IS NOT NULL AND e.longitude IS NOT NULL "
            + "AND e.latitude >= :minLat AND e.latitude <= :maxLat "
            + "AND e.longitude >= :minLon AND e.longitude <= :maxLon "
            + "AND e.status = :status "
            + "AND e.startDate >= :fromDate "
            + "ORDER BY e.startDate ASC")
    List<EventEntity> findEventsInBoundingBox(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLon") BigDecimal minLon,
            @Param("maxLon") BigDecimal maxLon,
            @Param("status") EventStatus status,
            @Param("fromDate") Instant fromDate
    );

    /**
     * Finds published events with optional filtering.
     *
     * @param status   event status
     * @param type     event type (optional)
     * @param city     city filter (optional)
     * @param region   region filter (optional)
     * @param fromDate minimum start date
     * @param pageable pagination info
     * @return page of events
     */
    @Query("SELECT e FROM EventEntity e LEFT JOIN FETCH e.organizer "
            + "WHERE e.status = :status "
            + "AND e.startDate >= :fromDate "
            + "AND (:type IS NULL OR e.type = :type) "
            + "AND (:city IS NULL OR LOWER(CAST(e.locationCity AS string)) LIKE LOWER(CAST(CONCAT('%', :city, '%') AS string))) "
            + "AND (:region IS NULL OR LOWER(CAST(e.locationRegion AS string)) LIKE LOWER(CAST(CONCAT('%', :region, '%') AS string)))")
    Page<EventEntity> findPublishedEvents(
            @Param("status") EventStatus status,
            @Param("type") EventType type,
            @Param("city") String city,
            @Param("region") String region,
            @Param("fromDate") Instant fromDate,
            Pageable pageable
    );

    /**
     * Searches events by title or description.
     *
     * @param search   search term
     * @param pageable pagination info
     * @return page of events
     */
    @Query("SELECT e FROM EventEntity e LEFT JOIN FETCH e.organizer WHERE "
            + "(LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND e.status = 'PUBLISHED'")
    Page<EventEntity> search(@Param("search") String search, Pageable pageable);

    /**
     * Counts events by organizer.
     *
     * @param organizerId the organizer user ID
     * @return count of events
     */
    long countByOrganizerId(UUID organizerId);
}


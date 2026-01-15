package com.rewine.backend.service;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.CreateEventRequest;
import com.rewine.backend.dto.request.UpdateEventRequest;
import com.rewine.backend.dto.response.EventDetailsResponse;
import com.rewine.backend.dto.response.EventSummaryResponse;
import com.rewine.backend.model.enums.EventType;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service interface for event operations.
 */
public interface IEventService {

    /**
     * Lists events near a geographic location within a radius.
     *
     * @param latitude  center latitude
     * @param longitude center longitude
     * @param radiusKm  radius in kilometers
     * @param pageable  pagination info
     * @return page of events sorted by distance
     */
    PageResponse<EventSummaryResponse> listNearbyEvents(
            BigDecimal latitude,
            BigDecimal longitude,
            Double radiusKm,
            Pageable pageable
    );

    /**
     * Lists published events with optional filtering.
     *
     * @param type     filter by event type (optional)
     * @param city     filter by city (optional)
     * @param region   filter by region (optional)
     * @param search   search term (optional)
     * @param pageable pagination info
     * @return page of events
     */
    PageResponse<EventSummaryResponse> listEvents(
            EventType type,
            String city,
            String region,
            String search,
            Pageable pageable
    );

    /**
     * Gets detailed information about an event.
     *
     * @param id the event ID
     * @return event details
     */
    EventDetailsResponse getEventDetails(UUID id);

    /**
     * Creates a new event.
     * Requires ROLE_PARTNER or ROLE_ADMIN.
     *
     * @param userId  the creating user's ID
     * @param request the creation request
     * @return the created event details
     */
    EventDetailsResponse createEvent(UUID userId, CreateEventRequest request);

    /**
     * Updates an existing event.
     * Requires the user to be the event owner or an admin.
     *
     * @param id      the event ID
     * @param userId  the updating user's ID
     * @param request the update request
     * @return the updated event details
     */
    EventDetailsResponse updateEvent(UUID id, UUID userId, UpdateEventRequest request);

    /**
     * Deletes an event.
     * Requires the user to be the event owner or an admin.
     *
     * @param id     the event ID
     * @param userId the deleting user's ID
     */
    void deleteEvent(UUID id, UUID userId);

    /**
     * Lists events organized by a specific user.
     *
     * @param organizerId the organizer's user ID
     * @param pageable    pagination info
     * @return page of events
     */
    PageResponse<EventSummaryResponse> listEventsByOrganizer(UUID organizerId, Pageable pageable);

    /**
     * Checks if a user can modify an event.
     *
     * @param eventId the event ID
     * @param userId  the user ID
     * @param isAdmin whether the user has admin role
     * @return true if the user can modify the event
     */
    boolean canModifyEvent(UUID eventId, UUID userId, boolean isAdmin);
}


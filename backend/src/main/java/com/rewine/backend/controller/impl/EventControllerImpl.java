package com.rewine.backend.controller.impl;

import com.rewine.backend.configuration.security.CustomUserDetails;
import com.rewine.backend.controller.IEventController;
import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.CreateEventRequest;
import com.rewine.backend.dto.request.UpdateEventRequest;
import com.rewine.backend.dto.response.EventDetailsResponse;
import com.rewine.backend.dto.response.EventSummaryResponse;
import com.rewine.backend.exception.ErrorCode;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.model.enums.EventType;
import com.rewine.backend.service.IEventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Event endpoints implementation.
 */
@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Wine event management endpoints")
public class EventControllerImpl implements IEventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventControllerImpl.class);
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final double DEFAULT_RADIUS_KM = 50.0;

    /**
     * Latitude boundary constants for validation.
     */
    private static final BigDecimal MIN_LATITUDE = BigDecimal.valueOf(-90);
    private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);

    /**
     * Longitude boundary constants for validation.
     */
    private static final BigDecimal MIN_LONGITUDE = BigDecimal.valueOf(-180);
    private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);

    private final IEventService eventService;

    public EventControllerImpl(IEventService eventService) {
        this.eventService = eventService;
    }

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<EventSummaryResponse>> listEvents(
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        LOGGER.info("GET /events - type={}, city={}, region={}, search={}, page={}, size={}",
                type, city, region, search, page, size);

        int pageSize = Math.min(size, MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by("startDate").ascending());

        PageResponse<EventSummaryResponse> response = eventService.listEvents(
                type, city, region, search, pageable
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/nearby")
    public ResponseEntity<PageResponse<EventSummaryResponse>> listNearbyEvents(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        LOGGER.info("GET /events/nearby - lat={}, lng={}, radius={}, page={}, size={}",
                latitude, longitude, radiusKm, page, size);

        // Validate coordinates
        if (latitude.compareTo(MIN_LATITUDE) < 0
                || latitude.compareTo(MAX_LATITUDE) > 0) {
            throw new RewineException(ErrorCode.VALIDATION_ERROR,
                    "Latitude must be between -90 and 90");
        }
        if (longitude.compareTo(MIN_LONGITUDE) < 0
                || longitude.compareTo(MAX_LONGITUDE) > 0) {
            throw new RewineException(ErrorCode.VALIDATION_ERROR,
                    "Longitude must be between -180 and 180");
        }

        int pageSize = Math.min(size, MAX_PAGE_SIZE);
        double searchRadius = Objects.nonNull(radiusKm) ? radiusKm : DEFAULT_RADIUS_KM;

        PageResponse<EventSummaryResponse> response = eventService.listNearbyEvents(
                latitude, longitude, searchRadius, PageRequest.of(page, pageSize)
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EventDetailsResponse> getEventDetails(@PathVariable UUID id) {
        LOGGER.info("GET /events/{} - Getting event details", id);

        EventDetailsResponse response = eventService.getEventDetails(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public ResponseEntity<EventDetailsResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request) {

        LOGGER.info("POST /events - Creating event: {}", request.getTitle());

        UUID userId = getCurrentUserId();
        EventDetailsResponse response = eventService.createEvent(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public ResponseEntity<EventDetailsResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest request) {

        LOGGER.info("PUT /events/{} - Updating event", id);

        UUID userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();

        // Check authorization
        if (!eventService.canModifyEvent(id, userId, isAdmin)) {
            throw new RewineException(ErrorCode.ACCESS_DENIED,
                    "You are not authorized to modify this event");
        }

        EventDetailsResponse response = eventService.updateEvent(id, userId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        LOGGER.info("DELETE /events/{} - Deleting event", id);

        UUID userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();

        // Check authorization
        if (!eventService.canModifyEvent(id, userId, isAdmin)) {
            throw new RewineException(ErrorCode.ACCESS_DENIED,
                    "You are not authorized to delete this event");
        }

        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/my-events")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<EventSummaryResponse>> listMyEvents(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        LOGGER.info("GET /events/my-events - page={}, size={}", page, size);

        UUID userId = getCurrentUserId();
        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        PageResponse<EventSummaryResponse> response = eventService.listEventsByOrganizer(
                userId, PageRequest.of(page, pageSize, Sort.by("startDate").descending())
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Gets the current authenticated user's ID.
     *
     * @return the user ID
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            throw new RewineException(ErrorCode.AUTHENTICATION_REQUIRED, "User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        throw new RewineException(ErrorCode.AUTHENTICATION_REQUIRED, "Unable to get user ID");
    }

    /**
     * Checks if the current user has admin role.
     *
     * @return true if admin
     */
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }
}


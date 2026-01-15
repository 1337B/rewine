package com.rewine.backend.service.impl;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.CreateEventRequest;
import com.rewine.backend.dto.request.UpdateEventRequest;
import com.rewine.backend.dto.response.EventDetailsResponse;
import com.rewine.backend.dto.response.EventSummaryResponse;
import com.rewine.backend.exception.ResourceNotFoundException;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.exception.ErrorCode;
import com.rewine.backend.model.entity.EventEntity;
import com.rewine.backend.model.entity.UserEntity;
import com.rewine.backend.model.enums.EventStatus;
import com.rewine.backend.model.enums.EventType;
import com.rewine.backend.repository.IEventRepository;
import com.rewine.backend.repository.IUserRepository;
import com.rewine.backend.service.IEventService;
import com.rewine.backend.utils.geo.IGeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Event service implementation.
 */
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements IEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);
    private static final double DEFAULT_RADIUS_KM = 50.0;
    private static final int MAX_NEARBY_RESULTS = 100;

    private final IEventRepository eventRepository;
    private final IUserRepository userRepository;
    private final IGeoUtils geoUtils;

    public EventServiceImpl(
            IEventRepository eventRepository,
            IUserRepository userRepository,
            IGeoUtils geoUtils) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.geoUtils = geoUtils;
    }

    @Override
    public PageResponse<EventSummaryResponse> listNearbyEvents(
            BigDecimal latitude,
            BigDecimal longitude,
            Double radiusKm,
            Pageable pageable) {

        LOGGER.info("Searching events near ({}, {}) within {} km",
                latitude, longitude, radiusKm);

        double searchRadius = Objects.nonNull(radiusKm) ? radiusKm : DEFAULT_RADIUS_KM;
        double lat = latitude.doubleValue();
        double lon = longitude.doubleValue();

        // Calculate bounding box for initial filtering
        IGeoUtils.BoundingBox bbox = geoUtils.calculateBoundingBox(lat, lon, searchRadius);

        LOGGER.debug("Bounding box: minLat={}, maxLat={}, minLon={}, maxLon={}",
                bbox.minLatitude(), bbox.maxLatitude(),
                bbox.minLongitude(), bbox.maxLongitude());

        // Fetch events within bounding box
        List<EventEntity> candidateEvents = eventRepository.findEventsInBoundingBox(
                BigDecimal.valueOf(bbox.minLatitude()),
                BigDecimal.valueOf(bbox.maxLatitude()),
                BigDecimal.valueOf(bbox.minLongitude()),
                BigDecimal.valueOf(bbox.maxLongitude()),
                EventStatus.PUBLISHED,
                Instant.now()
        );

        LOGGER.debug("Found {} events in bounding box", candidateEvents.size());

        // Apply Haversine filter and calculate actual distances
        List<EventWithDistance> eventsWithDistance = candidateEvents.stream()
                .map(event -> {
                    double eventLat = event.getLatitude().doubleValue();
                    double eventLon = event.getLongitude().doubleValue();
                    double distance = geoUtils.calculateDistance(lat, lon, eventLat, eventLon);
                    return new EventWithDistance(event, distance);
                })
                .filter(ewd -> ewd.distance <= searchRadius)
                .sorted(Comparator.comparingDouble(ewd -> ewd.distance))
                .collect(Collectors.toList());

        LOGGER.debug("After Haversine filter: {} events within radius", eventsWithDistance.size());

        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), eventsWithDistance.size());

        List<EventSummaryResponse> content;
        if (start >= eventsWithDistance.size()) {
            content = List.of();
        } else {
            content = eventsWithDistance.subList(start, end).stream()
                    .map(ewd -> EventSummaryResponse.fromEntityWithDistance(ewd.event, ewd.distance))
                    .collect(Collectors.toList());
        }

        int totalElements = eventsWithDistance.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return PageResponse.<EventSummaryResponse>builder()
                .content(content)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(pageable.getPageNumber() == 0)
                .last(pageable.getPageNumber() >= totalPages - 1)
                .hasNext(pageable.getPageNumber() < totalPages - 1)
                .hasPrevious(pageable.getPageNumber() > 0)
                .build();
    }

    @Override
    public PageResponse<EventSummaryResponse> listEvents(
            EventType type,
            String city,
            String region,
            String search,
            Pageable pageable) {

        LOGGER.info("Listing events: type={}, city={}, region={}, search={}",
                type, city, region, search);

        Page<EventEntity> page;

        if (Objects.nonNull(search) && !search.isBlank()) {
            page = eventRepository.search(search.trim(), pageable);
        } else {
            page = eventRepository.findPublishedEvents(
                    EventStatus.PUBLISHED,
                    type,
                    city,
                    region,
                    Instant.now(),
                    pageable
            );
        }

        List<EventSummaryResponse> content = page.getContent().stream()
                .map(EventSummaryResponse::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.of(page, content);
    }

    @Override
    public EventDetailsResponse getEventDetails(UUID id) {
        LOGGER.info("Getting event details: {}", id);

        EventEntity event = eventRepository.findByIdWithOrganizer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        return EventDetailsResponse.fromEntity(event);
    }

    @Override
    @Transactional
    public EventDetailsResponse createEvent(UUID userId, CreateEventRequest request) {
        LOGGER.info("Creating event for user {}: {}", userId, request.getTitle());

        // Validate end date is after start date
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RewineException(ErrorCode.VALIDATION_ERROR,
                    "End date must be after start date");
        }

        UserEntity organizer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        EventEntity event = EventEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .status(EventStatus.DRAFT)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .locationName(request.getLocationName())
                .locationAddress(request.getLocationAddress())
                .locationCity(request.getLocationCity())
                .locationRegion(request.getLocationRegion())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .price(request.getPrice())
                .maxAttendees(request.getMaxAttendees())
                .currentAttendees(0)
                .imageUrl(request.getImageUrl())
                .organizer(organizer)
                .organizerType(request.getOrganizerType())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .websiteUrl(request.getWebsiteUrl())
                .build();

        EventEntity savedEvent = eventRepository.save(event);
        LOGGER.info("Created event with ID: {}", savedEvent.getId());

        return EventDetailsResponse.fromEntity(savedEvent);
    }

    @Override
    @Transactional
    public EventDetailsResponse updateEvent(UUID id, UUID userId, UpdateEventRequest request) {
        LOGGER.info("Updating event {}: user={}", id, userId);

        EventEntity event = eventRepository.findByIdWithOrganizer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        // Update only provided fields
        if (Objects.nonNull(request.getTitle())) {
            event.setTitle(request.getTitle());
        }
        if (Objects.nonNull(request.getDescription())) {
            event.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getType())) {
            event.setType(request.getType());
        }
        if (Objects.nonNull(request.getStatus())) {
            event.setStatus(request.getStatus());
        }
        if (Objects.nonNull(request.getStartDate())) {
            event.setStartDate(request.getStartDate());
        }
        if (Objects.nonNull(request.getEndDate())) {
            event.setEndDate(request.getEndDate());
        }
        if (Objects.nonNull(request.getLocationName())) {
            event.setLocationName(request.getLocationName());
        }
        if (Objects.nonNull(request.getLocationAddress())) {
            event.setLocationAddress(request.getLocationAddress());
        }
        if (Objects.nonNull(request.getLocationCity())) {
            event.setLocationCity(request.getLocationCity());
        }
        if (Objects.nonNull(request.getLocationRegion())) {
            event.setLocationRegion(request.getLocationRegion());
        }
        if (Objects.nonNull(request.getLatitude())) {
            event.setLatitude(request.getLatitude());
        }
        if (Objects.nonNull(request.getLongitude())) {
            event.setLongitude(request.getLongitude());
        }
        if (Objects.nonNull(request.getPrice())) {
            event.setPrice(request.getPrice());
        }
        if (Objects.nonNull(request.getMaxAttendees())) {
            event.setMaxAttendees(request.getMaxAttendees());
        }
        if (Objects.nonNull(request.getImageUrl())) {
            event.setImageUrl(request.getImageUrl());
        }
        if (Objects.nonNull(request.getOrganizerType())) {
            event.setOrganizerType(request.getOrganizerType());
        }
        if (Objects.nonNull(request.getContactEmail())) {
            event.setContactEmail(request.getContactEmail());
        }
        if (Objects.nonNull(request.getContactPhone())) {
            event.setContactPhone(request.getContactPhone());
        }
        if (Objects.nonNull(request.getWebsiteUrl())) {
            event.setWebsiteUrl(request.getWebsiteUrl());
        }

        // Validate dates if both are set
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new RewineException(ErrorCode.VALIDATION_ERROR,
                    "End date must be after start date");
        }

        EventEntity savedEvent = eventRepository.save(event);
        LOGGER.info("Updated event: {}", savedEvent.getId());

        return EventDetailsResponse.fromEntity(savedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(UUID id, UUID userId) {
        LOGGER.info("Deleting event {}: user={}", id, userId);

        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        eventRepository.delete(event);
        LOGGER.info("Deleted event: {}", id);
    }

    @Override
    public PageResponse<EventSummaryResponse> listEventsByOrganizer(UUID organizerId, Pageable pageable) {
        LOGGER.info("Listing events for organizer: {}", organizerId);

        Page<EventEntity> page = eventRepository.findByOrganizerId(organizerId, pageable);

        List<EventSummaryResponse> content = page.getContent().stream()
                .map(EventSummaryResponse::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.of(page, content);
    }

    @Override
    public boolean canModifyEvent(UUID eventId, UUID userId, boolean isAdmin) {
        if (isAdmin) {
            return true;
        }

        return eventRepository.findById(eventId)
                .map(event -> event.isOrganizer(userId))
                .orElse(false);
    }

    /**
     * Helper record for event with calculated distance.
     */
    private record EventWithDistance(EventEntity event, double distance) {
    }
}


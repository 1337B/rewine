package com.rewine.backend.dto.response;

import com.rewine.backend.model.entity.EventEntity;
import com.rewine.backend.model.enums.EventStatus;
import com.rewine.backend.model.enums.EventType;
import com.rewine.backend.model.enums.OrganizerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Detailed response DTO for a single event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailsResponse {

    private UUID id;
    private String title;
    private String description;
    private EventType type;
    private EventStatus status;
    private Instant startDate;
    private Instant endDate;

    // Location
    private String locationName;
    private String locationAddress;
    private String locationCity;
    private String locationRegion;
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Capacity and pricing
    private BigDecimal price;
    private Integer maxAttendees;
    private Integer currentAttendees;
    private Integer availableSpots;
    private Boolean isAvailable;

    // Media
    private String imageUrl;

    // Organizer information
    private UUID organizerId;
    private String organizerName;
    private OrganizerType organizerType;

    // Contact information
    private String contactEmail;
    private String contactPhone;
    private String websiteUrl;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Creates a details response from an event entity.
     *
     * @param entity the event entity
     * @return the details response
     */
    public static EventDetailsResponse fromEntity(EventEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        Integer availableSpots = null;
        if (Objects.nonNull(entity.getMaxAttendees())) {
            availableSpots = Math.max(0, entity.getMaxAttendees() - entity.getCurrentAttendees());
        }

        return EventDetailsResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .type(entity.getType())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .locationName(entity.getLocationName())
                .locationAddress(entity.getLocationAddress())
                .locationCity(entity.getLocationCity())
                .locationRegion(entity.getLocationRegion())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .price(entity.getPrice())
                .maxAttendees(entity.getMaxAttendees())
                .currentAttendees(entity.getCurrentAttendees())
                .availableSpots(availableSpots)
                .isAvailable(entity.isAvailable())
                .imageUrl(entity.getImageUrl())
                .organizerId(Objects.nonNull(entity.getOrganizer()) ? entity.getOrganizer().getId() : null)
                .organizerName(Objects.nonNull(entity.getOrganizer()) ? entity.getOrganizer().getName() : null)
                .organizerType(entity.getOrganizerType())
                .contactEmail(entity.getContactEmail())
                .contactPhone(entity.getContactPhone())
                .websiteUrl(entity.getWebsiteUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}


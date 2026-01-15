package com.rewine.backend.dto.response;

import com.rewine.backend.model.entity.EventEntity;
import com.rewine.backend.model.enums.EventStatus;
import com.rewine.backend.model.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Summary response DTO for event listings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSummaryResponse {

    private UUID id;
    private String title;
    private EventType type;
    private EventStatus status;
    private Instant startDate;
    private Instant endDate;
    private String locationName;
    private String locationCity;
    private String locationRegion;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal price;
    private Integer maxAttendees;
    private Integer currentAttendees;
    private Integer availableSpots;
    private String imageUrl;
    private String organizerName;
    private UUID organizerId;

    /**
     * Distance from the search center in kilometers.
     * Only populated when searching by location.
     */
    private Double distanceKm;

    /**
     * Creates a summary response from an event entity.
     *
     * @param entity the event entity
     * @return the summary response
     */
    public static EventSummaryResponse fromEntity(EventEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        Integer availableSpots = null;
        if (Objects.nonNull(entity.getMaxAttendees())) {
            availableSpots = Math.max(0, entity.getMaxAttendees() - entity.getCurrentAttendees());
        }

        return EventSummaryResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .locationName(entity.getLocationName())
                .locationCity(entity.getLocationCity())
                .locationRegion(entity.getLocationRegion())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .price(entity.getPrice())
                .maxAttendees(entity.getMaxAttendees())
                .currentAttendees(entity.getCurrentAttendees())
                .availableSpots(availableSpots)
                .imageUrl(entity.getImageUrl())
                .organizerName(Objects.nonNull(entity.getOrganizer()) ? entity.getOrganizer().getName() : null)
                .organizerId(Objects.nonNull(entity.getOrganizer()) ? entity.getOrganizer().getId() : null)
                .build();
    }

    /**
     * Creates a summary response from an event entity with distance.
     *
     * @param entity     the event entity
     * @param distanceKm distance in kilometers
     * @return the summary response with distance
     */
    public static EventSummaryResponse fromEntityWithDistance(EventEntity entity, Double distanceKm) {
        EventSummaryResponse response = fromEntity(entity);
        if (Objects.nonNull(response)) {
            response.setDistanceKm(distanceKm);
        }
        return response;
    }
}


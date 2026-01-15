package com.rewine.backend.dto.request;

import com.rewine.backend.model.enums.EventType;
import com.rewine.backend.model.enums.OrganizerType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Request DTO for creating a new event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 10000, message = "Description must not exceed 10000 characters")
    private String description;

    @NotNull(message = "Event type is required")
    private EventType type;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private Instant startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private Instant endDate;

    // Location
    @Size(max = 255, message = "Location name must not exceed 255 characters")
    private String locationName;

    @Size(max = 255, message = "Location address must not exceed 255 characters")
    private String locationAddress;

    @Size(max = 255, message = "City must not exceed 255 characters")
    private String locationCity;

    @Size(max = 255, message = "Region must not exceed 255 characters")
    private String locationRegion;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    // Capacity and pricing
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;

    @Min(value = 1, message = "Max attendees must be at least 1")
    private Integer maxAttendees;

    // Media
    @URL(message = "Image URL must be a valid URL")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    // Organizer type
    @Builder.Default
    private OrganizerType organizerType = OrganizerType.PARTNER;

    // Contact information
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    private String contactEmail;

    @Size(max = 50, message = "Contact phone must not exceed 50 characters")
    private String contactPhone;

    @URL(message = "Website URL must be a valid URL")
    @Size(max = 500, message = "Website URL must not exceed 500 characters")
    private String websiteUrl;
}


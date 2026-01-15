package com.rewine.backend.controller;

import com.rewine.backend.dto.common.PageResponse;
import com.rewine.backend.dto.request.CreateEventRequest;
import com.rewine.backend.dto.request.UpdateEventRequest;
import com.rewine.backend.dto.response.EventDetailsResponse;
import com.rewine.backend.dto.response.EventSummaryResponse;
import com.rewine.backend.model.enums.EventType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Interface for event endpoints.
 */
@Tag(name = "Events", description = "Wine event management endpoints")
public interface IEventController {

    /**
     * Lists events with optional filtering.
     *
     * @param type   event type filter
     * @param city   city filter
     * @param region region filter
     * @param search search term
     * @param page   page number
     * @param size   page size
     * @return page of events
     */
    @Operation(
            summary = "List events",
            description = "Get a paginated list of published events with optional filtering"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Events retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<PageResponse<EventSummaryResponse>> listEvents(
            @Parameter(description = "Filter by event type") EventType type,
            @Parameter(description = "Filter by city") String city,
            @Parameter(description = "Filter by region") String region,
            @Parameter(description = "Search in title and description") String search,
            @Parameter(description = "Page number (0-indexed)") int page,
            @Parameter(description = "Page size") int size
    );

    /**
     * Lists events near a geographic location.
     *
     * @param latitude  center latitude
     * @param longitude center longitude
     * @param radiusKm  search radius in kilometers
     * @param page      page number
     * @param size      page size
     * @return page of events sorted by distance
     */
    @Operation(
            summary = "List nearby events",
            description = "Get events near a geographic location, sorted by distance"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Nearby events retrieved successfully"
            ),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates")
    })
    ResponseEntity<PageResponse<EventSummaryResponse>> listNearbyEvents(
            @Parameter(description = "Center latitude", required = true) BigDecimal latitude,
            @Parameter(description = "Center longitude", required = true) BigDecimal longitude,
            @Parameter(description = "Search radius in kilometers") Double radiusKm,
            @Parameter(description = "Page number") int page,
            @Parameter(description = "Page size") int size
    );

    /**
     * Gets event details by ID.
     *
     * @param id the event ID
     * @return the event details
     */
    @Operation(
            summary = "Get event details",
            description = "Get detailed information about a specific event"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event found",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    ResponseEntity<EventDetailsResponse> getEventDetails(
            @Parameter(description = "Event ID", required = true) UUID id
    );

    /**
     * Creates a new event.
     * Requires ROLE_PARTNER or ROLE_ADMIN.
     *
     * @param request the creation request
     * @return the created event
     */
    @Operation(
            summary = "Create event",
            description = "Create a new event. Requires ROLE_PARTNER or ROLE_ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Event created successfully",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    ResponseEntity<EventDetailsResponse> createEvent(CreateEventRequest request);

    /**
     * Updates an existing event.
     * Requires the user to be the event organizer or an admin.
     *
     * @param id      the event ID
     * @param request the update request
     * @return the updated event
     */
    @Operation(
            summary = "Update event",
            description = "Update an existing event. Requires ownership or ROLE_ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event updated successfully",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    ResponseEntity<EventDetailsResponse> updateEvent(
            @Parameter(description = "Event ID", required = true) UUID id,
            UpdateEventRequest request
    );

    /**
     * Deletes an event.
     * Requires the user to be the event organizer or an admin.
     *
     * @param id the event ID
     * @return no content
     */
    @Operation(
            summary = "Delete event",
            description = "Delete an event. Requires ownership or ROLE_ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    ResponseEntity<Void> deleteEvent(
            @Parameter(description = "Event ID", required = true) UUID id
    );

    /**
     * Lists events organized by the current user.
     *
     * @param page page number
     * @param size page size
     * @return page of events
     */
    @Operation(
            summary = "List my events",
            description = "Get events organized by the current user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    ResponseEntity<PageResponse<EventSummaryResponse>> listMyEvents(
            @Parameter(description = "Page number") int page,
            @Parameter(description = "Page size") int size
    );
}


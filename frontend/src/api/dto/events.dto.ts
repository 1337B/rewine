/**
 * Event DTOs
 *
 * Data Transfer Objects for event API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 * Note: Backend uses camelCase for all JSON fields.
 */

import type { EventType, EventStatus } from '@domain/event/event.types'

// ============================================================================
// Event DTOs
// ============================================================================

/**
 * Event summary for list views (matches backend EventSummaryResponse)
 */
export interface EventSummaryDto {
  id: string
  title: string
  type: EventType
  status: EventStatus
  startDate: string
  endDate: string
  locationName: string
  locationCity: string
  locationRegion?: string | null
  latitude?: number | null
  longitude?: number | null
  price?: number | null
  maxAttendees?: number | null
  currentAttendees?: number
  availableSpots?: number | null
  imageUrl?: string | null
  organizerName?: string | null
  organizerId?: string | null
  distanceKm?: number | null
}

/**
 * Full event details (matches backend EventDetailsResponse)
 */
export interface EventDetailsDto extends EventSummaryDto {
  description: string
  locationAddress?: string | null
  contactEmail?: string | null
  contactPhone?: string | null
  websiteUrl?: string | null
  createdAt: string
  updatedAt: string
}

/**
 * Event DTO (legacy alias)
 * @deprecated Use EventDetailsDto for full details, EventSummaryDto for lists
 */
export interface EventDto extends EventDetailsDto {}

// ============================================================================
// Attendee DTOs
// ============================================================================

/**
 * Event attendee
 */
export interface EventAttendeeDto {
  id: string
  eventId: string
  userId: string
  userName: string
  userAvatar?: string | null
  status: 'REGISTERED' | 'CONFIRMED' | 'CANCELLED' | 'ATTENDED'
  registeredAt: string
  confirmedAt?: string | null
}

// ============================================================================
// Request DTOs
// ============================================================================

/**
 * Create event request (matches backend CreateEventRequest)
 */
export interface CreateEventRequestDto {
  title: string
  description: string
  type: EventType
  startDate: string
  endDate: string
  locationName: string
  locationAddress?: string
  locationCity: string
  locationRegion?: string
  latitude?: number
  longitude?: number
  price?: number
  maxAttendees?: number
  imageUrl?: string
  contactEmail?: string
  contactPhone?: string
  websiteUrl?: string
}

/**
 * Update event request
 */
export interface UpdateEventRequestDto extends Partial<CreateEventRequestDto> {
  status?: EventStatus
}

/**
 * Event registration request
 */
export interface RegisterEventRequestDto {
  eventId: string
  notes?: string
}

/**
 * Event filter/search parameters
 */
export interface EventFilterParamsDto {
  search?: string
  type?: EventType
  city?: string
  region?: string
  country?: string
  startDate?: string
  endDate?: string
  minPrice?: number
  maxPrice?: number
  status?: EventStatus
  latitude?: number
  longitude?: number
  radiusKm?: number
  sortBy?: 'startDate' | 'price' | 'distance' | 'createdAt'
  sortDirection?: 'ASC' | 'DESC'
  page?: number
  size?: number
}

// ============================================================================
// Response DTOs
// ============================================================================

/**
 * Paginated events response (matches backend PageResponse)
 */
export interface EventsPageResponseDto {
  items: EventSummaryDto[]
  content: EventSummaryDto[]  // Legacy alias
  pageNumber: number
  pageSize: number
  totalItems: number
  totalPages: number
  first: boolean
  last: boolean
  hasNext: boolean
  hasPrevious: boolean
}

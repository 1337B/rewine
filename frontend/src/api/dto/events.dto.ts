/**
 * Event DTOs
 *
 * Data Transfer Objects for event API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 */

import type { EventType, EventStatus } from '@domain/event/event.types'

// ============================================================================
// Event DTOs
// ============================================================================

/**
 * Event summary for list views
 */
export interface EventSummaryDto {
  id: string
  title: string
  type: EventType
  start_date: string
  end_date: string
  location: EventLocationDto
  image_url?: string | null
  price?: number | null
  max_attendees?: number | null
  current_attendees?: number
  status: EventStatus
}

/**
 * Full event details
 */
export interface EventDetailsDto extends EventSummaryDto {
  description: string
  organizer: EventOrganizerDto
  tags?: string[]
  is_registered?: boolean
  is_full?: boolean
  created_at: string
  updated_at: string
}

/**
 * Event DTO (legacy alias)
 * @deprecated Use EventDetailsDto for full details, EventSummaryDto for lists
 */
export interface EventDto extends EventDetailsDto {}

/**
 * Event location structure
 */
export interface EventLocationDto {
  name: string
  address: string
  city: string
  region: string
  country: string
  latitude?: number | null
  longitude?: number | null
}

/**
 * Event organizer structure
 */
export interface EventOrganizerDto {
  id: string
  name: string
  email: string
  phone?: string | null
  avatar?: string | null
}

// ============================================================================
// Attendee DTOs
// ============================================================================

/**
 * Event attendee
 */
export interface EventAttendeeDto {
  id: string
  event_id: string
  user_id: string
  user_name: string
  user_avatar?: string | null
  status: 'registered' | 'confirmed' | 'cancelled' | 'attended'
  registered_at: string
  confirmed_at?: string | null
}

// ============================================================================
// Request DTOs
// ============================================================================

/**
 * Create event request
 */
export interface CreateEventRequestDto {
  title: string
  description: string
  type: EventType
  start_date: string
  end_date: string
  location: Omit<EventLocationDto, 'latitude' | 'longitude'> & {
    latitude?: number
    longitude?: number
  }
  price?: number
  max_attendees?: number
  tags?: string[]
  image_url?: string
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
  event_id: string
  notes?: string
}

/**
 * Event filter/search parameters
 */
export interface EventFilterParamsDto {
  search?: string
  type?: string | string[]
  city?: string
  region?: string
  country?: string
  start_date?: string
  end_date?: string
  min_price?: number
  max_price?: number
  status?: EventStatus | EventStatus[]
  has_availability?: boolean
  latitude?: number
  longitude?: number
  radius_km?: number
  sort_by?: 'date' | 'price' | 'popularity' | 'distance' | 'created_at'
  sort_order?: 'asc' | 'desc'
  page?: number
  page_size?: number
}

// ============================================================================
// Response DTOs
// ============================================================================

/**
 * Paginated events response
 */
export interface EventsPageResponseDto {
  data: EventSummaryDto[]
  pagination: {
    page: number
    page_size: number
    total_items: number
    total_pages: number
    has_next: boolean
    has_previous: boolean
  }
}

/**
 * Nearby events response with distance
 */
export interface NearbyEventDto extends EventSummaryDto {
  distance_km: number
}

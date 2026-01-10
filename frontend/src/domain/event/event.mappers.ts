import type { Event, EventLocation, EventOrganizer, EventAttendee } from './event.types'
import type { EventDto, EventLocationDto, EventOrganizerDto, EventAttendeeDto } from '@api/dto/events.dto'

/**
 * Map Event DTO to domain model
 */
export function mapEventFromDto(dto: EventDto): Event {
  return {
    id: dto.id,
    title: dto.title,
    description: dto.description,
    type: dto.type,
    startDate: new Date(dto.start_date),
    endDate: new Date(dto.end_date),
    location: mapEventLocationFromDto(dto.location),
    organizer: mapEventOrganizerFromDto(dto.organizer),
    imageUrl: dto.image_url ?? null,
    price: dto.price ?? null,
    maxAttendees: dto.max_attendees ?? null,
    currentAttendees: dto.current_attendees ?? 0,
    tags: dto.tags ?? [],
    status: dto.status,
    createdAt: new Date(dto.created_at),
    updatedAt: new Date(dto.updated_at),
  }
}

/**
 * Map Event location DTO to domain model
 */
export function mapEventLocationFromDto(dto: EventLocationDto): EventLocation {
  return {
    name: dto.name,
    address: dto.address,
    city: dto.city,
    region: dto.region,
    country: dto.country,
    latitude: dto.latitude ?? null,
    longitude: dto.longitude ?? null,
  }
}

/**
 * Map Event organizer DTO to domain model
 */
export function mapEventOrganizerFromDto(dto: EventOrganizerDto): EventOrganizer {
  return {
    id: dto.id,
    name: dto.name,
    email: dto.email,
    phone: dto.phone ?? null,
  }
}

/**
 * Map Event attendee DTO to domain model
 */
export function mapEventAttendeeFromDto(dto: EventAttendeeDto): EventAttendee {
  return {
    id: dto.id,
    eventId: dto.event_id,
    userId: dto.user_id,
    userName: dto.user_name,
    status: dto.status,
    registeredAt: new Date(dto.registered_at),
  }
}

/**
 * Map Event domain model to DTO for API requests
 */
export function mapEventToDto(event: Partial<Event>): Partial<EventDto> {
  return {
    title: event.title,
    description: event.description,
    type: event.type,
    start_date: event.startDate?.toISOString(),
    end_date: event.endDate?.toISOString(),
    location: event.location ? {
      name: event.location.name,
      address: event.location.address,
      city: event.location.city,
      region: event.location.region,
      country: event.location.country,
      latitude: event.location.latitude ?? undefined,
      longitude: event.location.longitude ?? undefined,
    } : undefined,
    price: event.price ?? undefined,
    max_attendees: event.maxAttendees ?? undefined,
    tags: event.tags,
  }
}


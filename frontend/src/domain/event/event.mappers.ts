import type { Event, EventLocation, EventOrganizer, EventAttendee } from './event.types'
import type { EventDto, EventDetailsDto, EventSummaryDto, EventAttendeeDto, CreateEventRequestDto } from '@api/dto/events.dto'

/**
 * Map Event summary DTO to domain model
 */
export function mapEventSummaryFromDto(dto: EventSummaryDto): Event {
  return {
    id: dto.id,
    title: dto.title,
    description: '',
    type: dto.type,
    startDate: new Date(dto.startDate),
    endDate: new Date(dto.endDate),
    location: {
      name: dto.locationName,
      address: '',
      city: dto.locationCity,
      region: dto.locationRegion ?? '',
      country: '',
      latitude: dto.latitude ?? null,
      longitude: dto.longitude ?? null,
    },
    organizer: {
      id: dto.organizerId ?? '',
      name: dto.organizerName ?? '',
      email: '',
      phone: null,
    },
    imageUrl: dto.imageUrl ?? null,
    price: dto.price ? Number(dto.price) : null,
    maxAttendees: dto.maxAttendees ?? null,
    currentAttendees: dto.currentAttendees ?? 0,
    tags: [],
    status: dto.status,
    createdAt: new Date(),
    updatedAt: new Date(),
  }
}

/**
 * Map Event details DTO to domain model
 */
export function mapEventFromDto(dto: EventDetailsDto): Event {
  return {
    id: dto.id,
    title: dto.title,
    description: dto.description,
    type: dto.type,
    startDate: new Date(dto.startDate),
    endDate: new Date(dto.endDate),
    location: {
      name: dto.locationName,
      address: dto.locationAddress ?? '',
      city: dto.locationCity,
      region: dto.locationRegion ?? '',
      country: '',
      latitude: dto.latitude ?? null,
      longitude: dto.longitude ?? null,
    },
    organizer: {
      id: dto.organizerId ?? '',
      name: dto.organizerName ?? '',
      email: dto.contactEmail ?? '',
      phone: dto.contactPhone ?? null,
    },
    imageUrl: dto.imageUrl ?? null,
    price: dto.price ? Number(dto.price) : null,
    maxAttendees: dto.maxAttendees ?? null,
    currentAttendees: dto.currentAttendees ?? 0,
    tags: [],
    status: dto.status,
    createdAt: new Date(dto.createdAt),
    updatedAt: new Date(dto.updatedAt),
  }
}

/**
 * Map Event attendee DTO to domain model
 */
export function mapEventAttendeeFromDto(dto: EventAttendeeDto): EventAttendee {
  return {
    id: dto.id,
    eventId: dto.eventId,
    userId: dto.userId,
    userName: dto.userName,
    status: dto.status.toLowerCase() as EventAttendee['status'],
    registeredAt: new Date(dto.registeredAt),
  }
}

/**
 * Map Event domain model to DTO for API requests
 */
export function mapEventToDto(event: Partial<Event>): Partial<CreateEventRequestDto> {
  return {
    title: event.title,
    description: event.description,
    type: event.type,
    startDate: event.startDate?.toISOString(),
    endDate: event.endDate?.toISOString(),
    locationName: event.location?.name,
    locationAddress: event.location?.address,
    locationCity: event.location?.city,
    locationRegion: event.location?.region,
    latitude: event.location?.latitude ?? undefined,
    longitude: event.location?.longitude ?? undefined,
    price: event.price ?? undefined,
    maxAttendees: event.maxAttendees ?? undefined,
    imageUrl: event.imageUrl ?? undefined,
  }
}

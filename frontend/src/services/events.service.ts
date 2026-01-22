import { eventsClient } from '@api/clients/events.client'
import { mapEventFromDto, mapEventSummaryFromDto, mapEventAttendeeFromDto, mapEventToDto } from '@domain/event/event.mappers'
import type { Event, EventFilter, EventAttendee } from '@domain/event/event.types'
import type { EventFilterParamsDto, CreateEventRequestDto } from '@api/dto/events.dto'
import type { PageMeta } from '@api/api.types'

export interface EventsResult {
  events: Event[]
  pagination: PageMeta
}

/**
 * Events service
 */
export const eventsService = {
  /**
   * Get paginated list of events
   */
  async getEvents(filter?: EventFilter, page = 0, pageSize = 20): Promise<EventsResult> {
    const params: EventFilterParamsDto = {
      page,
      size: pageSize,
      search: filter?.search,
      type: filter?.type,
      city: filter?.city,
      region: filter?.region,
      startDate: filter?.startDate?.toISOString(),
      endDate: filter?.endDate?.toISOString(),
      minPrice: filter?.minPrice,
      maxPrice: filter?.maxPrice,
      status: filter?.status,
      sortBy: filter?.sortBy as EventFilterParamsDto['sortBy'],
      sortDirection: filter?.sortOrder?.toUpperCase() as 'ASC' | 'DESC',
    }

    const response = await eventsClient.getEvents(params)

    // Backend returns PageResponse format with items/content
    const items = response.items ?? response.content ?? []

    return {
      events: items.map(mapEventSummaryFromDto),
      pagination: {
        pageNumber: response.pageNumber ?? page,
        pageSize: response.pageSize ?? pageSize,
        totalItems: response.totalItems ?? 0,
        totalPages: response.totalPages ?? 0,
        hasNext: response.hasNext ?? false,
        hasPrevious: response.hasPrevious ?? false,
      },
    }
  },

  /**
   * Get nearby events
   */
  async getNearbyEvents(latitude: number, longitude: number, radiusKm = 50, page = 0, pageSize = 20): Promise<EventsResult> {
    const response = await eventsClient.getNearbyEvents(latitude, longitude, radiusKm, page, pageSize)

    // Backend returns PageResponse format
    const items = response.items ?? response.content ?? []

    return {
      events: items.map(mapEventSummaryFromDto),
      pagination: {
        pageNumber: response.pageNumber ?? page,
        pageSize: response.pageSize ?? pageSize,
        totalItems: response.totalItems ?? 0,
        totalPages: response.totalPages ?? 0,
        hasNext: response.hasNext ?? false,
        hasPrevious: response.hasPrevious ?? false,
      },
    }
  },

  /**
   * Get a single event by ID
   */
  async getEvent(id: string): Promise<Event> {
    const response = await eventsClient.getEvent(id)
    return mapEventFromDto(response)
  },

  /**
   * Create a new event
   */
  async createEvent(event: Omit<Event, 'id' | 'currentAttendees' | 'status' | 'createdAt' | 'updatedAt'>): Promise<Event> {
    const dto = mapEventToDto(event) as CreateEventRequestDto
    const response = await eventsClient.createEvent(dto)
    return mapEventFromDto(response)
  },

  /**
   * Update an event
   */
  async updateEvent(id: string, event: Partial<Event>): Promise<Event> {
    const dto = mapEventToDto(event) as Partial<CreateEventRequestDto>
    const response = await eventsClient.updateEvent(id, dto)
    return mapEventFromDto(response)
  },

  /**
   * Delete an event
   */
  async deleteEvent(id: string): Promise<void> {
    await eventsClient.deleteEvent(id)
  },

  /**
   * Get event attendees
   */
  async getEventAttendees(eventId: string): Promise<EventAttendee[]> {
    const response = await eventsClient.getEventAttendees(eventId)
    return response.map(mapEventAttendeeFromDto)
  },

  /**
   * Register for an event
   */
  async registerForEvent(eventId: string): Promise<EventAttendee> {
    const response = await eventsClient.registerForEvent(eventId)
    return mapEventAttendeeFromDto(response)
  },

  /**
   * Cancel event registration
   */
  async cancelRegistration(eventId: string): Promise<void> {
    await eventsClient.cancelRegistration(eventId)
  },
}

export default eventsService

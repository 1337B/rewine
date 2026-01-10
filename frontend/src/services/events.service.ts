import { eventsClient } from '@api/clients/events.client'
import { mapEventFromDto, mapEventAttendeeFromDto, mapEventToDto } from '@domain/event/event.mappers'
import type { Event, EventFilter, EventAttendee } from '@domain/event/event.types'
import type { PaginationMeta } from '@api/api.types'
import type { EventFilterParamsDto, CreateEventRequestDto } from '@api/dto/events.dto'

export interface EventsResult {
  events: Event[]
  pagination: PaginationMeta
}

/**
 * Events service
 */
export const eventsService = {
  /**
   * Get paginated list of events
   */
  async getEvents(filter?: EventFilter, page = 1, pageSize = 20): Promise<EventsResult> {
    const params: EventFilterParamsDto = {
      page,
      page_size: pageSize,
      search: filter?.search,
      type: filter?.type,
      city: filter?.city,
      region: filter?.region,
      start_date: filter?.startDate?.toISOString(),
      end_date: filter?.endDate?.toISOString(),
      min_price: filter?.minPrice,
      max_price: filter?.maxPrice,
      status: filter?.status,
      sort_by: filter?.sortBy,
      sort_order: filter?.sortOrder,
    }

    const response = await eventsClient.getEvents(params)

    return {
      events: response.data.map(mapEventFromDto),
      pagination: response.pagination,
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
    const dto = mapEventToDto(event)
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
    const response = await eventsClient.registerForEvent({ event_id: eventId })
    return mapEventAttendeeFromDto(response)
  },

  /**
   * Cancel event registration
   */
  async cancelRegistration(eventId: string): Promise<void> {
    await eventsClient.cancelRegistration(eventId)
  },

  /**
   * Get nearby events
   */
  async getNearbyEvents(latitude: number, longitude: number, radiusKm = 50): Promise<Event[]> {
    const response = await eventsClient.getNearbyEvents(latitude, longitude, radiusKm)
    return response.map(mapEventFromDto)
  },
}

export default eventsService


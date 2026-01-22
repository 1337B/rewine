import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type {
  EventDto,
  EventDetailsDto,
  EventAttendeeDto,
  CreateEventRequestDto,
  EventFilterParamsDto,
  RegisterEventRequestDto,
  EventsPageResponseDto,
} from '@api/dto/events.dto'

/**
 * Events API client
 * Note: Backend returns data directly (no wrapper).
 */
export const eventsClient = {
  /**
   * Get paginated list of events
   */
  async getEvents(params?: EventFilterParamsDto): Promise<EventsPageResponseDto> {
    const response = await http.get<EventsPageResponseDto>(API_ENDPOINTS.EVENTS, {
      params,
    })
    return response.data
  },

  /**
   * Get nearby events
   */
  async getNearbyEvents(latitude: number, longitude: number, radiusKm?: number, page = 0, size = 20): Promise<EventsPageResponseDto> {
    const response = await http.get<EventsPageResponseDto>(
      `${API_ENDPOINTS.EVENTS}/nearby`,
      { params: { latitude, longitude, radiusKm, page, size } }
    )
    return response.data
  },

  /**
   * Get a single event by ID
   */
  async getEvent(id: string): Promise<EventDetailsDto> {
    const response = await http.get<EventDetailsDto>(`${API_ENDPOINTS.EVENTS}/${id}`)
    return response.data
  },

  /**
   * Create a new event (partner/admin only)
   */
  async createEvent(data: CreateEventRequestDto): Promise<EventDetailsDto> {
    const response = await http.post<EventDetailsDto>(API_ENDPOINTS.EVENTS, data)
    return response.data
  },

  /**
   * Update an event (partner/admin only)
   */
  async updateEvent(id: string, data: Partial<CreateEventRequestDto>): Promise<EventDetailsDto> {
    const response = await http.put<EventDetailsDto>(
      `${API_ENDPOINTS.EVENTS}/${id}`,
      data
    )
    return response.data
  },

  /**
   * Delete an event (admin only)
   */
  async deleteEvent(id: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.EVENTS}/${id}`)
  },

  /**
   * Get event attendees
   */
  async getEventAttendees(eventId: string): Promise<EventAttendeeDto[]> {
    const response = await http.get<EventAttendeeDto[]>(
      `${API_ENDPOINTS.EVENTS}/${eventId}/attendees`
    )
    return response.data
  },

  /**
   * Register for an event
   */
  async registerForEvent(eventId: string): Promise<EventAttendeeDto> {
    const response = await http.post<EventAttendeeDto>(
      `${API_ENDPOINTS.EVENTS}/${eventId}/register`
    )
    return response.data
  },

  /**
   * Cancel event registration
   */
  async cancelRegistration(eventId: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.EVENTS}/${eventId}/register`)
  },
}

export default eventsClient


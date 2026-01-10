import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type { ApiResponse, PaginatedResponse } from '@api/api.types'
import type {
  EventDto,
  EventAttendeeDto,
  CreateEventRequestDto,
  EventFilterParamsDto,
  RegisterEventRequestDto,
} from '@api/dto/events.dto'

/**
 * Events API client
 */
export const eventsClient = {
  /**
   * Get paginated list of events
   */
  async getEvents(params?: EventFilterParamsDto): Promise<PaginatedResponse<EventDto>> {
    const response = await http.get<PaginatedResponse<EventDto>>(API_ENDPOINTS.EVENTS, {
      params,
    })
    return response.data
  },

  /**
   * Get a single event by ID
   */
  async getEvent(id: string): Promise<EventDto> {
    const response = await http.get<ApiResponse<EventDto>>(`${API_ENDPOINTS.EVENTS}/${id}`)
    return response.data.data
  },

  /**
   * Create a new event (admin only)
   */
  async createEvent(data: CreateEventRequestDto): Promise<EventDto> {
    const response = await http.post<ApiResponse<EventDto>>(API_ENDPOINTS.EVENTS, data)
    return response.data.data
  },

  /**
   * Update an event (admin only)
   */
  async updateEvent(id: string, data: Partial<CreateEventRequestDto>): Promise<EventDto> {
    const response = await http.patch<ApiResponse<EventDto>>(
      `${API_ENDPOINTS.EVENTS}/${id}`,
      data
    )
    return response.data.data
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
    const response = await http.get<ApiResponse<EventAttendeeDto[]>>(
      `${API_ENDPOINTS.EVENTS}/${eventId}/attendees`
    )
    return response.data.data
  },

  /**
   * Register for an event
   */
  async registerForEvent(data: RegisterEventRequestDto): Promise<EventAttendeeDto> {
    const response = await http.post<ApiResponse<EventAttendeeDto>>(
      `${API_ENDPOINTS.EVENTS}/${data.event_id}/register`
    )
    return response.data.data
  },

  /**
   * Cancel event registration
   */
  async cancelRegistration(eventId: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.EVENTS}/${eventId}/register`)
  },

  /**
   * Get nearby events
   */
  async getNearbyEvents(latitude: number, longitude: number, radiusKm?: number): Promise<EventDto[]> {
    const response = await http.get<ApiResponse<EventDto[]>>(
      `${API_ENDPOINTS.EVENTS}/nearby`,
      { params: { latitude, longitude, radius: radiusKm } }
    )
    return response.data.data
  },
}

export default eventsClient


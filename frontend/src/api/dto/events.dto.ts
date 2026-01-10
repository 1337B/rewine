import type { EventType, EventStatus } from '@domain/event/event.types'

export interface EventDto {
  id: string
  title: string
  description: string
  type: EventType
  start_date: string
  end_date: string
  location: EventLocationDto
  organizer: EventOrganizerDto
  image_url?: string
  price?: number
  max_attendees?: number
  current_attendees?: number
  tags?: string[]
  status: EventStatus
  created_at: string
  updated_at: string
}

export interface EventLocationDto {
  name: string
  address: string
  city: string
  region: string
  country: string
  latitude?: number
  longitude?: number
}

export interface EventOrganizerDto {
  id: string
  name: string
  email: string
  phone?: string
}

export interface EventAttendeeDto {
  id: string
  event_id: string
  user_id: string
  user_name: string
  status: 'registered' | 'confirmed' | 'cancelled'
  registered_at: string
}

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
}

export interface EventFilterParamsDto {
  search?: string
  type?: string | string[]
  city?: string
  region?: string
  start_date?: string
  end_date?: string
  min_price?: number
  max_price?: number
  status?: EventStatus
  sort_by?: string
  sort_order?: 'asc' | 'desc'
  page?: number
  page_size?: number
}

export interface RegisterEventRequestDto {
  event_id: string
}


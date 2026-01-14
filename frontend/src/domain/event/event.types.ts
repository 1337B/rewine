/**
 * Event domain types
 */

export interface Event {
  id: string
  title: string
  description: string
  type: EventType
  startDate: Date
  endDate: Date
  location: EventLocation
  organizer: EventOrganizer
  imageUrl: string | null
  price: number | null
  maxAttendees: number | null
  currentAttendees: number
  tags: string[]
  status: EventStatus
  createdAt: Date
  updatedAt: Date
}

export type EventType = 'tasting' | 'festival' | 'tour' | 'class' | 'dinner' | 'other'

export type EventStatus = 'draft' | 'published' | 'cancelled' | 'completed'

export interface EventLocation {
  name: string
  address: string
  city: string
  region: string
  country: string
  latitude: number | null
  longitude: number | null
}

export interface EventOrganizer {
  id: string
  name: string
  email: string
  phone: string | null
}

export interface EventFilter {
  search?: string
  type?: EventType | EventType[]
  city?: string
  region?: string
  startDate?: Date
  endDate?: Date
  minPrice?: number
  maxPrice?: number
  status?: EventStatus
  sortBy?: 'date' | 'price' | 'popularity'
  sortOrder?: 'asc' | 'desc'
}

export interface EventAttendee {
  id: string
  eventId: string
  userId: string
  userName: string
  status: 'registered' | 'confirmed' | 'cancelled' | 'attended'
  registeredAt: Date
}


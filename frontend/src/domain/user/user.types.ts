/**
 * User domain types
 */

export interface User {
  id: string
  email: string
  name: string
  avatar: string | null
  roles: UserRole[]
  preferences: UserPreferences
  isVerified: boolean
  createdAt: Date
  updatedAt: Date
}

export type UserRole = 'user' | 'admin' | 'winery' | 'sommelier'

export interface UserPreferences {
  favoriteWineTypes: string[]
  favoriteRegions: string[]
  priceRange: PriceRange | null
  notifications: NotificationPreferences
  locale: string
  theme: 'light' | 'dark' | 'system'
}

export interface PriceRange {
  min: number
  max: number
}

export interface NotificationPreferences {
  email: boolean
  push: boolean
  events: boolean
  recommendations: boolean
  newsletter: boolean
}

export interface UserProfile {
  id: string
  name: string
  avatar: string | null
  bio: string | null
  location: string | null
  favoriteWines: string[]
  reviewCount: number
  joinedAt: Date
}

export interface UserStats {
  totalReviews: number
  totalWinesTasted: number
  totalEventsAttended: number
  totalRoutesCompleted: number
  cellarSize: number
}


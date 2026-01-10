import type { UserRole } from '@domain/user/user.types'

export interface UserDto {
  id: string
  email: string
  name: string
  avatar?: string
  roles?: UserRole[]
  preferences?: UserPreferencesDto
  is_verified?: boolean
  created_at: string
  updated_at: string
}

export interface UserPreferencesDto {
  favorite_wine_types?: string[]
  favorite_regions?: string[]
  price_range?: { min: number; max: number }
  notifications?: {
    email?: boolean
    push?: boolean
    events?: boolean
    recommendations?: boolean
    newsletter?: boolean
  }
  locale?: string
  theme?: 'light' | 'dark' | 'system'
}

export interface UserProfileDto {
  id: string
  name: string
  avatar?: string
  bio?: string
  location?: string
  favorite_wines?: string[]
  review_count?: number
  joined_at: string
}

export interface UserStatsDto {
  total_reviews?: number
  total_wines_tasted?: number
  total_events_attended?: number
  total_routes_completed?: number
  cellar_size?: number
}

export interface UpdateUserRequestDto {
  name?: string
  avatar?: string
}

export interface UpdateUserPreferencesRequestDto {
  favorite_wine_types?: string[]
  favorite_regions?: string[]
  price_range?: { min: number; max: number }
  notifications?: {
    email?: boolean
    push?: boolean
    events?: boolean
    recommendations?: boolean
    newsletter?: boolean
  }
  locale?: string
  theme?: 'light' | 'dark' | 'system'
}

export interface ChangePasswordRequestDto {
  current_password: string
  new_password: string
}

export interface UserFilterParamsDto {
  search?: string
  role?: UserRole
  is_verified?: boolean
  sort_by?: string
  sort_order?: 'asc' | 'desc'
  page?: number
  page_size?: number
}


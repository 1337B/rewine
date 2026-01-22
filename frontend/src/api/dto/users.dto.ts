/**
 * User DTOs
 *
 * Data Transfer Objects for user API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 * Note: Backend uses camelCase for all JSON fields.
 */

import type { UserRole } from '@domain/user/user.types'

// ============================================================================
// User DTOs
// ============================================================================

/**
 * Full user data (admin/self view)
 * Supports both camelCase (backend) and snake_case (legacy) fields
 */
export interface UserDto {
  id: string
  username?: string
  email: string
  name: string
  avatar?: string | null
  avatarUrl?: string | null
  roles?: string[]
  preferences?: UserPreferencesDto | null
  // camelCase (from backend)
  emailVerified?: boolean
  isActive?: boolean
  lastLoginAt?: string | null
  createdAt?: string
  updatedAt?: string
  // snake_case (legacy support)
  is_verified?: boolean
  is_active?: boolean
  last_login_at?: string | null
  created_at?: string
  updated_at?: string
}

/**
 * User preferences structure
 */
export interface UserPreferencesDto {
  favorite_wine_types?: string[]
  favorite_regions?: string[]
  price_range?: PriceRangeDto | null
  notifications?: NotificationPreferencesDto | null
  locale?: string
  theme?: 'light' | 'dark' | 'system'
}

/**
 * Price range structure
 */
export interface PriceRangeDto {
  min: number
  max: number
}

/**
 * Notification preferences structure
 */
export interface NotificationPreferencesDto {
  email?: boolean
  push?: boolean
  events?: boolean
  recommendations?: boolean
  newsletter?: boolean
}

// ============================================================================
// Profile DTOs
// ============================================================================

/**
 * Public user profile
 */
export interface UserProfileDto {
  id: string
  name: string
  avatar?: string | null
  bio?: string | null
  location?: string | null
  favorite_wines?: string[]
  review_count?: number
  follower_count?: number
  following_count?: number
  is_following?: boolean
  joined_at: string
}

/**
 * User statistics
 */
export interface UserStatsDto {
  total_reviews?: number
  total_wines_tasted?: number
  total_events_attended?: number
  total_routes_completed?: number
  cellar_size?: number
  badges?: UserBadgeDto[]
}

/**
 * User badge/achievement
 */
export interface UserBadgeDto {
  id: string
  name: string
  description: string
  icon_url: string
  earned_at: string
}

// ============================================================================
// Request DTOs
// ============================================================================

/**
 * Update user request
 */
export interface UpdateUserRequestDto {
  name?: string
  avatar?: string
  bio?: string
  location?: string
}

/**
 * Update user preferences request
 */
export interface UpdateUserPreferencesRequestDto {
  favorite_wine_types?: string[]
  favorite_regions?: string[]
  price_range?: PriceRangeDto | null
  notifications?: NotificationPreferencesDto
  locale?: string
  theme?: 'light' | 'dark' | 'system'
}

/**
 * Change password request
 */
export interface ChangePasswordRequestDto {
  current_password: string
  new_password: string
}

/**
 * User filter/search parameters (admin)
 */
export interface UserFilterParamsDto {
  search?: string
  role?: UserRole | UserRole[]
  is_verified?: boolean
  is_active?: boolean
  created_after?: string
  created_before?: string
  sort_by?: 'name' | 'email' | 'created_at' | 'last_login_at'
  sort_order?: 'asc' | 'desc'
  page?: number
  page_size?: number
}

// ============================================================================
// Response DTOs
// ============================================================================

/**
 * Paginated users response
 */
export interface UsersPageResponseDto {
  data: UserDto[]
  pagination: {
    page: number
    page_size: number
    total_items: number
    total_pages: number
    has_next: boolean
    has_previous: boolean
  }
}

/**
 * User activity item
 */
export interface UserActivityDto {
  id: string
  type: 'review' | 'event_registration' | 'route_completed' | 'wine_added' | 'follow'
  description: string
  target_id?: string
  target_name?: string
  created_at: string
}

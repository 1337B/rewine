import type { User, UserPreferences, UserProfile, UserStats, NotificationPreferences, PriceRange, UserRole } from './user.types'
import type { UserDto, UserPreferencesDto, UserProfileDto, UserStatsDto, NotificationPreferencesDto } from '@api/dto/users.dto'

/**
 * Map role string to UserRole
 */
function mapRole(role: string): UserRole {
  // Handle both formats: 'user' and 'ROLE_USER'
  const roleMap: Record<string, UserRole> = {
    'user': 'ROLE_USER',
    'admin': 'ROLE_ADMIN',
    'moderator': 'ROLE_MODERATOR',
    'partner': 'ROLE_PARTNER',
    'ROLE_USER': 'ROLE_USER',
    'ROLE_ADMIN': 'ROLE_ADMIN',
    'ROLE_MODERATOR': 'ROLE_MODERATOR',
    'ROLE_PARTNER': 'ROLE_PARTNER',
  }
  return roleMap[role] ?? 'ROLE_USER'
}

/**
 * Map User DTO to domain model
 */
export function mapUserFromDto(dto: UserDto): User {
  return {
    id: dto.id,
    email: dto.email,
    name: dto.name,
    avatar: dto.avatar ?? null,
    roles: (dto.roles ?? ['user']).map(mapRole),
    preferences: dto.preferences ? mapUserPreferencesFromDto(dto.preferences) : getDefaultPreferences(),
    isVerified: dto.is_verified ?? false,
    createdAt: new Date(dto.created_at),
    updatedAt: new Date(dto.updated_at),
  }
}

/**
 * Map User preferences DTO to domain model
 */
export function mapUserPreferencesFromDto(dto: UserPreferencesDto): UserPreferences {
  return {
    favoriteWineTypes: dto.favorite_wine_types ?? [],
    favoriteRegions: dto.favorite_regions ?? [],
    priceRange: dto.price_range ? mapPriceRangeFromDto(dto.price_range) : null,
    notifications: mapNotificationPreferencesFromDto(dto.notifications),
    locale: dto.locale ?? 'es-AR',
    theme: dto.theme ?? 'system',
  }
}

/**
 * Map price range from DTO
 */
function mapPriceRangeFromDto(dto: { min: number; max: number }): PriceRange {
  return {
    min: dto.min,
    max: dto.max,
  }
}

/**
 * Map notification preferences from DTO
 */
function mapNotificationPreferencesFromDto(dto?: NotificationPreferencesDto | null): NotificationPreferences {
  return {
    email: dto?.email ?? true,
    push: dto?.push ?? false,
    events: dto?.events ?? true,
    recommendations: dto?.recommendations ?? true,
    newsletter: dto?.newsletter ?? false,
  }
}

/**
 * Get default user preferences
 */
function getDefaultPreferences(): UserPreferences {
  return {
    favoriteWineTypes: [],
    favoriteRegions: [],
    priceRange: null,
    notifications: {
      email: true,
      push: false,
      events: true,
      recommendations: true,
      newsletter: false,
    },
    locale: 'es-AR',
    theme: 'system',
  }
}

/**
 * Map User profile DTO to domain model
 */
export function mapUserProfileFromDto(dto: UserProfileDto): UserProfile {
  return {
    id: dto.id,
    name: dto.name,
    avatar: dto.avatar ?? null,
    bio: dto.bio ?? null,
    location: dto.location ?? null,
    favoriteWines: dto.favorite_wines ?? [],
    reviewCount: dto.review_count ?? 0,
    joinedAt: new Date(dto.joined_at),
  }
}

/**
 * Map User stats DTO to domain model
 */
export function mapUserStatsFromDto(dto: UserStatsDto): UserStats {
  return {
    totalReviews: dto.total_reviews ?? 0,
    totalWinesTasted: dto.total_wines_tasted ?? 0,
    totalEventsAttended: dto.total_events_attended ?? 0,
    totalRoutesCompleted: dto.total_routes_completed ?? 0,
    cellarSize: dto.cellar_size ?? 0,
  }
}

/**
 * Map User domain model to DTO for API requests
 */
export function mapUserToDto(user: Partial<User>): Partial<UserDto> {
  return {
    name: user.name,
    avatar: user.avatar ?? undefined,
  }
}

/**
 * Map User preferences to DTO for API requests
 */
export function mapUserPreferencesToDto(preferences: Partial<UserPreferences>): Partial<UserPreferencesDto> {
  return {
    favorite_wine_types: preferences.favoriteWineTypes,
    favorite_regions: preferences.favoriteRegions,
    price_range: preferences.priceRange ?? undefined,
    notifications: preferences.notifications,
    locale: preferences.locale,
    theme: preferences.theme,
  }
}


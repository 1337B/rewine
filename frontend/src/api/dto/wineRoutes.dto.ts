/**
 * Wine Route DTOs
 *
 * Data Transfer Objects for wine route API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 */

import type { RouteDifficulty, StopType } from '@domain/route/route.types'

// ============================================================================
// Route DTOs
// ============================================================================

/**
 * Wine route summary for list views
 */
export interface WineRouteSummaryDto {
  id: string
  name: string
  region: string
  country: string
  difficulty: RouteDifficulty
  duration: number
  distance: number
  stop_count?: number
  image_url?: string | null
  rating?: number | null
  review_count?: number
}

/**
 * Full wine route details
 */
export interface WineRouteDetailsDto extends WineRouteSummaryDto {
  description: string
  stops?: RouteStopDto[]
  tags?: string[]
  is_published?: boolean
  is_favorite?: boolean
  created_by: string
  created_at: string
  updated_at: string
}

/**
 * Wine Route DTO (legacy alias)
 * @deprecated Use WineRouteDetailsDto for full details, WineRouteSummaryDto for lists
 */
export interface WineRouteDto extends WineRouteDetailsDto {}

/**
 * Route stop structure
 */
export interface RouteStopDto {
  id: string
  order: number
  name: string
  type: StopType
  description: string
  location: RouteLocationDto
  duration: number
  winery?: RouteWineryDto | null
  photos?: string[]
}

/**
 * Route location structure
 */
export interface RouteLocationDto {
  address: string
  city: string
  latitude: number
  longitude: number
}

/**
 * Route winery info
 */
export interface RouteWineryDto {
  id: string
  name: string
  wines?: string[]
  rating?: number | null
}

// ============================================================================
// Review DTOs
// ============================================================================

/**
 * Wine route review
 */
export interface WineRouteReviewDto {
  id: string
  route_id: string
  user_id: string
  user_name: string
  user_avatar?: string | null
  rating: number
  comment: string
  visited_at: string
  photos?: string[]
  created_at: string
}

/**
 * Create wine route review request
 */
export interface CreateWineRouteReviewRequestDto {
  rating: number
  comment?: string
  visited_at: string
  photos?: string[]
}

// ============================================================================
// Request DTOs
// ============================================================================

/**
 * Create wine route request
 */
export interface CreateWineRouteRequestDto {
  name: string
  description: string
  region: string
  country: string
  difficulty: RouteDifficulty
  duration: number
  distance: number
  image_url?: string
  tags?: string[]
}

/**
 * Update wine route request
 */
export interface UpdateWineRouteRequestDto extends Partial<CreateWineRouteRequestDto> {
  is_published?: boolean
}

/**
 * Add route stop request
 */
export interface AddRouteStopRequestDto {
  name: string
  type: StopType
  description: string
  location: RouteLocationDto
  duration: number
  winery_id?: string
  order?: number
}

/**
 * Update route stop request
 */
export interface UpdateRouteStopRequestDto extends Partial<AddRouteStopRequestDto> {}

/**
 * Wine route filter/search parameters
 */
export interface WineRouteFilterParamsDto {
  search?: string
  region?: string | string[]
  country?: string | string[]
  difficulty?: RouteDifficulty | RouteDifficulty[]
  min_duration?: number
  max_duration?: number
  min_distance?: number
  max_distance?: number
  min_rating?: number
  has_winery?: boolean
  stop_type?: StopType | StopType[]
  sort_by?: 'name' | 'rating' | 'duration' | 'distance' | 'created_at'
  sort_order?: 'asc' | 'desc'
  page?: number
  page_size?: number
}

// ============================================================================
// Response DTOs
// ============================================================================

/**
 * Paginated wine routes response
 */
export interface WineRoutesPageResponseDto {
  data: WineRouteSummaryDto[]
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
 * Route directions/navigation response
 */
export interface RouteDirectionsDto {
  route_id: string
  total_distance: number
  total_duration: number
  segments: RouteSegmentDto[]
}

/**
 * Route segment between stops
 */
export interface RouteSegmentDto {
  from_stop_id: string
  to_stop_id: string
  distance: number
  duration: number
  polyline?: string
}

/**
 * Wine Route DTOs
 *
 * Data Transfer Objects for wine route API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 * Note: Backend uses camelCase for all JSON fields.
 */

import type { RouteDifficulty, StopType } from '@domain/route/route.types'

// ============================================================================
// Route DTOs
// ============================================================================

/**
 * Wine route summary for list views (matches backend WineRouteSummaryResponse)
 */
export interface WineRouteSummaryDto {
  id: string
  name: string
  description?: string | null
  country?: string | null
  region?: string | null
  subregion?: string | null
  estimatedDuration?: number | null
  estimatedDays?: number | null
  totalDistance?: number | null
  difficulty?: string | null
  imageUrl?: string | null
  status?: string | null
  wineryCount?: number
  stopCount?: number
  createdAt?: string | null
}

/**
 * Full wine route details (matches backend WineRouteDetailsResponse)
 */
export interface WineRouteDetailsDto extends WineRouteSummaryDto {
  recommendedWineTypes?: string[] | null
  stops?: RouteStopDto[]
  wineries?: RouteWineryDto[]
  createdById?: string | null
  updatedAt?: string | null
}

/**
 * Wine Route DTO (legacy alias)
 * @deprecated Use WineRouteDetailsDto for full details, WineRouteSummaryDto for lists
 */
export interface WineRouteDto extends WineRouteDetailsDto {}

/**
 * Route stop structure (matches backend WineRouteStopResponse)
 */
export interface RouteStopDto {
  id: string
  name: string
  type?: string | null
  description?: string | null
  address?: string | null
  latitude?: number | null
  longitude?: number | null
  estimatedDuration?: number | null
  stopOrder?: number
}

/**
 * Route winery info (matches backend WineryInfoResponse)
 */
export interface RouteWineryDto {
  id: string
  name: string
  region?: string | null
  country?: string | null
  description?: string | null
  logoUrl?: string | null
  websiteUrl?: string | null
}

/**
 * Hierarchy response (matches backend WineRouteHierarchyResponse)
 */
export interface WineRouteHierarchyDto {
  countries: WineRouteHierarchyCountryDto[]
}

export interface WineRouteHierarchyCountryDto {
  name: string
  regions: WineRouteHierarchyRegionDto[]
}

export interface WineRouteHierarchyRegionDto {
  name: string
  subregions: string[]
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
  estimatedDuration: number
  totalDistance: number
  imageUrl?: string
}

/**
 * Update wine route request
 */
export interface UpdateWineRouteRequestDto extends Partial<CreateWineRouteRequestDto> {
  status?: string
}

/**
 * Wine route filter/search parameters
 */
export interface WineRouteFilterParamsDto {
  country?: string
  region?: string
  subregion?: string
  search?: string
  page?: number
  size?: number
}

// ============================================================================
// Response DTOs
// ============================================================================

/**
 * Paginated wine routes response (matches backend PageResponse format)
 */
export interface WineRoutesPageResponseDto {
  items: WineRouteSummaryDto[]
  content: WineRouteSummaryDto[]  // Legacy alias
  pageNumber: number
  pageSize: number
  totalItems: number
  totalPages: number
  first: boolean
  last: boolean
  hasNext: boolean
  hasPrevious: boolean
  empty: boolean
}

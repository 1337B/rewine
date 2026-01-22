/**
 * Wine DTOs
 *
 * Data Transfer Objects for wine API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 * Note: Backend uses camelCase for all JSON fields.
 */

import type { WineType } from '@domain/wine/wine.types'

// ============================================================================
// Wine DTOs
// ============================================================================

/**
 * Wine summary for list views (matches backend WineSummaryResponse)
 */
export interface WineSummaryDto {
  id: string
  name: string
  vintage?: number | null
  wineType: WineType
  style?: string | null
  wineryName?: string | null
  region?: string | null
  country?: string | null
  priceMin?: number | null
  priceMax?: number | null
  ratingAverage?: number | null
  ratingCount?: number
  imageUrl?: string | null
  isFeatured?: boolean
}

/**
 * Full wine details (matches backend WineDetailsResponse)
 */
export interface WineDetailsDto {
  id: string
  name: string
  vintage?: number | null
  wineType: WineType
  style?: string | null
  grapes?: string[]
  allergens?: string[]
  descriptionEs?: string | null
  descriptionEn?: string | null
  alcoholContent?: number | null
  servingTempMin?: number | null
  servingTempMax?: number | null
  priceMin?: number | null
  priceMax?: number | null
  imageUrl?: string | null
  ratingAverage?: number | null
  ratingCount?: number
  isFeatured?: boolean
  createdAt?: string

  // Winery information
  winery?: WineryInfoDto | null

  // Rating distribution
  ratingDistribution?: RatingDistributionDto | null

  // Featured reviews preview
  featuredReviews?: WineReviewDto[]

  // User-specific data
  userWineData?: UserWineDataDto | null

  // AI profile status
  aiProfileStatus?: 'NOT_REQUESTED' | 'GENERATED' | null
  aiProfileGeneratedAt?: string | null
}

/**
 * Winery info embedded in wine details
 */
export interface WineryInfoDto {
  id: string
  name: string
  region?: string | null
  country?: string | null
  logoUrl?: string | null
  websiteUrl?: string | null
}

/**
 * Rating distribution for star ratings
 */
export interface RatingDistributionDto {
  oneStar: number
  twoStar: number
  threeStar: number
  fourStar: number
  fiveStar: number
}

/**
 * User-specific wine data
 */
export interface UserWineDataDto {
  hasReviewed?: boolean
  userRating?: number | null
  inCellar?: boolean
  cellarQuantity?: number | null
}

/**
 * Wine DTO (legacy alias)
 * @deprecated Use WineDetailsDto for full details, WineSummaryDto for lists
 */
export interface WineDto extends WineDetailsDto {}

/**
 * Tasting notes structure
 */
export interface TastingNotesDto {
  appearance: string
  aroma: string[]
  palate: string[]
  finish: string
  body: 'light' | 'medium' | 'full'
  sweetness: 'dry' | 'off-dry' | 'medium-sweet' | 'sweet'
  tannins: 'low' | 'medium' | 'high'
  acidity: 'low' | 'medium' | 'high'
}

/**
 * Wine award/recognition
 */
export interface WineAwardDto {
  name: string
  year: number
  medal?: 'gold' | 'silver' | 'bronze'
}

// ============================================================================
// Review DTOs
// ============================================================================

/**
 * Wine review (matches backend ReviewResponse)
 */
export interface WineReviewDto {
  id: string
  wineId: string
  userId: string
  username: string
  userAvatarUrl?: string | null
  rating: number
  title?: string | null
  comment?: string | null
  helpfulCount?: number
  isVerified?: boolean
  createdAt: string
  updatedAt?: string
}

/**
 * Create wine review request
 */
export interface CreateWineReviewRequestDto {
  rating: number
  title?: string
  comment?: string
}

// ============================================================================
// Comparison DTOs
// ============================================================================

/**
 * Wine comparison result (matches backend WineComparisonResponse)
 */
export interface CompareResultDto {
  wineAId: string
  wineBId: string
  language: string
  comparisonContent: ComparisonContentDto
  cached: boolean
  generatedAt: string
}

/**
 * Comparison content from AI
 */
export interface ComparisonContentDto {
  similarities?: string[]
  differences?: string[]
  recommendation?: string
  summary?: string
}

/**
 * Comparison attribute structure
 */
export interface ComparisonAttributeDto {
  attribute: string
  values: string[]
  winner_index?: number | null
}

// ============================================================================
// AI Profile DTOs
// ============================================================================

/**
 * AI-generated wine profile (matches backend WineAiProfileResponse)
 */
export interface AiProfileDto {
  wineId: string
  language: string
  profileContent: AiProfileContentDto
  cached: boolean
  generatedAt: string
}

/**
 * AI profile content
 */
export interface AiProfileContentDto {
  summary?: string
  tastingNotes?: string
  foodPairings?: string[]
  idealOccasions?: string[]
  similarWines?: string[]
}

// ============================================================================
// Request DTOs
// ============================================================================

/**
 * Create wine request (matches backend format)
 */
export interface CreateWineRequestDto {
  name: string
  wineryId?: string
  wineType: WineType
  vintage?: number
  style?: string
  grapes?: string[]
  allergens?: string[]
  descriptionEs?: string
  descriptionEn?: string
  alcoholContent?: number
  servingTempMin?: number
  servingTempMax?: number
  priceMin?: number
  priceMax?: number
  imageUrl?: string
}

/**
 * Update wine request
 */
export interface UpdateWineRequestDto extends Partial<CreateWineRequestDto> {}

/**
 * Wine filter/search parameters (matches backend WineSearchRequest)
 */
export interface WineFilterParamsDto {
  search?: string
  wineType?: WineType
  country?: string
  region?: string
  vintage?: number
  minPrice?: number
  maxPrice?: number
  minRating?: number
  featured?: boolean
  sortBy?: 'name' | 'vintage' | 'priceMin' | 'priceMax' | 'ratingAverage'
  sortDirection?: 'ASC' | 'DESC'
  page?: number
  size?: number
}

// ============================================================================
// Response DTOs
// ============================================================================

/**
 * Paginated wines response (matches backend PageResponse)
 */
export interface WinesPageResponseDto {
  items: WineSummaryDto[]
  content: WineSummaryDto[]  // Legacy alias
  pageNumber: number
  pageSize: number
  totalItems: number
  totalPages: number
  first: boolean
  last: boolean
  hasNext: boolean
  hasPrevious: boolean
}

/**
 * Paginated reviews response (matches backend PageResponse)
 */
export interface ReviewsPageResponseDto {
  items: WineReviewDto[]
  content: WineReviewDto[]  // Legacy alias
  pageNumber: number
  pageSize: number
  totalItems: number
  totalPages: number
  first: boolean
  last: boolean
  hasNext: boolean
  hasPrevious: boolean
}

/**
 * Wine scan/recognition result
 */
export interface WineScanResultDto {
  confidence: number
  wine?: WineSummaryDto
  suggestions?: WineSummaryDto[]
}

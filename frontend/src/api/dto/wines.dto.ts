/**
 * Wine DTOs
 *
 * Data Transfer Objects for wine API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 */

import type { WineType } from '@domain/wine/wine.types'

// ============================================================================
// Wine DTOs
// ============================================================================

/**
 * Wine summary for list views
 */
export interface WineSummaryDto {
  id: string
  name: string
  winery: string
  type: WineType
  region: string
  country: string
  vintage?: number | null
  price?: number | null
  rating?: number | null
  review_count?: number
  image_url?: string | null
}

/**
 * Full wine details
 */
export interface WineDetailsDto extends WineSummaryDto {
  grape_varieties?: string[]
  alcohol_content?: number | null
  description?: string | null
  tasting_notes?: TastingNotesDto | null
  food_pairings?: string[]
  awards?: WineAwardDto[]
  created_at: string
  updated_at: string
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
 * Wine review
 */
export interface WineReviewDto {
  id: string
  wine_id: string
  user_id: string
  user_name: string
  user_avatar?: string | null
  rating: number
  comment: string
  helpful_count?: number
  created_at: string
  updated_at?: string
}

/**
 * Create wine review request
 */
export interface CreateWineReviewRequestDto {
  rating: number
  comment?: string
}

// ============================================================================
// Comparison DTOs
// ============================================================================

/**
 * Wine comparison result
 */
export interface CompareResultDto {
  wines: WineDetailsDto[]
  comparison: ComparisonAttributeDto[]
  ai_summary?: string
}

/**
 * Comparison attribute
 */
export interface ComparisonAttributeDto {
  attribute: string
  values: (string | number | null)[]
  winner_index?: number | null
}

// ============================================================================
// AI Profile DTOs
// ============================================================================

/**
 * AI-generated wine profile
 */
export interface AiProfileDto {
  wine_id: string
  summary: string
  ideal_occasions: string[]
  food_pairings_detailed: AiFoodPairingDto[]
  similar_wines: string[]
  personality_traits: string[]
  generated_at: string
}

/**
 * AI food pairing suggestion
 */
export interface AiFoodPairingDto {
  dish: string
  category: string
  match_score: number
  reason: string
}

// ============================================================================
// Request DTOs
// ============================================================================

/**
 * Create wine request
 */
export interface CreateWineRequestDto {
  name: string
  winery: string
  type: WineType
  region: string
  country: string
  grape_varieties?: string[]
  vintage?: number
  alcohol_content?: number
  price?: number
  description?: string
  image_url?: string
  food_pairings?: string[]
}

/**
 * Update wine request
 */
export interface UpdateWineRequestDto extends Partial<CreateWineRequestDto> {}

/**
 * Wine filter/search parameters
 */
export interface WineFilterParamsDto {
  search?: string
  type?: string | string[]
  region?: string | string[]
  country?: string | string[]
  grape_variety?: string | string[]
  winery?: string | string[]
  min_price?: number
  max_price?: number
  min_rating?: number
  max_rating?: number
  vintage?: number | number[]
  min_vintage?: number
  max_vintage?: number
  sort_by?: 'name' | 'price' | 'rating' | 'vintage' | 'created_at'
  sort_order?: 'asc' | 'desc'
  page?: number
  page_size?: number
}

// ============================================================================
// Response DTOs
// ============================================================================

/**
 * Paginated wines response
 */
export interface WinesPageResponseDto {
  data: WineSummaryDto[]
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
 * Wine scan/recognition result
 */
export interface WineScanResultDto {
  confidence: number
  wine?: WineSummaryDto
  suggestions?: WineSummaryDto[]
}

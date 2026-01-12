/**
 * Wine domain mappers
 *
 * Functions to map between DTOs (API layer) and domain models.
 * All mappers include null checks and default values for safety.
 */

import type {
  Wine,
  WineSummary,
  WineReview,
  TastingNotes,
  WineAward,
  WineComparison,
  ComparisonAttribute,
  AiWineProfile,
  AiFoodPairing,
  WineScanResult,
} from './wine.types'
import type {
  WineDto,
  WineSummaryDto,
  WineDetailsDto,
  WineReviewDto,
  TastingNotesDto,
  WineAwardDto,
  CompareResultDto,
  ComparisonAttributeDto,
  AiProfileDto,
  AiFoodPairingDto,
  WineScanResultDto,
} from '@api/dto/wines.dto'

// ============================================================================
// Wine Mappers
// ============================================================================

/**
 * Map Wine DTO to full domain model
 */
export function mapWineFromDto(dto: WineDto | WineDetailsDto): Wine {
  return {
    id: dto.id,
    name: dto.name,
    winery: dto.winery,
    type: dto.type,
    region: dto.region,
    country: dto.country,
    grapeVarieties: dto.grape_varieties ?? [],
    vintage: dto.vintage ?? null,
    alcoholContent: dto.alcohol_content ?? null,
    price: dto.price ?? null,
    rating: dto.rating ?? null,
    reviewCount: dto.review_count ?? 0,
    description: dto.description ?? '',
    imageUrl: dto.image_url ?? null,
    tastingNotes: dto.tasting_notes ? mapTastingNotesFromDto(dto.tasting_notes) : null,
    foodPairings: dto.food_pairings ?? [],
    awards: (dto as WineDetailsDto).awards?.map(mapWineAwardFromDto) ?? [],
    createdAt: new Date(dto.created_at),
    updatedAt: new Date(dto.updated_at),
  }
}

/**
 * Map Wine summary DTO to domain summary
 */
export function mapWineSummaryFromDto(dto: WineSummaryDto): WineSummary {
  return {
    id: dto.id,
    name: dto.name,
    winery: dto.winery,
    type: dto.type,
    region: dto.region,
    country: dto.country,
    vintage: dto.vintage ?? null,
    price: dto.price ?? null,
    rating: dto.rating ?? null,
    reviewCount: dto.review_count ?? 0,
    imageUrl: dto.image_url ?? null,
  }
}

/**
 * Map tasting notes DTO to domain model
 */
export function mapTastingNotesFromDto(dto: TastingNotesDto): TastingNotes {
  return {
    appearance: dto.appearance,
    aroma: dto.aroma ?? [],
    palate: dto.palate ?? [],
    finish: dto.finish,
    body: dto.body,
    sweetness: dto.sweetness,
    tannins: dto.tannins,
    acidity: dto.acidity,
  }
}

/**
 * Map wine award DTO to domain model
 */
export function mapWineAwardFromDto(dto: WineAwardDto): WineAward {
  return {
    name: dto.name,
    year: dto.year,
    medal: dto.medal ?? null,
  }
}

// ============================================================================
// Review Mappers
// ============================================================================

/**
 * Map Wine review DTO to domain model
 */
export function mapWineReviewFromDto(dto: WineReviewDto): WineReview {
  return {
    id: dto.id,
    wineId: dto.wine_id,
    userId: dto.user_id,
    userName: dto.user_name,
    userAvatar: dto.user_avatar ?? null,
    rating: dto.rating,
    comment: dto.comment,
    helpfulCount: dto.helpful_count ?? 0,
    createdAt: new Date(dto.created_at),
  }
}

// ============================================================================
// Comparison Mappers
// ============================================================================

/**
 * Map comparison result DTO to domain model
 */
export function mapWineComparisonFromDto(dto: CompareResultDto): WineComparison {
  return {
    wines: dto.wines.map(mapWineFromDto),
    attributes: dto.comparison.map(mapComparisonAttributeFromDto),
    aiSummary: dto.ai_summary ?? null,
  }
}

/**
 * Map comparison attribute DTO to domain model
 */
export function mapComparisonAttributeFromDto(dto: ComparisonAttributeDto): ComparisonAttribute {
  return {
    name: dto.attribute,
    values: dto.values,
    winnerIndex: dto.winner_index ?? null,
  }
}

// ============================================================================
// AI Profile Mappers
// ============================================================================

/**
 * Map AI profile DTO to domain model
 */
export function mapAiProfileFromDto(dto: AiProfileDto): AiWineProfile {
  return {
    wineId: dto.wine_id,
    summary: dto.summary,
    idealOccasions: dto.ideal_occasions ?? [],
    foodPairingsDetailed: dto.food_pairings_detailed?.map(mapAiFoodPairingFromDto) ?? [],
    similarWines: dto.similar_wines ?? [],
    personalityTraits: dto.personality_traits ?? [],
    generatedAt: new Date(dto.generated_at),
  }
}

/**
 * Map AI food pairing DTO to domain model
 */
export function mapAiFoodPairingFromDto(dto: AiFoodPairingDto): AiFoodPairing {
  return {
    dish: dto.dish,
    category: dto.category,
    matchScore: dto.match_score,
    reason: dto.reason,
  }
}

// ============================================================================
// Scan Result Mappers
// ============================================================================

/**
 * Map wine scan result DTO to domain model
 */
export function mapWineScanResultFromDto(dto: WineScanResultDto): WineScanResult {
  return {
    confidence: dto.confidence,
    wine: dto.wine ? mapWineSummaryFromDto(dto.wine) : null,
    suggestions: dto.suggestions?.map(mapWineSummaryFromDto) ?? [],
  }
}

// ============================================================================
// Domain to DTO Mappers (for API requests)
// ============================================================================

/**
 * Map Wine domain model to DTO for API requests
 */
export function mapWineToDto(wine: Partial<Wine>): Partial<WineDto> {
  return {
    name: wine.name,
    winery: wine.winery,
    type: wine.type,
    region: wine.region,
    country: wine.country,
    grape_varieties: wine.grapeVarieties,
    vintage: wine.vintage ?? undefined,
    alcohol_content: wine.alcoholContent ?? undefined,
    price: wine.price ?? undefined,
    description: wine.description,
    image_url: wine.imageUrl ?? undefined,
    food_pairings: wine.foodPairings,
  }
}

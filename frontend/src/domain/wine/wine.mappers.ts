/**
 * Wine domain mappers
 *
 * Functions to map between DTOs (API layer) and domain models.
 * All mappers include null checks and default values for safety.
 * Note: Backend uses camelCase for all JSON fields.
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
    winery: dto.winery?.name ?? '',
    type: dto.wineType,
    region: dto.winery?.region ?? '',
    country: dto.winery?.country ?? '',
    grapeVarieties: dto.grapes ?? [],
    vintage: dto.vintage ?? null,
    alcoholContent: dto.alcoholContent ?? null,
    price: dto.priceMin ?? null,
    rating: dto.ratingAverage ? Number(dto.ratingAverage) : null,
    reviewCount: dto.ratingCount ?? 0,
    description: dto.descriptionEs ?? dto.descriptionEn ?? '',
    imageUrl: dto.imageUrl ?? null,
    tastingNotes: null, // Not in current backend response
    foodPairings: [],
    awards: [],
    createdAt: dto.createdAt ? new Date(dto.createdAt) : new Date(),
    updatedAt: dto.createdAt ? new Date(dto.createdAt) : new Date(),
  }
}

/**
 * Map Wine summary DTO to domain summary
 */
export function mapWineSummaryFromDto(dto: WineSummaryDto): WineSummary {
  return {
    id: dto.id,
    name: dto.name,
    winery: dto.wineryName ?? '',
    type: dto.wineType,
    region: dto.region ?? '',
    country: dto.country ?? '',
    vintage: dto.vintage ?? null,
    price: dto.priceMin ? Number(dto.priceMin) : null,
    rating: dto.ratingAverage ? Number(dto.ratingAverage) : null,
    reviewCount: dto.ratingCount ?? 0,
    imageUrl: dto.imageUrl ?? null,
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
    wineId: dto.wineId,
    userId: dto.userId,
    userName: dto.username,
    userAvatar: dto.userAvatarUrl ?? null,
    rating: dto.rating,
    comment: dto.comment ?? '',
    helpfulCount: dto.helpfulCount ?? 0,
    createdAt: new Date(dto.createdAt),
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
    wines: [], // Would need to fetch wine details separately
    attributes: [],
    aiSummary: dto.comparisonContent?.summary ?? null,
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
    wineId: dto.wineId,
    summary: dto.profileContent?.summary ?? '',
    idealOccasions: dto.profileContent?.idealOccasions ?? [],
    foodPairingsDetailed: dto.profileContent?.foodPairings?.map(dish => ({
      dish,
      category: 'General',
      matchScore: 0.8,
      reason: '',
    })) ?? [],
    similarWines: dto.profileContent?.similarWines ?? [],
    personalityTraits: [],
    generatedAt: new Date(dto.generatedAt),
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
export function mapWineToDto(wine: Partial<Wine>): Record<string, unknown> {
  return {
    name: wine.name,
    wineType: wine.type,
    grapes: wine.grapeVarieties,
    vintage: wine.vintage ?? undefined,
    alcoholContent: wine.alcoholContent ?? undefined,
    priceMin: wine.price ?? undefined,
    descriptionEs: wine.description,
    imageUrl: wine.imageUrl ?? undefined,
  }
}

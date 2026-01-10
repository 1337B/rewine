import type { Wine, WineReview, TastingNotes } from './wine.types'
import type { WineDto, WineReviewDto, TastingNotesDto } from '@api/dto/wines.dto'

/**
 * Map Wine DTO to domain model
 */
export function mapWineFromDto(dto: WineDto): Wine {
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
    createdAt: new Date(dto.created_at),
    updatedAt: new Date(dto.updated_at),
  }
}

/**
 * Map tasting notes DTO to domain model
 */
export function mapTastingNotesFromDto(dto: TastingNotesDto): TastingNotes {
  return {
    appearance: dto.appearance,
    aroma: dto.aroma,
    palate: dto.palate,
    finish: dto.finish,
    body: dto.body,
    sweetness: dto.sweetness,
    tannins: dto.tannins,
    acidity: dto.acidity,
  }
}

/**
 * Map Wine review DTO to domain model
 */
export function mapWineReviewFromDto(dto: WineReviewDto): WineReview {
  return {
    id: dto.id,
    wineId: dto.wine_id,
    userId: dto.user_id,
    userName: dto.user_name,
    rating: dto.rating,
    comment: dto.comment,
    createdAt: new Date(dto.created_at),
  }
}

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


/**
 * Wines Service
 *
 * Application service layer for wine-related operations.
 * Orchestrates: API client calls -> DTO mapping -> domain model returns.
 */

import { winesClient } from '@api/clients/wines.client'
import {
  mapWineFromDto,
  mapWineSummaryFromDto,
  mapWineReviewFromDto,
  mapWineToDto,
  mapWineComparisonFromDto,
  mapAiProfileFromDto,
  mapWineScanResultFromDto,
} from '@domain/wine/wine.mappers'
import type {
  Wine,
  WineSummary,
  WineReview,
  WineFilter,
  WineComparison,
  AiWineProfile,
  WineScanResult,
} from '@domain/wine/wine.types'
import type { PaginationMeta } from '@api/api.types'
import type { WineFilterParamsDto, CreateWineRequestDto, UpdateWineRequestDto } from '@api/dto/wines.dto'

// ============================================================================
// Result Types
// ============================================================================

export interface WinesResult {
  wines: WineSummary[]
  pagination: PaginationMeta
}

export interface WineDetailsResult {
  wine: Wine
}

export interface WineReviewsResult {
  reviews: WineReview[]
  pagination: PaginationMeta
}

// ============================================================================
// Service
// ============================================================================

/**
 * Wines service - orchestrates wine-related business logic
 */
export const winesService = {
  // ==========================================================================
  // Wine Retrieval
  // ==========================================================================

  /**
   * Get paginated list of wines
   */
  async getWines(filter?: WineFilter, page = 1, pageSize = 20): Promise<WinesResult> {
    const params = mapFilterToParams(filter, page, pageSize)
    const response = await winesClient.getWines(params)

    return {
      wines: response.data.map(mapWineSummaryFromDto),
      pagination: response.pagination,
    }
  },

  /**
   * Get a single wine by ID
   */
  async getWine(id: string): Promise<Wine> {
    const response = await winesClient.getWine(id)
    return mapWineFromDto(response)
  },

  /**
   * Get similar wines
   */
  async getSimilarWines(wineId: string, limit = 5): Promise<WineSummary[]> {
    const response = await winesClient.getSimilarWines(wineId, limit)
    return response.map(mapWineSummaryFromDto)
  },

  /**
   * Get recommended wines for current user
   */
  async getRecommendedWines(limit = 10): Promise<WineSummary[]> {
    const response = await winesClient.getRecommendedWines(limit)
    return response.map(mapWineSummaryFromDto)
  },

  /**
   * Get popular wines
   */
  async getPopularWines(limit = 10): Promise<WineSummary[]> {
    const response = await winesClient.getPopularWines(limit)
    return response.map(mapWineSummaryFromDto)
  },

  // ==========================================================================
  // Wine CRUD (Admin)
  // ==========================================================================

  /**
   * Create a new wine
   */
  async createWine(wine: Omit<Wine, 'id' | 'rating' | 'reviewCount' | 'awards' | 'createdAt' | 'updatedAt'>): Promise<Wine> {
    const dto = mapWineToDto(wine) as CreateWineRequestDto
    const response = await winesClient.createWine(dto)
    return mapWineFromDto(response)
  },

  /**
   * Update a wine
   */
  async updateWine(id: string, wine: Partial<Wine>): Promise<Wine> {
    const dto = mapWineToDto(wine) as UpdateWineRequestDto
    const response = await winesClient.updateWine(id, dto)
    return mapWineFromDto(response)
  },

  /**
   * Delete a wine
   */
  async deleteWine(id: string): Promise<void> {
    await winesClient.deleteWine(id)
  },

  // ==========================================================================
  // Reviews
  // ==========================================================================

  /**
   * Get reviews for a wine
   */
  async getWineReviews(wineId: string, page = 1, pageSize = 10): Promise<WineReviewsResult> {
    const response = await winesClient.getWineReviews(wineId, { page, pageSize })

    return {
      reviews: response.data.map(mapWineReviewFromDto),
      pagination: response.pagination,
    }
  },

  /**
   * Add a review to a wine
   */
  async addReview(wineId: string, rating: number, comment?: string): Promise<WineReview> {
    const response = await winesClient.createWineReview(wineId, { rating, comment })
    return mapWineReviewFromDto(response)
  },

  /**
   * Update a review
   */
  async updateReview(wineId: string, reviewId: string, rating: number, comment?: string): Promise<WineReview> {
    const response = await winesClient.updateWineReview(wineId, reviewId, { rating, comment })
    return mapWineReviewFromDto(response)
  },

  /**
   * Delete a review
   */
  async deleteReview(wineId: string, reviewId: string): Promise<void> {
    await winesClient.deleteWineReview(wineId, reviewId)
  },

  /**
   * Mark a review as helpful
   */
  async markReviewHelpful(wineId: string, reviewId: string): Promise<void> {
    await winesClient.markReviewHelpful(wineId, reviewId)
  },

  // ==========================================================================
  // Comparison & AI
  // ==========================================================================

  /**
   * Compare multiple wines
   */
  async compareWines(wineIds: string[]): Promise<WineComparison> {
    const response = await winesClient.compareWines(wineIds)
    return mapWineComparisonFromDto(response)
  },

  /**
   * Get AI-generated wine profile
   */
  async getAiProfile(wineId: string): Promise<AiWineProfile> {
    const response = await winesClient.getAiProfile(wineId)
    return mapAiProfileFromDto(response)
  },

  /**
   * Scan wine label image
   */
  async scanWineLabel(imageFile: File | string): Promise<WineScanResult> {
    const response = await winesClient.scanWineLabel(imageFile)
    return mapWineScanResultFromDto(response)
  },

  // ==========================================================================
  // Favorites
  // ==========================================================================

  /**
   * Add wine to favorites
   */
  async addToFavorites(wineId: string): Promise<void> {
    await winesClient.addToFavorites(wineId)
  },

  /**
   * Remove wine from favorites
   */
  async removeFromFavorites(wineId: string): Promise<void> {
    await winesClient.removeFromFavorites(wineId)
  },

  /**
   * Get user's favorite wines
   */
  async getFavoriteWines(page = 1, pageSize = 20): Promise<WinesResult> {
    const response = await winesClient.getFavoriteWines({ page, pageSize })

    return {
      wines: response.data.map(mapWineSummaryFromDto),
      pagination: response.pagination,
    }
  },
}

// ============================================================================
// Helper Functions
// ============================================================================

/**
 * Map domain filter to API filter params
 */
function mapFilterToParams(filter: WineFilter | undefined, page: number, pageSize: number): WineFilterParamsDto {
  // Map sortBy from domain format to DTO format
  const sortByMap: Record<string, 'name' | 'price' | 'rating' | 'vintage' | 'created_at'> = {
    'name': 'name',
    'price': 'price',
    'rating': 'rating',
    'vintage': 'vintage',
    'createdAt': 'created_at',
  }

  return {
    page,
    page_size: pageSize,
    search: filter?.search,
    type: filter?.type,
    region: filter?.region,
    country: filter?.country,
    grape_variety: filter?.grapeVariety,
    winery: filter?.winery,
    min_price: filter?.minPrice,
    max_price: filter?.maxPrice,
    min_rating: filter?.minRating,
    max_rating: filter?.maxRating,
    vintage: filter?.vintage,
    min_vintage: filter?.minVintage,
    max_vintage: filter?.maxVintage,
    sort_by: filter?.sortBy ? sortByMap[filter.sortBy] : undefined,
    sort_order: filter?.sortOrder,
  }
}

export default winesService

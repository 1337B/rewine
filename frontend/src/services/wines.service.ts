import { winesClient } from '@api/clients/wines.client'
import { mapWineFromDto, mapWineReviewFromDto, mapWineToDto } from '@domain/wine/wine.mappers'
import type { Wine, WineReview, WineFilter } from '@domain/wine/wine.types'
import type { PaginationMeta } from '@api/api.types'
import type { WineFilterParamsDto, CreateWineRequestDto } from '@api/dto/wines.dto'

export interface WinesResult {
  wines: Wine[]
  pagination: PaginationMeta
}

export interface WineReviewsResult {
  reviews: WineReview[]
  pagination: PaginationMeta
}

/**
 * Wines service
 */
export const winesService = {
  /**
   * Get paginated list of wines
   */
  async getWines(filter?: WineFilter, page = 1, pageSize = 20): Promise<WinesResult> {
    const params: WineFilterParamsDto = {
      page,
      page_size: pageSize,
      search: filter?.search,
      type: filter?.type,
      region: filter?.region,
      grape_variety: filter?.grapeVariety,
      min_price: filter?.minPrice,
      max_price: filter?.maxPrice,
      min_rating: filter?.minRating,
      vintage: filter?.vintage,
      sort_by: filter?.sortBy,
      sort_order: filter?.sortOrder,
    }

    const response = await winesClient.getWines(params)

    return {
      wines: response.data.map(mapWineFromDto),
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
   * Create a new wine
   */
  async createWine(wine: Omit<Wine, 'id' | 'rating' | 'reviewCount' | 'createdAt' | 'updatedAt'>): Promise<Wine> {
    const dto = mapWineToDto(wine) as CreateWineRequestDto
    const response = await winesClient.createWine(dto)
    return mapWineFromDto(response)
  },

  /**
   * Update a wine
   */
  async updateWine(id: string, wine: Partial<Wine>): Promise<Wine> {
    const dto = mapWineToDto(wine)
    const response = await winesClient.updateWine(id, dto)
    return mapWineFromDto(response)
  },

  /**
   * Delete a wine
   */
  async deleteWine(id: string): Promise<void> {
    await winesClient.deleteWine(id)
  },

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
   * Delete a review
   */
  async deleteReview(wineId: string, reviewId: string): Promise<void> {
    await winesClient.deleteWineReview(wineId, reviewId)
  },

  /**
   * Get similar wines
   */
  async getSimilarWines(wineId: string, limit = 5): Promise<Wine[]> {
    const response = await winesClient.getSimilarWines(wineId, limit)
    return response.map(mapWineFromDto)
  },
}

export default winesService


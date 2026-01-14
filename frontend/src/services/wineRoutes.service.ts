import { wineRoutesClient } from '@api/clients/wineRoutes.client'
import { mapWineRouteFromDto, mapWineRouteReviewFromDto, mapWineRouteToDto } from '@domain/route/route.mappers'
import type { WineRoute, WineRouteFilter, WineRouteReview } from '@domain/route/route.types'
import type { PaginationMeta } from '@api/api.types'
import type { WineRouteFilterParamsDto, CreateWineRouteRequestDto, AddRouteStopRequestDto } from '@api/dto/wineRoutes.dto'

export interface WineRoutesResult {
  routes: WineRoute[]
  pagination: PaginationMeta
}

export interface WineRouteReviewsResult {
  reviews: WineRouteReview[]
  pagination: PaginationMeta
}

/**
 * Wine Routes service
 */
export const wineRoutesService = {
  /**
   * Get paginated list of wine routes
   */
  async getWineRoutes(filter?: WineRouteFilter, page = 1, pageSize = 20): Promise<WineRoutesResult> {
    const params: WineRouteFilterParamsDto = {
      page,
      page_size: pageSize,
      search: filter?.search,
      region: filter?.region,
      difficulty: filter?.difficulty,
      min_duration: filter?.minDuration,
      max_duration: filter?.maxDuration,
      min_distance: filter?.minDistance,
      max_distance: filter?.maxDistance,
      min_rating: filter?.minRating,
      sort_by: filter?.sortBy,
      sort_order: filter?.sortOrder,
    }

    const response = await wineRoutesClient.getWineRoutes(params)

    return {
      routes: response.data.map(mapWineRouteFromDto),
      pagination: response.pagination,
    }
  },

  /**
   * Get a single wine route by ID
   */
  async getWineRoute(id: string): Promise<WineRoute> {
    const response = await wineRoutesClient.getWineRoute(id)
    return mapWineRouteFromDto(response)
  },

  /**
   * Create a new wine route
   */
  async createWineRoute(route: Omit<WineRoute, 'id' | 'stops' | 'rating' | 'reviewCount' | 'createdAt' | 'updatedAt'>): Promise<WineRoute> {
    const dto = mapWineRouteToDto(route) as CreateWineRouteRequestDto
    const response = await wineRoutesClient.createWineRoute(dto)
    return mapWineRouteFromDto(response)
  },

  /**
   * Update a wine route
   */
  async updateWineRoute(id: string, route: Partial<WineRoute>): Promise<WineRoute> {
    const dto = mapWineRouteToDto(route) as Partial<CreateWineRouteRequestDto>
    const response = await wineRoutesClient.updateWineRoute(id, dto)
    return mapWineRouteFromDto(response)
  },

  /**
   * Delete a wine route
   */
  async deleteWineRoute(id: string): Promise<void> {
    await wineRoutesClient.deleteWineRoute(id)
  },

  /**
   * Add a stop to a wine route
   */
  async addRouteStop(routeId: string, stop: AddRouteStopRequestDto): Promise<WineRoute> {
    const response = await wineRoutesClient.addRouteStop(routeId, stop)
    return mapWineRouteFromDto(response)
  },

  /**
   * Remove a stop from a wine route
   */
  async removeRouteStop(routeId: string, stopId: string): Promise<WineRoute> {
    const response = await wineRoutesClient.removeRouteStop(routeId, stopId)
    return mapWineRouteFromDto(response)
  },

  /**
   * Get reviews for a wine route
   */
  async getRouteReviews(routeId: string, page = 1, pageSize = 10): Promise<WineRouteReviewsResult> {
    const response = await wineRoutesClient.getRouteReviews(routeId, { page, pageSize })

    return {
      reviews: response.data.map(mapWineRouteReviewFromDto),
      pagination: response.pagination,
    }
  },

  /**
   * Add a review to a wine route
   */
  async addReview(routeId: string, rating: number, comment: string | undefined, visitedAt: Date): Promise<WineRouteReview> {
    const response = await wineRoutesClient.createRouteReview(routeId, {
      rating,
      comment,
      visited_at: visitedAt.toISOString(),
    })
    return mapWineRouteReviewFromDto(response)
  },

  /**
   * Publish a wine route
   */
  async publishRoute(routeId: string): Promise<WineRoute> {
    const response = await wineRoutesClient.publishRoute(routeId)
    return mapWineRouteFromDto(response)
  },

  /**
   * Unpublish a wine route
   */
  async unpublishRoute(routeId: string): Promise<WineRoute> {
    const response = await wineRoutesClient.unpublishRoute(routeId)
    return mapWineRouteFromDto(response)
  },
}

export default wineRoutesService


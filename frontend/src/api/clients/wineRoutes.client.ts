import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type { ApiResponse, PaginatedResponse } from '@api/api.types'
import type {
  WineRouteDto,
  WineRouteReviewDto,
  CreateWineRouteRequestDto,
  AddRouteStopRequestDto,
  CreateWineRouteReviewRequestDto,
  WineRouteFilterParamsDto,
} from '@api/dto/wineRoutes.dto'

/**
 * Wine Routes API client
 */
export const wineRoutesClient = {
  /**
   * Get paginated list of wine routes
   */
  async getWineRoutes(params?: WineRouteFilterParamsDto): Promise<PaginatedResponse<WineRouteDto>> {
    const response = await http.get<PaginatedResponse<WineRouteDto>>(API_ENDPOINTS.WINE_ROUTES, {
      params,
    })
    return response.data
  },

  /**
   * Get a single wine route by ID
   */
  async getWineRoute(id: string): Promise<WineRouteDto> {
    const response = await http.get<ApiResponse<WineRouteDto>>(`${API_ENDPOINTS.WINE_ROUTES}/${id}`)
    return response.data.data
  },

  /**
   * Create a new wine route
   */
  async createWineRoute(data: CreateWineRouteRequestDto): Promise<WineRouteDto> {
    const response = await http.post<ApiResponse<WineRouteDto>>(API_ENDPOINTS.WINE_ROUTES, data)
    return response.data.data
  },

  /**
   * Update a wine route
   */
  async updateWineRoute(id: string, data: Partial<CreateWineRouteRequestDto>): Promise<WineRouteDto> {
    const response = await http.patch<ApiResponse<WineRouteDto>>(
      `${API_ENDPOINTS.WINE_ROUTES}/${id}`,
      data
    )
    return response.data.data
  },

  /**
   * Delete a wine route
   */
  async deleteWineRoute(id: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.WINE_ROUTES}/${id}`)
  },

  /**
   * Add a stop to a wine route
   */
  async addRouteStop(routeId: string, data: AddRouteStopRequestDto): Promise<WineRouteDto> {
    const response = await http.post<ApiResponse<WineRouteDto>>(
      `${API_ENDPOINTS.WINE_ROUTES}/${routeId}/stops`,
      data
    )
    return response.data.data
  },

  /**
   * Remove a stop from a wine route
   */
  async removeRouteStop(routeId: string, stopId: string): Promise<WineRouteDto> {
    const response = await http.delete<ApiResponse<WineRouteDto>>(
      `${API_ENDPOINTS.WINE_ROUTES}/${routeId}/stops/${stopId}`
    )
    return response.data.data
  },

  /**
   * Get reviews for a wine route
   */
  async getRouteReviews(routeId: string, params?: { page?: number; pageSize?: number }): Promise<PaginatedResponse<WineRouteReviewDto>> {
    const response = await http.get<PaginatedResponse<WineRouteReviewDto>>(
      `${API_ENDPOINTS.WINE_ROUTES}/${routeId}/reviews`,
      { params }
    )
    return response.data
  },

  /**
   * Create a review for a wine route
   */
  async createRouteReview(routeId: string, data: CreateWineRouteReviewRequestDto): Promise<WineRouteReviewDto> {
    const response = await http.post<ApiResponse<WineRouteReviewDto>>(
      `${API_ENDPOINTS.WINE_ROUTES}/${routeId}/reviews`,
      data
    )
    return response.data.data
  },

  /**
   * Publish a wine route
   */
  async publishRoute(routeId: string): Promise<WineRouteDto> {
    const response = await http.post<ApiResponse<WineRouteDto>>(
      `${API_ENDPOINTS.WINE_ROUTES}/${routeId}/publish`
    )
    return response.data.data
  },

  /**
   * Unpublish a wine route
   */
  async unpublishRoute(routeId: string): Promise<WineRouteDto> {
    const response = await http.post<ApiResponse<WineRouteDto>>(
      `${API_ENDPOINTS.WINE_ROUTES}/${routeId}/unpublish`
    )
    return response.data.data
  },
}

export default wineRoutesClient


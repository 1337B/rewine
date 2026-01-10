import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type { ApiResponse, PaginatedResponse } from '@api/api.types'
import type {
  WineDto,
  WineReviewDto,
  CreateWineRequestDto,
  CreateWineReviewRequestDto,
  WineFilterParamsDto,
} from '@api/dto/wines.dto'

/**
 * Wines API client
 */
export const winesClient = {
  /**
   * Get paginated list of wines
   */
  async getWines(params?: WineFilterParamsDto): Promise<PaginatedResponse<WineDto>> {
    const response = await http.get<PaginatedResponse<WineDto>>(API_ENDPOINTS.WINES, {
      params,
    })
    return response.data
  },

  /**
   * Get a single wine by ID
   */
  async getWine(id: string): Promise<WineDto> {
    const response = await http.get<ApiResponse<WineDto>>(`${API_ENDPOINTS.WINES}/${id}`)
    return response.data.data
  },

  /**
   * Create a new wine (admin only)
   */
  async createWine(data: CreateWineRequestDto): Promise<WineDto> {
    const response = await http.post<ApiResponse<WineDto>>(API_ENDPOINTS.WINES, data)
    return response.data.data
  },

  /**
   * Update a wine (admin only)
   */
  async updateWine(id: string, data: Partial<CreateWineRequestDto>): Promise<WineDto> {
    const response = await http.patch<ApiResponse<WineDto>>(
      `${API_ENDPOINTS.WINES}/${id}`,
      data
    )
    return response.data.data
  },

  /**
   * Delete a wine (admin only)
   */
  async deleteWine(id: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.WINES}/${id}`)
  },

  /**
   * Get reviews for a wine
   */
  async getWineReviews(wineId: string, params?: { page?: number; pageSize?: number }): Promise<PaginatedResponse<WineReviewDto>> {
    const response = await http.get<PaginatedResponse<WineReviewDto>>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews`,
      { params }
    )
    return response.data
  },

  /**
   * Create a review for a wine
   */
  async createWineReview(wineId: string, data: CreateWineReviewRequestDto): Promise<WineReviewDto> {
    const response = await http.post<ApiResponse<WineReviewDto>>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews`,
      data
    )
    return response.data.data
  },

  /**
   * Delete a wine review
   */
  async deleteWineReview(wineId: string, reviewId: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.WINES}/${wineId}/reviews/${reviewId}`)
  },

  /**
   * Get similar wines
   */
  async getSimilarWines(wineId: string, limit?: number): Promise<WineDto[]> {
    const response = await http.get<ApiResponse<WineDto[]>>(
      `${API_ENDPOINTS.WINES}/${wineId}/similar`,
      { params: { limit } }
    )
    return response.data.data
  },
}

export default winesClient


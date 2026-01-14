/**
 * Wines API Client
 *
 * Handles all wine-related API calls.
 * Uses the configured axios instance for automatic token handling.
 */

import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type { ApiResponse, PaginatedResponse } from '@api/api.types'
import type {
  WineSummaryDto,
  WineDetailsDto,
  WineReviewDto,
  CreateWineRequestDto,
  UpdateWineRequestDto,
  CreateWineReviewRequestDto,
  WineFilterParamsDto,
  CompareResultDto,
  AiProfileDto,
  WineScanResultDto,
} from '@api/dto/wines.dto'

/**
 * Wines API client
 */
export const winesClient = {
  // ============================================================================
  // Wine CRUD
  // ============================================================================

  /**
   * Get paginated list of wines
   * @param params Filter and pagination parameters
   */
  async getWines(params?: WineFilterParamsDto): Promise<PaginatedResponse<WineSummaryDto>> {
    const response = await http.get<PaginatedResponse<WineSummaryDto>>(API_ENDPOINTS.WINES, {
      params,
    })
    return response.data
  },

  /**
   * Get a single wine by ID
   * @param id Wine ID
   */
  async getWine(id: string): Promise<WineDetailsDto> {
    const response = await http.get<ApiResponse<WineDetailsDto>>(`${API_ENDPOINTS.WINES}/${id}`)
    return response.data.data
  },

  /**
   * Create a new wine (admin only)
   * @param data Wine data
   */
  async createWine(data: CreateWineRequestDto): Promise<WineDetailsDto> {
    const response = await http.post<ApiResponse<WineDetailsDto>>(API_ENDPOINTS.WINES, data)
    return response.data.data
  },

  /**
   * Update a wine (admin only)
   * @param id Wine ID
   * @param data Partial wine data
   */
  async updateWine(id: string, data: UpdateWineRequestDto): Promise<WineDetailsDto> {
    const response = await http.patch<ApiResponse<WineDetailsDto>>(
      `${API_ENDPOINTS.WINES}/${id}`,
      data
    )
    return response.data.data
  },

  /**
   * Delete a wine (admin only)
   * @param id Wine ID
   */
  async deleteWine(id: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.WINES}/${id}`)
  },

  // ============================================================================
  // Reviews
  // ============================================================================

  /**
   * Get reviews for a wine
   * @param wineId Wine ID
   * @param params Pagination parameters
   */
  async getWineReviews(
    wineId: string,
    params?: { page?: number; pageSize?: number }
  ): Promise<PaginatedResponse<WineReviewDto>> {
    const response = await http.get<PaginatedResponse<WineReviewDto>>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews`,
      { params: { page: params?.page, page_size: params?.pageSize } }
    )
    return response.data
  },

  /**
   * Create a review for a wine
   * @param wineId Wine ID
   * @param data Review data
   */
  async createWineReview(wineId: string, data: CreateWineReviewRequestDto): Promise<WineReviewDto> {
    const response = await http.post<ApiResponse<WineReviewDto>>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews`,
      data
    )
    return response.data.data
  },

  /**
   * Update a wine review
   * @param wineId Wine ID
   * @param reviewId Review ID
   * @param data Review data
   */
  async updateWineReview(
    wineId: string,
    reviewId: string,
    data: CreateWineReviewRequestDto
  ): Promise<WineReviewDto> {
    const response = await http.patch<ApiResponse<WineReviewDto>>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews/${reviewId}`,
      data
    )
    return response.data.data
  },

  /**
   * Delete a wine review
   * @param wineId Wine ID
   * @param reviewId Review ID
   */
  async deleteWineReview(wineId: string, reviewId: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.WINES}/${wineId}/reviews/${reviewId}`)
  },

  /**
   * Mark a review as helpful
   * @param wineId Wine ID
   * @param reviewId Review ID
   */
  async markReviewHelpful(wineId: string, reviewId: string): Promise<void> {
    await http.post(`${API_ENDPOINTS.WINES}/${wineId}/reviews/${reviewId}/helpful`)
  },

  // ============================================================================
  // Discovery & Recommendations
  // ============================================================================

  /**
   * Get similar wines
   * @param wineId Wine ID
   * @param limit Maximum number of results
   */
  async getSimilarWines(wineId: string, limit = 5): Promise<WineSummaryDto[]> {
    const response = await http.get<ApiResponse<WineSummaryDto[]>>(
      `${API_ENDPOINTS.WINES}/${wineId}/similar`,
      { params: { limit } }
    )
    return response.data.data
  },

  /**
   * Get recommended wines for current user
   * @param limit Maximum number of results
   */
  async getRecommendedWines(limit = 10): Promise<WineSummaryDto[]> {
    const response = await http.get<ApiResponse<WineSummaryDto[]>>(
      `${API_ENDPOINTS.WINES}/recommended`,
      { params: { limit } }
    )
    return response.data.data
  },

  /**
   * Get popular wines
   * @param limit Maximum number of results
   */
  async getPopularWines(limit = 10): Promise<WineSummaryDto[]> {
    const response = await http.get<ApiResponse<WineSummaryDto[]>>(
      `${API_ENDPOINTS.WINES}/popular`,
      { params: { limit } }
    )
    return response.data.data
  },

  // ============================================================================
  // Comparison & AI
  // ============================================================================

  /**
   * Compare multiple wines
   * @param wineIds Array of wine IDs to compare
   */
  async compareWines(wineIds: string[]): Promise<CompareResultDto> {
    const response = await http.post<ApiResponse<CompareResultDto>>(
      `${API_ENDPOINTS.WINES}/compare`,
      { wine_ids: wineIds }
    )
    return response.data.data
  },

  /**
   * Get AI-generated wine profile
   * @param wineId Wine ID
   */
  async getAiProfile(wineId: string): Promise<AiProfileDto> {
    const response = await http.get<ApiResponse<AiProfileDto>>(
      `${API_ENDPOINTS.WINES}/${wineId}/ai-profile`
    )
    return response.data.data
  },

  /**
   * Scan wine label image
   * @param imageFile Image file or base64 data
   */
  async scanWineLabel(imageFile: File | string): Promise<WineScanResultDto> {
    const formData = new FormData()
    if (typeof imageFile === 'string') {
      formData.append('image_base64', imageFile)
    } else {
      formData.append('image', imageFile)
    }

    const response = await http.post<ApiResponse<WineScanResultDto>>(
      `${API_ENDPOINTS.WINES}/scan`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    )
    return response.data.data
  },

  // ============================================================================
  // Favorites
  // ============================================================================

  /**
   * Add wine to favorites
   * @param wineId Wine ID
   */
  async addToFavorites(wineId: string): Promise<void> {
    await http.post(`${API_ENDPOINTS.WINES}/${wineId}/favorite`)
  },

  /**
   * Remove wine from favorites
   * @param wineId Wine ID
   */
  async removeFromFavorites(wineId: string): Promise<void> {
    await http.delete(`${API_ENDPOINTS.WINES}/${wineId}/favorite`)
  },

  /**
   * Get user's favorite wines
   * @param params Pagination parameters
   */
  async getFavoriteWines(
    params?: { page?: number; pageSize?: number }
  ): Promise<PaginatedResponse<WineSummaryDto>> {
    const response = await http.get<PaginatedResponse<WineSummaryDto>>(
      `${API_ENDPOINTS.WINES}/favorites`,
      { params: { page: params?.page, page_size: params?.pageSize } }
    )
    return response.data
  },
}

export default winesClient

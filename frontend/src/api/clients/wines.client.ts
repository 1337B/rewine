/**
 * Wines API Client
 *
 * Handles all wine-related API calls.
 * Uses the configured axios instance for automatic token handling.
 * Note: Backend returns data directly (no wrapper).
 */

import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type { PaginatedResponse } from '@api/api.types'
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
  WinesPageResponseDto,
  ReviewsPageResponseDto,
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
  async getWines(params?: WineFilterParamsDto): Promise<WinesPageResponseDto> {
    const response = await http.get<WinesPageResponseDto>(API_ENDPOINTS.WINES, {
      params,
    })
    return response.data
  },

  /**
   * Get a single wine by ID
   * @param id Wine ID
   */
  async getWine(id: string): Promise<WineDetailsDto> {
    const response = await http.get<WineDetailsDto>(`${API_ENDPOINTS.WINES}/${id}`)
    return response.data
  },

  /**
   * Create a new wine (admin only)
   * @param data Wine data
   */
  async createWine(data: CreateWineRequestDto): Promise<WineDetailsDto> {
    const response = await http.post<WineDetailsDto>(API_ENDPOINTS.WINES, data)
    return response.data
  },

  /**
   * Update a wine (admin only)
   * @param id Wine ID
   * @param data Partial wine data
   */
  async updateWine(id: string, data: UpdateWineRequestDto): Promise<WineDetailsDto> {
    const response = await http.patch<WineDetailsDto>(
      `${API_ENDPOINTS.WINES}/${id}`,
      data
    )
    return response.data
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
  ): Promise<ReviewsPageResponseDto> {
    const response = await http.get<ReviewsPageResponseDto>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews`,
      { params: { page: params?.page, size: params?.pageSize } }
    )
    return response.data
  },

  /**
   * Create a review for a wine
   * @param wineId Wine ID
   * @param data Review data
   */
  async createWineReview(wineId: string, data: CreateWineReviewRequestDto): Promise<WineReviewDto> {
    const response = await http.post<WineReviewDto>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews`,
      data
    )
    return response.data
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
    const response = await http.patch<WineReviewDto>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews/${reviewId}`,
      data
    )
    return response.data
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
   * Mark a review as helpful (toggle like)
   * @param wineId Wine ID
   * @param reviewId Review ID
   */
  async markReviewHelpful(wineId: string, reviewId: string): Promise<boolean> {
    const response = await http.post<boolean>(
      `${API_ENDPOINTS.WINES}/${wineId}/reviews/${reviewId}/like`
    )
    return response.data
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
    const response = await http.get<WineSummaryDto[]>(
      `${API_ENDPOINTS.WINES}/${wineId}/similar`,
      { params: { limit } }
    )
    return response.data
  },

  /**
   * Get recommended wines for current user
   * @param limit Maximum number of results
   */
  async getRecommendedWines(limit = 10): Promise<WineSummaryDto[]> {
    const response = await http.get<WineSummaryDto[]>(
      `${API_ENDPOINTS.WINES}/recommended`,
      { params: { limit } }
    )
    return response.data
  },

  /**
   * Get popular wines
   * @param limit Maximum number of results
   */
  async getPopularWines(limit = 10): Promise<WineSummaryDto[]> {
    const response = await http.get<WineSummaryDto[]>(
      `${API_ENDPOINTS.WINES}/popular`,
      { params: { limit } }
    )
    return response.data
  },

  // ============================================================================
  // Comparison & AI
  // ============================================================================

  /**
   * Compare two wines
   * @param wineAId First wine ID
   * @param wineBId Second wine ID
   * @param language Language for comparison (e.g., 'es', 'en')
   */
  async compareWines(wineAId: string, wineBId: string, language = 'es'): Promise<CompareResultDto> {
    const response = await http.post<CompareResultDto>(
      `${API_ENDPOINTS.WINES}/compare`,
      { wineAId, wineBId, language }
    )
    return response.data
  },

  /**
   * Get AI-generated wine profile
   * @param wineId Wine ID
   * @param language Language for profile (e.g., 'es', 'en')
   */
  async getAiProfile(wineId: string, language = 'es'): Promise<AiProfileDto> {
    const response = await http.get<AiProfileDto>(
      `${API_ENDPOINTS.WINES}/${wineId}/ai-profile`,
      { params: { language } }
    )
    return response.data
  },

  /**
   * Generate AI profile for a wine
   * @param wineId Wine ID
   * @param language Language for profile
   */
  async generateAiProfile(wineId: string, language = 'es'): Promise<AiProfileDto> {
    const response = await http.post<AiProfileDto>(
      `${API_ENDPOINTS.WINES}/${wineId}/ai-profile`,
      { language }
    )
    return response.data
  },

  /**
   * Scan wine label image
   * @param imageFile Image file or base64 data
   */
  async scanWineLabel(imageFile: File | string): Promise<WineScanResultDto> {
    const formData = new FormData()
    if (typeof imageFile === 'string') {
      formData.append('imageBase64', imageFile)
    } else {
      formData.append('image', imageFile)
    }

    const response = await http.post<WineScanResultDto>(
      `${API_ENDPOINTS.WINES}/scan`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    )
    return response.data
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
      { params: { page: params?.page, size: params?.pageSize } }
    )
    return response.data
  },
}

export default winesClient

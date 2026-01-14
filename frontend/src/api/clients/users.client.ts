import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type { ApiResponse, PaginatedResponse } from '@api/api.types'
import type {
  UserDto,
  UserProfileDto,
  UserStatsDto,
  UpdateUserRequestDto,
  UpdateUserPreferencesRequestDto,
  ChangePasswordRequestDto,
  UserFilterParamsDto,
} from '@api/dto/users.dto'

/**
 * Users API client
 */
export const usersClient = {
  /**
   * Get paginated list of users (admin only)
   */
  async getUsers(params?: UserFilterParamsDto): Promise<PaginatedResponse<UserDto>> {
    const response = await http.get<PaginatedResponse<UserDto>>(API_ENDPOINTS.USERS, {
      params,
    })
    return response.data
  },

  /**
   * Get a user by ID
   */
  async getUser(id: string): Promise<UserDto> {
    const response = await http.get<ApiResponse<UserDto>>(`${API_ENDPOINTS.USERS}/${id}`)
    return response.data.data
  },

  /**
   * Get user public profile
   */
  async getUserProfile(id: string): Promise<UserProfileDto> {
    const response = await http.get<ApiResponse<UserProfileDto>>(
      `${API_ENDPOINTS.USERS}/${id}/profile`
    )
    return response.data.data
  },

  /**
   * Get user stats
   */
  async getUserStats(id: string): Promise<UserStatsDto> {
    const response = await http.get<ApiResponse<UserStatsDto>>(
      `${API_ENDPOINTS.USERS}/${id}/stats`
    )
    return response.data.data
  },

  /**
   * Update current user
   */
  async updateUser(data: UpdateUserRequestDto): Promise<UserDto> {
    const response = await http.patch<ApiResponse<UserDto>>(
      `${API_ENDPOINTS.USERS}/me`,
      data
    )
    return response.data.data
  },

  /**
   * Update user preferences
   */
  async updatePreferences(data: UpdateUserPreferencesRequestDto): Promise<UserDto> {
    const response = await http.patch<ApiResponse<UserDto>>(
      `${API_ENDPOINTS.USERS}/me/preferences`,
      data
    )
    return response.data.data
  },

  /**
   * Change password
   */
  async changePassword(data: ChangePasswordRequestDto): Promise<void> {
    await http.post(`${API_ENDPOINTS.USERS}/me/change-password`, data)
  },

  /**
   * Delete user account
   */
  async deleteAccount(): Promise<void> {
    await http.delete(`${API_ENDPOINTS.USERS}/me`)
  },

  /**
   * Update user role (admin only)
   */
  async updateUserRole(userId: string, role: string): Promise<UserDto> {
    const response = await http.patch<ApiResponse<UserDto>>(
      `${API_ENDPOINTS.USERS}/${userId}/role`,
      { role }
    )
    return response.data.data
  },

  /**
   * Ban user (admin only)
   */
  async banUser(userId: string): Promise<void> {
    await http.post(`${API_ENDPOINTS.USERS}/${userId}/ban`)
  },

  /**
   * Unban user (admin only)
   */
  async unbanUser(userId: string): Promise<void> {
    await http.post(`${API_ENDPOINTS.USERS}/${userId}/unban`)
  },
}

export default usersClient


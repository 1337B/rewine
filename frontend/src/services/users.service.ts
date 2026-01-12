import { usersClient } from '@api/clients/users.client'
import { mapUserFromDto, mapUserProfileFromDto, mapUserStatsFromDto, mapUserPreferencesToDto } from '@domain/user/user.mappers'
import type { User, UserProfile, UserStats, UserPreferences } from '@domain/user/user.types'
import type { PaginationMeta } from '@api/api.types'
import type { UserFilterParamsDto, UpdateUserPreferencesRequestDto } from '@api/dto/users.dto'

export interface UsersResult {
  users: User[]
  pagination: PaginationMeta
}

/**
 * Users service
 */
export const usersService = {
  /**
   * Get paginated list of users (admin only)
   */
  async getUsers(params?: UserFilterParamsDto): Promise<UsersResult> {
    const response = await usersClient.getUsers(params)

    return {
      users: response.data.map(mapUserFromDto),
      pagination: response.pagination,
    }
  },

  /**
   * Get a user by ID
   */
  async getUser(id: string): Promise<User> {
    const response = await usersClient.getUser(id)
    return mapUserFromDto(response)
  },

  /**
   * Get user public profile
   */
  async getUserProfile(id: string): Promise<UserProfile> {
    const response = await usersClient.getUserProfile(id)
    return mapUserProfileFromDto(response)
  },

  /**
   * Get user stats
   */
  async getUserStats(id: string): Promise<UserStats> {
    const response = await usersClient.getUserStats(id)
    return mapUserStatsFromDto(response)
  },

  /**
   * Update current user
   */
  async updateUser(name?: string, avatar?: string): Promise<User> {
    const response = await usersClient.updateUser({ name, avatar })
    return mapUserFromDto(response)
  },

  /**
   * Update user preferences
   */
  async updatePreferences(preferences: Partial<UserPreferences>): Promise<User> {
    const dto = mapUserPreferencesToDto(preferences) as UpdateUserPreferencesRequestDto
    const response = await usersClient.updatePreferences(dto)
    return mapUserFromDto(response)
  },

  /**
   * Change password
   */
  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await usersClient.changePassword({
      current_password: currentPassword,
      new_password: newPassword,
    })
  },

  /**
   * Delete user account
   */
  async deleteAccount(): Promise<void> {
    await usersClient.deleteAccount()
  },

  /**
   * Update user role (admin only)
   */
  async updateUserRole(userId: string, role: string): Promise<User> {
    const response = await usersClient.updateUserRole(userId, role)
    return mapUserFromDto(response)
  },

  /**
   * Ban user (admin only)
   */
  async banUser(userId: string): Promise<void> {
    await usersClient.banUser(userId)
  },

  /**
   * Unban user (admin only)
   */
  async unbanUser(userId: string): Promise<void> {
    await usersClient.unbanUser(userId)
  },
}

export default usersService


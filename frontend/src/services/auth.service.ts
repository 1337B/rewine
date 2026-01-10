import { authClient } from '@api/clients/auth.client'
import { mapUserFromDto } from '@domain/user/user.mappers'
import type { User } from '@domain/user/user.types'
import type { LoginRequestDto, RegisterRequestDto } from '@api/dto/auth.dto'

export interface AuthTokens {
  accessToken: string
  refreshToken: string
  expiresIn: number
}

export interface AuthResult {
  user: User
  tokens: AuthTokens
}

/**
 * Authentication service
 */
export const authService = {
  /**
   * Login user with email and password
   */
  async login(email: string, password: string): Promise<AuthResult> {
    const data: LoginRequestDto = { email, password }
    const response = await authClient.login(data)

    return {
      user: mapUserFromDto({
        id: response.user.id,
        email: response.user.email,
        name: response.user.name,
        avatar: response.user.avatar,
        roles: response.user.roles,
        is_verified: response.user.is_verified,
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString(),
      }),
      tokens: {
        accessToken: response.access_token,
        refreshToken: response.refresh_token,
        expiresIn: response.expires_in,
      },
    }
  },

  /**
   * Register a new user
   */
  async register(email: string, password: string, name: string): Promise<AuthResult> {
    const data: RegisterRequestDto = { email, password, name }
    const response = await authClient.register(data)

    return {
      user: mapUserFromDto({
        id: response.user.id,
        email: response.user.email,
        name: response.user.name,
        avatar: response.user.avatar,
        roles: response.user.roles,
        is_verified: response.user.is_verified,
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString(),
      }),
      tokens: {
        accessToken: response.access_token,
        refreshToken: response.refresh_token,
        expiresIn: response.expires_in,
      },
    }
  },

  /**
   * Refresh access token
   */
  async refreshToken(refreshToken: string): Promise<{ accessToken: string; expiresIn: number }> {
    const response = await authClient.refreshToken({ refresh_token: refreshToken })
    return {
      accessToken: response.access_token,
      expiresIn: response.expires_in,
    }
  },

  /**
   * Logout user
   */
  async logout(): Promise<void> {
    await authClient.logout()
  },

  /**
   * Get current authenticated user
   */
  async getCurrentUser(): Promise<User> {
    const response = await authClient.getCurrentUser()
    return mapUserFromDto({
      id: response.id,
      email: response.email,
      name: response.name,
      avatar: response.avatar,
      roles: response.roles,
      is_verified: response.is_verified,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString(),
    })
  },
}

export default authService


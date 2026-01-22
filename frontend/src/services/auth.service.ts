import { authClient } from '@api/clients/auth.client'
import { mapUserFromDto } from '@domain/user/user.mappers'
import type { User } from '@domain/user/user.types'
import { loginUserSchema, registerUserSchema } from '@domain/user/user.validators'
import type { LoginRequestDto, RegisterRequestDto } from '@api/dto/auth.dto'
import { ZodError } from 'zod'

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
 * Auth validation error class
 */
export class AuthValidationError extends Error {
  public readonly fieldErrors: Record<string, string[]>

  constructor(message: string, fieldErrors: Record<string, string[]> = {}) {
    super(message)
    this.name = 'AuthValidationError'
    this.fieldErrors = fieldErrors
  }
}

/**
 * Auth API error class
 */
export class AuthApiError extends Error {
  public readonly statusCode: number
  public readonly isSessionExpired: boolean

  constructor(message: string, statusCode: number = 500) {
    super(message)
    this.name = 'AuthApiError'
    this.statusCode = statusCode
    this.isSessionExpired = statusCode === 401
  }
}

/**
 * Parse Zod errors into field errors map
 */
function parseZodErrors(error: ZodError): Record<string, string[]> {
  const fieldErrors: Record<string, string[]> = {}

  for (const issue of error.issues) {
    const path = issue.path.join('.')
    if (!fieldErrors[path]) {
      fieldErrors[path] = []
    }
    fieldErrors[path].push(issue.message)
  }

  return fieldErrors
}

/**
 * Authentication service with validation
 */
export const authService = {
  /**
   * Login user with email/username and password
   * @throws AuthValidationError if validation fails
   * @throws AuthApiError if API call fails
   */
  async login(usernameOrEmail: string, password: string): Promise<AuthResult> {
    // Basic validation
    if (!usernameOrEmail || !password) {
      throw new AuthValidationError('Invalid login credentials', {
        usernameOrEmail: !usernameOrEmail ? ['Email or username is required'] : [],
        password: !password ? ['Password is required'] : [],
      })
    }

    try {
      const data: LoginRequestDto = { usernameOrEmail, password }
      const response = await authClient.login(data)

      return {
        user: mapUserFromDto({
          id: response.user.id,
          username: response.user.username,
          email: response.user.email,
          name: response.user.name ?? '',
          avatarUrl: response.user.avatarUrl,
          roles: response.user.roles,
          emailVerified: response.user.emailVerified,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        }),
        tokens: {
          accessToken: response.accessToken,
          refreshToken: response.refreshToken,
          expiresIn: response.expiresIn,
        },
      }
    } catch (error: unknown) {
      if (error instanceof Error && 'response' in error) {
        const axiosError = error as { response?: { status?: number; data?: { message?: string } } }
        const statusCode = axiosError.response?.status ?? 500
        const message = axiosError.response?.data?.message ?? 'Login failed'
        throw new AuthApiError(message, statusCode)
      }
      throw new AuthApiError('Network error. Please try again.', 0)
    }
  },

  /**
   * Register a new user
   * @throws AuthValidationError if validation fails
   * @throws AuthApiError if API call fails
   */
  async register(username: string, email: string, password: string, name?: string): Promise<AuthResult> {
    // Basic validation
    if (!username || !email || !password) {
      throw new AuthValidationError('Invalid registration data', {
        username: !username ? ['Username is required'] : [],
        email: !email ? ['Email is required'] : [],
        password: !password ? ['Password is required'] : [],
      })
    }

    try {
      const data: RegisterRequestDto = { username, email, password, name }
      const response = await authClient.register(data)

      return {
        user: mapUserFromDto({
          id: response.user.id,
          username: response.user.username,
          email: response.user.email,
          name: response.user.name ?? '',
          avatarUrl: response.user.avatarUrl,
          roles: response.user.roles,
          emailVerified: response.user.emailVerified,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        }),
        tokens: {
          accessToken: response.accessToken,
          refreshToken: response.refreshToken,
          expiresIn: response.expiresIn,
        },
      }
    } catch (error: unknown) {
      if (error instanceof Error && 'response' in error) {
        const axiosError = error as { response?: { status?: number; data?: { message?: string } } }
        const statusCode = axiosError.response?.status ?? 500
        const message = axiosError.response?.data?.message ?? 'Registration failed'
        throw new AuthApiError(message, statusCode)
      }
      throw new AuthApiError('Network error. Please try again.', 0)
    }
  },

  /**
   * Refresh access token
   * @throws AuthApiError if refresh fails (session expired)
   */
  async refreshToken(refreshToken: string): Promise<{ accessToken: string; expiresIn: number }> {
    try {
      const response = await authClient.refreshToken({ refreshToken })
      return {
        accessToken: response.accessToken,
        expiresIn: response.expiresIn,
      }
    } catch (error: unknown) {
      if (error instanceof Error && 'response' in error) {
        const axiosError = error as { response?: { status?: number } }
        throw new AuthApiError('Session expired. Please login again.', axiosError.response?.status ?? 401)
      }
      throw new AuthApiError('Session expired. Please login again.', 401)
    }
  },

  /**
   * Logout user
   */
  async logout(): Promise<void> {
    try {
      await authClient.logout()
    } catch {
      // Ignore logout errors - we'll clear the session anyway
    }
  },

  /**
   * Get current authenticated user
   * @throws AuthApiError if not authenticated or session expired
   */
  async getCurrentUser(): Promise<User> {
    try {
      const response = await authClient.getCurrentUser()
      return mapUserFromDto({
        id: response.id,
        username: response.username,
        email: response.email,
        name: response.name ?? '',
        avatarUrl: response.avatarUrl,
        roles: response.roles,
        emailVerified: response.emailVerified,
        createdAt: response.createdAt ?? new Date().toISOString(),
        updatedAt: response.updatedAt ?? new Date().toISOString(),
      })
    } catch (error: unknown) {
      if (error instanceof Error && 'response' in error) {
        const axiosError = error as { response?: { status?: number } }
        const statusCode = axiosError.response?.status ?? 500
        throw new AuthApiError(
          statusCode === 401 ? 'Session expired' : 'Failed to get user',
          statusCode
        )
      }
      throw new AuthApiError('Network error', 0)
    }
  },
}

export default authService

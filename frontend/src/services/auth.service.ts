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
   * Login user with email and password
   * @throws AuthValidationError if validation fails
   * @throws AuthApiError if API call fails
   */
  async login(email: string, password: string): Promise<AuthResult> {
    // Validate input
    const validationResult = loginUserSchema.safeParse({ email, password })

    if (!validationResult.success) {
      throw new AuthValidationError(
        'Invalid login credentials',
        parseZodErrors(validationResult.error)
      )
    }

    try {
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
  async register(email: string, password: string, name: string, confirmPassword?: string): Promise<AuthResult> {
    // Validate input
    const validationResult = registerUserSchema.safeParse({
      email,
      password,
      name,
      confirmPassword: confirmPassword ?? password
    })

    if (!validationResult.success) {
      throw new AuthValidationError(
        'Invalid registration data',
        parseZodErrors(validationResult.error)
      )
    }

    try {
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
      const response = await authClient.refreshToken({ refresh_token: refreshToken })
      return {
        accessToken: response.access_token,
        expiresIn: response.expires_in,
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
        email: response.email,
        name: response.name,
        avatar: response.avatar,
        roles: response.roles,
        is_verified: response.is_verified,
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString(),
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

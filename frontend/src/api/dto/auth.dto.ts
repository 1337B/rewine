/**
 * Authentication DTOs
 *
 * Data Transfer Objects for authentication API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 * Note: Backend uses camelCase for all JSON fields.
 */

import type { UserRole } from '@domain/user/user.types'

// ============================================================================
// Request DTOs
// ============================================================================

/**
 * Login request payload
 * Backend accepts username OR email in usernameOrEmail field
 */
export interface LoginRequestDto {
  usernameOrEmail: string
  password: string
}

/**
 * Registration request payload
 */
export interface RegisterRequestDto {
  username: string
  email: string
  password: string
  name?: string
}

/**
 * Token refresh request payload
 */
export interface RefreshTokenRequestDto {
  refreshToken: string
}

/**
 * Password reset request
 */
export interface ForgotPasswordRequestDto {
  email: string
}

/**
 * Reset password with token
 */
export interface ResetPasswordRequestDto {
  token: string
  newPassword: string
}

/**
 * Email verification request
 */
export interface VerifyEmailRequestDto {
  token: string
}

// ============================================================================
// Response DTOs
// ============================================================================

/**
 * Authentication response (login/register)
 * Backend returns camelCase fields
 */
export interface AuthResponseDto {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: 'Bearer'
  user: AuthUserDto
}

/**
 * Authenticated user data from backend
 */
export interface AuthUserDto {
  id: string
  username: string
  email: string
  name: string | null
  avatarUrl?: string | null
  roles: string[]
  emailVerified: boolean
}

/**
 * Token refresh response
 */
export interface RefreshTokenResponseDto {
  accessToken: string
  expiresIn: number
}

/**
 * Current user response (GET /auth/me)
 */
export interface MeResponseDto extends AuthUserDto {
  createdAt: string
  updatedAt: string
}

/**
 * Generic success response
 */
export interface AuthMessageResponseDto {
  message: string
}

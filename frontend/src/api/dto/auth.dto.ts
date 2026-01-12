/**
 * Authentication DTOs
 *
 * Data Transfer Objects for authentication API endpoints.
 * These represent the exact shape of data sent to/from the backend.
 */

import type { UserRole } from '@domain/user/user.types'

// ============================================================================
// Request DTOs
// ============================================================================

/**
 * Login request payload
 */
export interface LoginRequestDto {
  email: string
  password: string
}

/**
 * Registration request payload
 */
export interface RegisterRequestDto {
  email: string
  password: string
  name: string
}

/**
 * Token refresh request payload
 */
export interface RefreshTokenRequestDto {
  refresh_token: string
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
  new_password: string
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
 */
export interface AuthResponseDto {
  access_token: string
  refresh_token: string
  expires_in: number
  token_type: 'Bearer'
  user: AuthUserDto
}

/**
 * Authenticated user data
 */
export interface AuthUserDto {
  id: string
  email: string
  name: string
  avatar?: string | null
  roles: UserRole[]
  is_verified: boolean
}

/**
 * Token refresh response
 */
export interface RefreshTokenResponseDto {
  access_token: string
  expires_in: number
}

/**
 * Current user response (GET /auth/me)
 */
export interface MeResponseDto extends AuthUserDto {
  created_at: string
  updated_at: string
}

/**
 * Generic success response
 */
export interface AuthMessageResponseDto {
  message: string
}

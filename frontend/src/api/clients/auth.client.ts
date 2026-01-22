/**
 * Authentication API Client
 *
 * Handles all authentication-related API calls.
 * Uses the configured axios instance for automatic token handling.
 * Note: Backend returns data directly (no wrapper).
 */

import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type {
  LoginRequestDto,
  RegisterRequestDto,
  RefreshTokenRequestDto,
  ForgotPasswordRequestDto,
  ResetPasswordRequestDto,
  VerifyEmailRequestDto,
  AuthResponseDto,
  RefreshTokenResponseDto,
  MeResponseDto,
  AuthMessageResponseDto,
} from '@api/dto/auth.dto'

/**
 * Authentication API client
 */
export const authClient = {
  /**
   * Login with email/username and password
   * @param data Login credentials
   * @returns Auth tokens and user data
   */
  async login(data: LoginRequestDto): Promise<AuthResponseDto> {
    const response = await http.post<AuthResponseDto>(
      API_ENDPOINTS.AUTH.LOGIN,
      data
    )
    return response.data
  },

  /**
   * Register a new user
   * @param data Registration data
   * @returns Auth tokens and user data
   */
  async register(data: RegisterRequestDto): Promise<AuthResponseDto> {
    const response = await http.post<AuthResponseDto>(
      API_ENDPOINTS.AUTH.REGISTER,
      data
    )
    return response.data
  },

  /**
   * Refresh access token using refresh token
   * @param data Refresh token
   * @returns New access token
   */
  async refreshToken(data: RefreshTokenRequestDto): Promise<RefreshTokenResponseDto> {
    const response = await http.post<RefreshTokenResponseDto>(
      API_ENDPOINTS.AUTH.REFRESH,
      data
    )
    return response.data
  },

  /**
   * Logout current user (invalidate tokens)
   */
  async logout(): Promise<void> {
    await http.post(API_ENDPOINTS.AUTH.LOGOUT)
  },

  /**
   * Get current authenticated user
   * @returns Current user data with preferences
   */
  async getCurrentUser(): Promise<MeResponseDto> {
    const response = await http.get<MeResponseDto>(API_ENDPOINTS.AUTH.ME)
    return response.data
  },

  /**
   * Request password reset email
   * @param data Email address
   */
  async forgotPassword(data: ForgotPasswordRequestDto): Promise<AuthMessageResponseDto> {
    const response = await http.post<AuthMessageResponseDto>(
      `${API_ENDPOINTS.AUTH.BASE}/forgot-password`,
      data
    )
    return response.data
  },

  /**
   * Reset password with token
   * @param data New password and reset token
   */
  async resetPassword(data: ResetPasswordRequestDto): Promise<AuthMessageResponseDto> {
    const response = await http.post<AuthMessageResponseDto>(
      `${API_ENDPOINTS.AUTH.BASE}/reset-password`,
      data
    )
    return response.data
  },

  /**
   * Verify email address
   * @param data Verification token
   */
  async verifyEmail(data: VerifyEmailRequestDto): Promise<AuthMessageResponseDto> {
    const response = await http.post<AuthMessageResponseDto>(
      `${API_ENDPOINTS.AUTH.BASE}/verify-email`,
      data
    )
    return response.data
  },

  /**
   * Resend verification email
   */
  async resendVerificationEmail(): Promise<AuthMessageResponseDto> {
    const response = await http.post<AuthMessageResponseDto>(
      `${API_ENDPOINTS.AUTH.BASE}/resend-verification`
    )
    return response.data
  },
}

export default authClient

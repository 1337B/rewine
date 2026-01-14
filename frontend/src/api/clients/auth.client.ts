/**
 * Authentication API Client
 *
 * Handles all authentication-related API calls.
 * Uses the configured axios instance for automatic token handling.
 */

import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type { ApiResponse } from '@api/api.types'
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
   * Login with email and password
   * @param data Login credentials
   * @returns Auth tokens and user data
   */
  async login(data: LoginRequestDto): Promise<AuthResponseDto> {
    const response = await http.post<ApiResponse<AuthResponseDto>>(
      API_ENDPOINTS.AUTH.LOGIN,
      data
    )
    return response.data.data
  },

  /**
   * Register a new user
   * @param data Registration data
   * @returns Auth tokens and user data
   */
  async register(data: RegisterRequestDto): Promise<AuthResponseDto> {
    const response = await http.post<ApiResponse<AuthResponseDto>>(
      API_ENDPOINTS.AUTH.REGISTER,
      data
    )
    return response.data.data
  },

  /**
   * Refresh access token using refresh token
   * @param data Refresh token
   * @returns New access token
   */
  async refreshToken(data: RefreshTokenRequestDto): Promise<RefreshTokenResponseDto> {
    const response = await http.post<ApiResponse<RefreshTokenResponseDto>>(
      API_ENDPOINTS.AUTH.REFRESH,
      data
    )
    return response.data.data
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
    const response = await http.get<ApiResponse<MeResponseDto>>(API_ENDPOINTS.AUTH.ME)
    return response.data.data
  },

  /**
   * Request password reset email
   * @param data Email address
   */
  async forgotPassword(data: ForgotPasswordRequestDto): Promise<AuthMessageResponseDto> {
    const response = await http.post<ApiResponse<AuthMessageResponseDto>>(
      `${API_ENDPOINTS.AUTH.BASE}/forgot-password`,
      data
    )
    return response.data.data
  },

  /**
   * Reset password with token
   * @param data New password and reset token
   */
  async resetPassword(data: ResetPasswordRequestDto): Promise<AuthMessageResponseDto> {
    const response = await http.post<ApiResponse<AuthMessageResponseDto>>(
      `${API_ENDPOINTS.AUTH.BASE}/reset-password`,
      data
    )
    return response.data.data
  },

  /**
   * Verify email address
   * @param data Verification token
   */
  async verifyEmail(data: VerifyEmailRequestDto): Promise<AuthMessageResponseDto> {
    const response = await http.post<ApiResponse<AuthMessageResponseDto>>(
      `${API_ENDPOINTS.AUTH.BASE}/verify-email`,
      data
    )
    return response.data.data
  },

  /**
   * Resend verification email
   */
  async resendVerificationEmail(): Promise<AuthMessageResponseDto> {
    const response = await http.post<ApiResponse<AuthMessageResponseDto>>(
      `${API_ENDPOINTS.AUTH.BASE}/resend-verification`
    )
    return response.data.data
  },
}

export default authClient

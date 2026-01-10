import http from '@app/http'
import { API_ENDPOINTS } from '@config/constants'
import type { ApiResponse } from '@api/api.types'
import type {
  LoginRequestDto,
  RegisterRequestDto,
  RefreshTokenRequestDto,
  AuthResponseDto,
  RefreshTokenResponseDto,
  AuthUserDto,
} from '@api/dto/auth.dto'

/**
 * Authentication API client
 */
export const authClient = {
  /**
   * Login with email and password
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
   */
  async register(data: RegisterRequestDto): Promise<AuthResponseDto> {
    const response = await http.post<ApiResponse<AuthResponseDto>>(
      API_ENDPOINTS.AUTH.REGISTER,
      data
    )
    return response.data.data
  },

  /**
   * Refresh access token
   */
  async refreshToken(data: RefreshTokenRequestDto): Promise<RefreshTokenResponseDto> {
    const response = await http.post<ApiResponse<RefreshTokenResponseDto>>(
      API_ENDPOINTS.AUTH.REFRESH,
      data
    )
    return response.data.data
  },

  /**
   * Logout current user
   */
  async logout(): Promise<void> {
    await http.post(API_ENDPOINTS.AUTH.LOGOUT)
  },

  /**
   * Get current authenticated user
   */
  async getCurrentUser(): Promise<AuthUserDto> {
    const response = await http.get<ApiResponse<AuthUserDto>>(API_ENDPOINTS.AUTH.ME)
    return response.data.data
  },
}

export default authClient


import type { UserRole } from '@domain/user/user.types'

export interface LoginRequestDto {
  email: string
  password: string
}

export interface RegisterRequestDto {
  email: string
  password: string
  name: string
}

export interface RefreshTokenRequestDto {
  refresh_token: string
}

// Auth response DTOs
export interface AuthResponseDto {
  access_token: string
  refresh_token: string
  expires_in: number
  token_type: string
  user: AuthUserDto
}

export interface AuthUserDto {
  id: string
  email: string
  name: string
  avatar?: string
  roles: UserRole[]
  is_verified: boolean
}

export interface RefreshTokenResponseDto {
  access_token: string
  expires_in: number
}


/**
 * Shared API types
 */

export interface ApiResponse<T> {
  data: T
  message?: string
  timestamp: string
}

export interface PaginatedResponse<T> {
  data: T[]
  pagination: PaginationMeta
}

export interface PaginationMeta {
  page: number
  pageSize: number
  totalItems: number
  totalPages: number
  hasNext: boolean
  hasPrevious: boolean
}

export interface PaginationParams {
  page?: number
  pageSize?: number
}

export interface SortParams {
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export interface ApiError {
  code: string
  message: string
  details?: Record<string, string[]>
  timestamp: string
}

export interface ListParams extends PaginationParams, SortParams {
  search?: string
}

export interface UploadResponse {
  url: string
  filename: string
  size: number
  mimeType: string
}


/**
 * Shared API types for request/response handling
 */

// ============================================================================
// Error Types
// ============================================================================

/**
 * Standardized API error structure
 */
export interface ApiError {
  /** HTTP status code */
  status: number
  /** Application-specific error code */
  code: string
  /** Human-readable error message */
  message: string
  /** Field-specific validation errors */
  details?: Record<string, string[]>
  /** Error timestamp */
  timestamp: string
  /** Request path that caused the error */
  path?: string
  /** Trace ID for debugging */
  traceId?: string
}

/**
 * Error codes used throughout the application
 */
export const ERROR_CODES = {
  // Authentication errors
  UNAUTHORIZED: 'UNAUTHORIZED',
  TOKEN_EXPIRED: 'TOKEN_EXPIRED',
  TOKEN_INVALID: 'TOKEN_INVALID',
  REFRESH_TOKEN_EXPIRED: 'REFRESH_TOKEN_EXPIRED',

  // Authorization errors
  FORBIDDEN: 'FORBIDDEN',
  INSUFFICIENT_PERMISSIONS: 'INSUFFICIENT_PERMISSIONS',

  // Validation errors
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  INVALID_INPUT: 'INVALID_INPUT',

  // Resource errors
  NOT_FOUND: 'NOT_FOUND',
  CONFLICT: 'CONFLICT',
  ALREADY_EXISTS: 'ALREADY_EXISTS',

  // Server errors
  INTERNAL_ERROR: 'INTERNAL_ERROR',
  SERVICE_UNAVAILABLE: 'SERVICE_UNAVAILABLE',

  // Network errors
  NETWORK_ERROR: 'NETWORK_ERROR',
  TIMEOUT: 'TIMEOUT',

  // Unknown
  UNKNOWN: 'UNKNOWN',
} as const

export type ErrorCode = (typeof ERROR_CODES)[keyof typeof ERROR_CODES]

// ============================================================================
// Response Types
// ============================================================================

/**
 * Standard API response wrapper
 */
export interface ApiResponse<T> {
  /** Response data */
  data: T
  /** Optional metadata */
  meta?: ResponseMeta
  /** Optional success message */
  message?: string
  /** Response timestamp */
  timestamp: string
}

/**
 * Response metadata
 */
export interface ResponseMeta {
  /** Request ID for tracing */
  requestId?: string
  /** Response time in milliseconds */
  responseTime?: number
}

// ============================================================================
// Pagination Types
// ============================================================================

/**
 * Pagination request parameters
 */
export interface PageRequest {
  /** Page number (1-based) */
  page?: number
  /** Number of items per page */
  pageSize?: number
  /** Field to sort by */
  sortBy?: string
  /** Sort direction */
  sortOrder?: 'asc' | 'desc'
}

/**
 * Pagination metadata in response
 */
export interface PageMeta {
  /** Current page number (1-based) */
  page: number
  /** Number of items per page */
  pageSize: number
  /** Total number of items */
  totalItems: number
  /** Total number of pages */
  totalPages: number
  /** Whether there is a next page */
  hasNext: boolean
  /** Whether there is a previous page */
  hasPrevious: boolean
}

/**
 * Paginated response wrapper
 */
export interface PageResponse<T> {
  /** Array of items for the current page */
  data: T[]
  /** Pagination metadata */
  pagination: PageMeta
}

// ============================================================================
// Legacy Aliases (for backward compatibility)
// ============================================================================

/** @deprecated Use PageMeta instead */
export type PaginationMeta = PageMeta

/** @deprecated Use PageRequest instead */
export type PaginationParams = Pick<PageRequest, 'page' | 'pageSize'>

/** @deprecated Use PageResponse instead */
export type PaginatedResponse<T> = PageResponse<T>

// ============================================================================
// Utility Types
// ============================================================================

/**
 * Sort parameters
 */
export interface SortParams {
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

/**
 * Common list query parameters
 */
export interface ListParams extends PageRequest {
  /** Search query string */
  search?: string
  /** Filter parameters */
  filters?: Record<string, string | string[] | number | boolean>
}

/**
 * Upload response
 */
export interface UploadResponse {
  /** URL of the uploaded file */
  url: string
  /** Original filename */
  filename: string
  /** File size in bytes */
  size: number
  /** MIME type */
  mimeType: string
}

/**
 * Type guard to check if an object is an ApiError
 */
export function isApiError(error: unknown): error is ApiError {
  return (
    typeof error === 'object' &&
    error !== null &&
    'status' in error &&
    'code' in error &&
    'message' in error
  )
}

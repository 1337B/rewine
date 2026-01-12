/**
 * API Error normalization utilities
 * Converts various error types into a standardized ApiError format
 */

import type { AxiosError } from 'axios'
import { type ApiError, ERROR_CODES, type ErrorCode } from '@api/api.types'

/**
 * Backend error response structure (what the API returns)
 */
interface BackendErrorResponse {
  code?: string
  message?: string
  details?: Record<string, string[]>
  error?: string
  errors?: Record<string, string[]>
  path?: string
  traceId?: string
  timestamp?: string
}

/**
 * Map HTTP status codes to error codes
 */
function getErrorCodeFromStatus(status: number): ErrorCode {
  switch (status) {
    case 400:
      return ERROR_CODES.VALIDATION_ERROR
    case 401:
      return ERROR_CODES.UNAUTHORIZED
    case 403:
      return ERROR_CODES.FORBIDDEN
    case 404:
      return ERROR_CODES.NOT_FOUND
    case 409:
      return ERROR_CODES.CONFLICT
    case 422:
      return ERROR_CODES.INVALID_INPUT
    case 500:
      return ERROR_CODES.INTERNAL_ERROR
    case 502:
    case 503:
    case 504:
      return ERROR_CODES.SERVICE_UNAVAILABLE
    default:
      return ERROR_CODES.UNKNOWN
  }
}

/**
 * Get a user-friendly message for common error codes
 */
function getDefaultMessage(code: ErrorCode, status: number): string {
  switch (code) {
    case ERROR_CODES.UNAUTHORIZED:
    case ERROR_CODES.TOKEN_EXPIRED:
    case ERROR_CODES.TOKEN_INVALID:
      return 'Your session has expired. Please log in again.'
    case ERROR_CODES.FORBIDDEN:
    case ERROR_CODES.INSUFFICIENT_PERMISSIONS:
      return 'You do not have permission to perform this action.'
    case ERROR_CODES.NOT_FOUND:
      return 'The requested resource was not found.'
    case ERROR_CODES.VALIDATION_ERROR:
    case ERROR_CODES.INVALID_INPUT:
      return 'Please check your input and try again.'
    case ERROR_CODES.CONFLICT:
    case ERROR_CODES.ALREADY_EXISTS:
      return 'This resource already exists.'
    case ERROR_CODES.SERVICE_UNAVAILABLE:
      return 'Service is temporarily unavailable. Please try again later.'
    case ERROR_CODES.NETWORK_ERROR:
      return 'Network error. Please check your connection.'
    case ERROR_CODES.TIMEOUT:
      return 'Request timed out. Please try again.'
    case ERROR_CODES.INTERNAL_ERROR:
    default:
      return `An error occurred (${status}). Please try again.`
  }
}

/**
 * Normalize an Axios error into a standardized ApiError
 */
export function normalizeAxiosError(error: AxiosError<BackendErrorResponse>): ApiError {
  const timestamp = new Date().toISOString()

  // Network error (no response received)
  if (!error.response) {
    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      return {
        status: 0,
        code: ERROR_CODES.TIMEOUT,
        message: 'Request timed out. Please try again.',
        timestamp,
      }
    }

    return {
      status: 0,
      code: ERROR_CODES.NETWORK_ERROR,
      message: 'Network error. Please check your connection.',
      timestamp,
    }
  }

  const { status, data } = error.response
  const backendCode = data?.code
  const errorCode = backendCode || getErrorCodeFromStatus(status)

  // Extract message from various possible response formats
  const message =
    data?.message ||
    data?.error ||
    error.message ||
    getDefaultMessage(errorCode as ErrorCode, status)

  // Extract validation details
  const details = data?.details || data?.errors

  return {
    status,
    code: errorCode,
    message,
    details,
    timestamp: data?.timestamp || timestamp,
    path: data?.path || error.config?.url,
    traceId: data?.traceId,
  }
}

/**
 * Normalize any error into an ApiError
 */
export function normalizeError(error: unknown): ApiError {
  const timestamp = new Date().toISOString()

  // Already an ApiError
  if (isApiErrorLike(error)) {
    return {
      status: error.status ?? 500,
      code: error.code ?? ERROR_CODES.UNKNOWN,
      message: error.message ?? 'An unknown error occurred',
      details: error.details,
      timestamp: error.timestamp ?? timestamp,
    }
  }

  // Axios error
  if (isAxiosError(error)) {
    return normalizeAxiosError(error)
  }

  // Standard Error
  if (error instanceof Error) {
    return {
      status: 500,
      code: ERROR_CODES.UNKNOWN,
      message: error.message,
      timestamp,
    }
  }

  // String error
  if (typeof error === 'string') {
    return {
      status: 500,
      code: ERROR_CODES.UNKNOWN,
      message: error,
      timestamp,
    }
  }

  // Unknown error
  return {
    status: 500,
    code: ERROR_CODES.UNKNOWN,
    message: 'An unknown error occurred',
    timestamp,
  }
}

/**
 * Type guard for Axios errors
 */
function isAxiosError(error: unknown): error is AxiosError<BackendErrorResponse> {
  return (
    typeof error === 'object' &&
    error !== null &&
    'isAxiosError' in error &&
    (error as AxiosError).isAxiosError === true
  )
}

/**
 * Type guard for ApiError-like objects
 */
function isApiErrorLike(error: unknown): error is Partial<ApiError> & { message: string } {
  return (
    typeof error === 'object' &&
    error !== null &&
    'message' in error &&
    typeof (error as Record<string, unknown>).message === 'string'
  )
}

/**
 * Check if error is an authentication error
 */
export function isAuthError(error: ApiError): boolean {
  return (
    error.status === 401 ||
    error.code === ERROR_CODES.UNAUTHORIZED ||
    error.code === ERROR_CODES.TOKEN_EXPIRED ||
    error.code === ERROR_CODES.TOKEN_INVALID
  )
}

/**
 * Check if error is a network/connectivity error
 */
export function isNetworkError(error: ApiError): boolean {
  return (
    error.status === 0 ||
    error.code === ERROR_CODES.NETWORK_ERROR ||
    error.code === ERROR_CODES.TIMEOUT
  )
}

/**
 * Check if error is a validation error
 */
export function isValidationError(error: ApiError): boolean {
  return (
    error.status === 400 ||
    error.status === 422 ||
    error.code === ERROR_CODES.VALIDATION_ERROR ||
    error.code === ERROR_CODES.INVALID_INPUT
  )
}

/**
 * Extract field errors from an ApiError
 */
export function getFieldErrors(error: ApiError): Record<string, string> {
  const fieldErrors: Record<string, string> = {}

  if (error.details) {
    for (const [field, messages] of Object.entries(error.details)) {
      if (Array.isArray(messages) && messages.length > 0) {
        fieldErrors[field] = messages[0]
      }
    }
  }

  return fieldErrors
}


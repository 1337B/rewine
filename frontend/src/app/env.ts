/**
 * Typed environment variable access
 *
 * Provides type-safe access to environment variables with:
 * - Default values for development
 * - Validation for required variables in production
 * - Type coercion for numbers and booleans
 */

// =============================================================================
// Environment Helpers
// =============================================================================

const isProd = import.meta.env.PROD
const isDev = import.meta.env.DEV

/**
 * Get a required string environment variable
 * Throws in production if missing, uses default in development
 */
function getRequiredEnvString(key: string, devDefault: string): string {
  const value = import.meta.env[key]

  if (value === undefined || value === '') {
    if (isProd) {
      throw new Error(`[ENV] Missing required environment variable: ${key}`)
    }
    console.warn(`[ENV] Using default value for ${key}: "${devDefault}"`)
    return devDefault
  }

  return value
}

/**
 * Get an optional string environment variable
 */
function getOptionalEnvString(key: string, defaultValue: string = ''): string {
  const value = import.meta.env[key]
  return value !== undefined && value !== '' ? value : defaultValue
}

/**
 * Get a number environment variable
 */
function getEnvNumber(key: string, defaultValue: number): number {
  const value = import.meta.env[key]

  if (value === undefined || value === '') {
    return defaultValue
  }

  const parsed = Number(value)
  if (isNaN(parsed)) {
    console.error(`[ENV] Invalid number for ${key}: "${value}", using default: ${defaultValue}`)
    return defaultValue
  }

  return parsed
}

/**
 * Get a boolean environment variable
 */
function getEnvBoolean(key: string, defaultValue: boolean): boolean {
  const value = import.meta.env[key]

  if (value === undefined || value === '') {
    return defaultValue
  }

  return value === 'true' || value === '1'
}

// =============================================================================
// Environment Configuration
// =============================================================================

/**
 * Validated environment configuration
 *
 * Required variables will throw in production if missing.
 * Optional variables have sensible defaults.
 */
export const env = {
  // -------------------------------------------------------------------------
  // API Configuration
  // -------------------------------------------------------------------------
  /** Base URL for API requests */
  apiBaseUrl: getRequiredEnvString('VITE_API_BASE_URL', '/api/v1'),

  /** API request timeout in milliseconds */
  apiTimeout: getEnvNumber('VITE_API_TIMEOUT', 30000),

  /** Enable mock API (MSW) - should be false in production */
  mockApi: getEnvBoolean('VITE_MOCK_API', isDev),

  // -------------------------------------------------------------------------
  // App Configuration
  // -------------------------------------------------------------------------
  /** Application display name */
  appName: getOptionalEnvString('VITE_APP_NAME', 'Rewine'),

  /** Current environment: development | staging | production */
  appEnv: getOptionalEnvString('VITE_APP_ENV', isDev ? 'development' : 'production'),

  /** Base path for the application */
  basePath: getOptionalEnvString('VITE_BASE_PATH', '/'),

  /** Is development mode */
  isDev,

  /** Is production mode */
  isProd,

  // -------------------------------------------------------------------------
  // Feature Flags
  // -------------------------------------------------------------------------
  /** Wine scanning feature enabled */
  featureWineScan: getEnvBoolean('VITE_FEATURE_WINE_SCAN', false),

  /** Social login (OAuth) enabled */
  featureSocialLogin: getEnvBoolean('VITE_FEATURE_SOCIAL_LOGIN', false),

  // -------------------------------------------------------------------------
  // External Services
  // -------------------------------------------------------------------------
  /** Google Maps API key for location services */
  googleMapsApiKey: getOptionalEnvString('VITE_GOOGLE_MAPS_API_KEY', ''),

  /** Sentry DSN for error tracking */
  sentryDsn: getOptionalEnvString('VITE_SENTRY_DSN', ''),
} as const

// =============================================================================
// Type Export
// =============================================================================

export type Env = typeof env

// =============================================================================
// Validation on Load
// =============================================================================

// Validate critical configuration in production
if (isProd) {
  const warnings: string[] = []

  // Check for mock API in production (should be disabled)
  if (env.mockApi) {
    warnings.push('VITE_MOCK_API is enabled in production - this should be false')
  }

  // Recommend error tracking in production
  if (!env.sentryDsn) {
    warnings.push('VITE_SENTRY_DSN is not configured - error tracking is recommended in production')
  }

  // Log warnings
  if (warnings.length > 0) {
    console.warn('[ENV] Production configuration warnings:')
    warnings.forEach(w => console.warn(`  - ${w}`))
  }
}

// Log environment info in development
if (isDev) {
  console.log('[ENV] Environment loaded:', {
    appEnv: env.appEnv,
    apiBaseUrl: env.apiBaseUrl,
    mockApi: env.mockApi,
  })
}

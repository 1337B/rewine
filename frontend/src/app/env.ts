/**
 * Typed environment variable access with defaults and null checks
 */

function getEnvString(key: string, defaultValue?: string): string {
  const value = import.meta.env[key]
  if (value === undefined || value === '') {
    if (defaultValue !== undefined) {
      return defaultValue
    }
    throw new Error(`Missing required environment variable: ${key}`)
  }
  return value
}

function getEnvNumber(key: string, defaultValue?: number): number {
  const value = import.meta.env[key]
  if (value === undefined || value === '') {
    if (defaultValue !== undefined) {
      return defaultValue
    }
    throw new Error(`Missing required environment variable: ${key}`)
  }
  const parsed = Number(value)
  if (isNaN(parsed)) {
    throw new Error(`Environment variable ${key} is not a valid number`)
  }
  return parsed
}

function getEnvBoolean(key: string, defaultValue?: boolean): boolean {
  const value = import.meta.env[key]
  if (value === undefined || value === '') {
    if (defaultValue !== undefined) {
      return defaultValue
    }
    throw new Error(`Missing required environment variable: ${key}`)
  }
  return value === 'true' || value === '1'
}

export const env = {
  // API
  apiBaseUrl: getEnvString('VITE_API_BASE_URL', 'http://localhost:8080/api'),
  apiTimeout: getEnvNumber('VITE_API_TIMEOUT', 30000),

  // App
  appName: getEnvString('VITE_APP_NAME', 'Rewine'),
  appEnv: getEnvString('VITE_APP_ENV', 'development'),
  isDev: import.meta.env.DEV,
  isProd: import.meta.env.PROD,

  // Feature flags
  featureWineScan: getEnvBoolean('VITE_FEATURE_WINE_SCAN', false),
  featureSocialLogin: getEnvBoolean('VITE_FEATURE_SOCIAL_LOGIN', false),

  // External services
  googleMapsApiKey: getEnvString('VITE_GOOGLE_MAPS_API_KEY', ''),
  sentryDsn: getEnvString('VITE_SENTRY_DSN', ''),
} as const

export type Env = typeof env


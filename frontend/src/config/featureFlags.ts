import { env } from '@app/env'

/**
 * Feature flags for frontend-only toggles
 * These can be overridden by environment variables or remote config
 */

export interface FeatureFlags {
  wineScan: boolean
  socialLogin: boolean
  darkMode: boolean
  wineComparison: boolean
  advancedFilters: boolean
  wineRoutes: boolean
  events: boolean
  cellar: boolean
  recommendations: boolean
  notifications: boolean
}

export const featureFlags: FeatureFlags = {
  // From environment
  wineScan: env.featureWineScan,
  socialLogin: env.featureSocialLogin,

  // Hardcoded (can be changed to env later)
  darkMode: false,
  wineComparison: true,
  advancedFilters: true,
  wineRoutes: true,
  events: true,
  cellar: true,
  recommendations: true,
  notifications: false,
}

/**
 * Check if a feature is enabled
 */
export function isFeatureEnabled(feature: keyof FeatureFlags): boolean {
  return featureFlags[feature] ?? false
}

/**
 * Get all enabled features
 */
export function getEnabledFeatures(): (keyof FeatureFlags)[] {
  return (Object.keys(featureFlags) as (keyof FeatureFlags)[]).filter(
    (key) => featureFlags[key]
  )
}

export default featureFlags


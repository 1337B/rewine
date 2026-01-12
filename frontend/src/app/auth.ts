/**
 * Authentication utilities and token management
 *
 * This module provides:
 * - Auth initialization with HTTP layer configuration
 * - Token accessor functions with null safety
 * - Login/logout helpers with navigation
 * - Role checking utilities
 */

import { useAuthStore } from '@stores/auth.store'
import { router } from './router'
import { configureHttpAuth } from './http'
import type { UserRole } from '@domain/user/user.types'

// ============================================================================
// Initialization
// ============================================================================

/**
 * Initialize authentication state on app startup
 * This function is resilient to errors - the app will mount even if auth fails
 */
export async function initAuth(): Promise<void> {
  try {
    // Configure HTTP layer with auth handlers (avoids circular dependency)
    configureHttpAuth({
      getAccessToken: () => getAccessToken(),
      refreshToken: () => refreshTokenAndGetNew(),
      clearSession: () => clearSession(),
      onSessionExpired: () => handleSessionExpired(),
    })

    // Initialize session from storage
    const authStore = useAuthStore()
    await authStore.initSession()
  } catch (error) {
    console.warn('[Auth] Failed to initialize session:', error)
    // Don't block app initialization if auth fails
  }
}

// ============================================================================
// Token Accessors (with null safety)
// ============================================================================

/**
 * Get the current access token
 * Returns null if not authenticated
 */
export function getAccessToken(): string | null {
  try {
    const authStore = useAuthStore()
    return authStore.accessToken ?? null
  } catch {
    return null
  }
}

/**
 * Get the current refresh token
 * Returns null if not available
 */
export function getRefreshToken(): string | null {
  try {
    const authStore = useAuthStore()
    // Access the private refresh token if exposed, or return null
    return (authStore as unknown as { refreshTokenValue?: string | null }).refreshTokenValue ?? null
  } catch {
    return null
  }
}

/**
 * Check if there is a valid access token
 */
export function hasAccessToken(): boolean {
  const token = getAccessToken()
  return token !== null && token.length > 0
}

/**
 * Refresh the token and return the new access token
 * Used by the HTTP interceptor
 */
async function refreshTokenAndGetNew(): Promise<string> {
  const authStore = useAuthStore()
  await authStore.refreshToken()
  const newToken = authStore.accessToken

  if (!newToken) {
    throw new Error('Failed to get new access token after refresh')
  }

  return newToken
}

// ============================================================================
// Session Management
// ============================================================================

/**
 * Clear the current session
 */
export function clearSession(): void {
  try {
    const authStore = useAuthStore()
    authStore.clearSession()
  } catch (error) {
    console.warn('[Auth] Failed to clear session:', error)
  }
}

/**
 * Handle session expiration
 * Clears session and redirects to login with toast
 */
function handleSessionExpired(): void {
  // Import toast dynamically to avoid circular dependency
  import('@composables/useToast').then(({ useToast }) => {
    const toast = useToast()
    toast.warning('Your session has expired. Please log in again.')
  }).catch(() => {
    // Toast not available, continue without notification
  })

  // Redirect to login with return URL
  const currentPath = router.currentRoute.value.fullPath
  if (currentPath !== '/login') {
    router.push({
      path: '/login',
      query: { returnUrl: currentPath },
    })
  }
}

// ============================================================================
// Auth Actions
// ============================================================================

/**
 * Perform login and redirect to intended destination
 */
export async function login(email: string, password: string, redirectTo?: string): Promise<void> {
  const authStore = useAuthStore()
  await authStore.login(email, password)

  const destination = redirectTo || '/'
  router.push(destination)
}

/**
 * Perform logout and redirect to login page
 */
export async function logout(): Promise<void> {
  const authStore = useAuthStore()
  await authStore.logout()
  router.push('/login')
}

// ============================================================================
// Role Checking
// ============================================================================

/**
 * Check if user has required role
 */
export function hasRole(role: UserRole): boolean {
  try {
    const authStore = useAuthStore()
    const userRoles = authStore.user?.roles
    if (!userRoles) return false
    return userRoles.includes(role)
  } catch {
    return false
  }
}

/**
 * Check if user has any of the required roles
 */
export function hasAnyRole(roles: UserRole[]): boolean {
  try {
    const authStore = useAuthStore()
    const userRoles = authStore.user?.roles
    if (!userRoles) return false
    return roles.some((role) => userRoles.includes(role))
  } catch {
    return false
  }
}

/**
 * Check if user is authenticated
 */
export function isAuthenticated(): boolean {
  try {
    const authStore = useAuthStore()
    return authStore.isAuthenticated
  } catch {
    return false
  }
}

/**
 * Check if user is an admin
 */
export function isAdmin(): boolean {
  try {
    const authStore = useAuthStore()
    return authStore.isAdmin
  } catch {
    return false
  }
}

/**
 * Get the current user
 * Returns null if not authenticated
 */
export function getCurrentUser() {
  try {
    const authStore = useAuthStore()
    return authStore.user ?? null
  } catch {
    return null
  }
}

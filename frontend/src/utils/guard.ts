import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { useAuthStore } from '@stores/auth.store'

/**
 * Route guard for authentication and authorization
 */
export async function authGuard(
  to: RouteLocationNormalized,
  _from: RouteLocationNormalized,
  next: NavigationGuardNext
): Promise<void> {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth
  const requiredRoles = to.meta.roles as string[] | undefined
  const guestOnly = to.meta.guestOnly

  // Guest-only routes (login, register)
  if (guestOnly && authStore.isAuthenticated) {
    next({ path: '/' })
    return
  }

  // Public routes
  if (!requiresAuth) {
    next()
    return
  }

  // Auth required
  if (!authStore.isAuthenticated) {
    next({
      path: '/login',
      query: { redirect: to.fullPath },
    })
    return
  }

  // Role check
  if (requiredRoles && requiredRoles.length > 0) {
    const userRoles = authStore.user?.roles || []
    const hasRequiredRole = requiredRoles.some((role) => userRoles.includes(role))

    if (!hasRequiredRole) {
      next({ path: '/', replace: true })
      return
    }
  }

  next()
}

/**
 * Check if user has a specific role
 */
export function hasRole(role: string): boolean {
  const authStore = useAuthStore()
  return authStore.user?.roles?.includes(role) ?? false
}

/**
 * Check if user has any of the specified roles
 */
export function hasAnyRole(roles: string[]): boolean {
  const authStore = useAuthStore()
  const userRoles = authStore.user?.roles || []
  return roles.some((role) => userRoles.includes(role))
}

/**
 * Check if user has all of the specified roles
 */
export function hasAllRoles(roles: string[]): boolean {
  const authStore = useAuthStore()
  const userRoles = authStore.user?.roles || []
  return roles.every((role) => userRoles.includes(role))
}

/**
 * Check route meta for required roles
 */
export function checkRouteMeta(
  meta: Record<string, unknown>,
  userRoles: string[]
): boolean {
  const requiredRoles = meta.roles as string[] | undefined
  if (!requiredRoles || requiredRoles.length === 0) {
    return true
  }
  return requiredRoles.some((role) => userRoles.includes(role))
}


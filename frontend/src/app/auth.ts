import { useAuthStore } from '@stores/auth.store'
import { router } from './router'

/**
 * Initialize authentication state on app startup
 */
export async function initAuth(): Promise<void> {
  const authStore = useAuthStore()

  await authStore.initSession()
}

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

/**
 * Check if user has required role
 */
export function hasRole(role: string): boolean {
  const authStore = useAuthStore()
  return authStore.user?.roles?.includes(role) ?? false
}

/**
 * Check if user has any of the required roles
 */
export function hasAnyRole(roles: string[]): boolean {
  return roles.some((role) => hasRole(role))
}

/**
 * Check if user is authenticated
 */
export function isAuthenticated(): boolean {
  const authStore = useAuthStore()
  return authStore.isAuthenticated
}


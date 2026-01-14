import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@stores/auth.store'
import type { UserRole } from '@domain/user/user.types'

/**
 * Authentication composable for Vue components
 * Provides reactive auth state and actions
 */
export function useAuth() {
  const authStore = useAuthStore()
  const router = useRouter()

  // Reactive state
  const user = computed(() => authStore.user)
  const isLoggedIn = computed(() => authStore.isAuthenticated)
  const isAdmin = computed(() => authStore.isAdmin)
  const isModerator = computed(() => authStore.isModerator)
  const loading = computed(() => authStore.loading)
  const error = computed(() => authStore.error)
  const fieldErrors = computed(() => authStore.fieldErrors)
  const isSessionExpired = computed(() => authStore.isSessionExpired)

  /**
   * Login and redirect to destination
   */
  async function login(email: string, password: string, redirectTo?: string): Promise<void> {
    await authStore.login(email, password)
    const destination = redirectTo || '/'
    router.push(destination)
  }

  /**
   * Register new user
   */
  async function register(email: string, password: string, name: string, confirmPassword?: string): Promise<void> {
    await authStore.register(email, password, name, confirmPassword)
  }

  /**
   * Logout and redirect to login
   */
  async function logout(): Promise<void> {
    await authStore.logout()
    router.push('/login')
  }

  /**
   * Check if user has a specific role
   */
  function hasRole(role: UserRole): boolean {
    const userRoles = authStore.user?.roles
    if (!userRoles) return false
    return userRoles.includes(role)
  }

  /**
   * Check if user has any of the specified roles
   */
  function hasAnyRole(roles: UserRole[]): boolean {
    const userRoles = authStore.user?.roles
    if (!userRoles) return false
    return roles.some((role) => userRoles.includes(role))
  }

  /**
   * Get field-specific error
   */
  function getFieldError(field: string): string | null {
    return authStore.getFieldError(field)
  }

  /**
   * Clear all auth errors
   */
  function clearErrors(): void {
    authStore.clearErrors()
  }

  return {
    // State
    user,
    isLoggedIn,
    isAdmin,
    isModerator,
    loading,
    error,
    fieldErrors,
    isSessionExpired,
    // Actions
    login,
    register,
    logout,
    // Utilities
    hasRole,
    hasAnyRole,
    getFieldError,
    clearErrors,
  }
}

export default useAuth


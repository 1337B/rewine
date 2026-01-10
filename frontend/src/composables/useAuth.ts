import { computed } from 'vue'
import { useAuthStore } from '@stores/auth.store'
import { login as authLogin, logout as authLogout, hasRole, hasAnyRole, isAuthenticated } from '@app/auth'

/**
 * Authentication composable
 */
export function useAuth() {
  const authStore = useAuthStore()

  const user = computed(() => authStore.user)
  const isLoggedIn = computed(() => authStore.isAuthenticated)
  const isAdmin = computed(() => authStore.isAdmin)
  const loading = computed(() => authStore.loading)
  const error = computed(() => authStore.error)

  async function login(email: string, password: string, redirectTo?: string) {
    await authLogin(email, password, redirectTo)
  }

  async function register(email: string, password: string, name: string) {
    await authStore.register(email, password, name)
  }

  async function logout() {
    await authLogout()
  }

  function checkRole(role: string): boolean {
    return hasRole(role)
  }

  function checkAnyRole(roles: string[]): boolean {
    return hasAnyRole(roles)
  }

  function checkAuthenticated(): boolean {
    return isAuthenticated()
  }

  return {
    user,
    isLoggedIn,
    isAdmin,
    loading,
    error,
    login,
    register,
    logout,
    hasRole: checkRole,
    hasAnyRole: checkAnyRole,
    isAuthenticated: checkAuthenticated,
  }
}

export default useAuth


import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService, AuthValidationError, AuthApiError } from '@services/auth.service'
import type { User } from '@domain/user/user.types'
import { STORAGE_KEYS } from '@config/constants'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const accessToken = ref<string | null>(null)
  const refreshTokenValue = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const fieldErrors = ref<Record<string, string[]>>({})
  const isSessionExpired = ref(false)

  // Getters
  const isAuthenticated = computed(() => !!accessToken.value && !!user.value)
  const isAdmin = computed(() => user.value?.roles?.includes('ROLE_ADMIN') ?? false)
  const isModerator = computed(() => user.value?.roles?.includes('ROLE_MODERATOR') ?? false)
  const hasFieldErrors = computed(() => Object.keys(fieldErrors.value).length > 0)

  /**
   * Get field error for a specific field
   */
  function getFieldError(field: string): string | null {
    const errors = fieldErrors.value[field]
    return errors && errors.length > 0 ? errors[0] : null
  }

  /**
   * Clear all errors
   */
  function clearErrors() {
    error.value = null
    fieldErrors.value = {}
    isSessionExpired.value = false
  }

  /**
   * Handle auth errors consistently
   */
  function handleAuthError(err: unknown): void {
    if (err instanceof AuthValidationError) {
      error.value = err.message
      fieldErrors.value = err.fieldErrors
    } else if (err instanceof AuthApiError) {
      error.value = err.message
      isSessionExpired.value = err.isSessionExpired
      if (err.isSessionExpired) {
        clearSession()
      }
    } else if (err instanceof Error) {
      error.value = err.message
    } else {
      error.value = 'An unexpected error occurred'
    }
  }

  // Actions
  async function initSession(): Promise<void> {
    const storedToken = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN)
    const storedRefresh = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN)
    const storedUser = localStorage.getItem(STORAGE_KEYS.USER)

    if (storedToken && storedRefresh && storedUser) {
      accessToken.value = storedToken
      refreshTokenValue.value = storedRefresh
      try {
        user.value = JSON.parse(storedUser)
      } catch {
        clearSession()
        return
      }

      try {
        // Verify token is still valid
        const currentUser = await authService.getCurrentUser()
        user.value = currentUser
        localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(currentUser))
      } catch (err) {
        if (err instanceof AuthApiError && err.isSessionExpired) {
          // Try to refresh
          try {
            await refreshToken()
          } catch {
            clearSession()
            isSessionExpired.value = true
          }
        } else {
          // Network error - keep existing session
          console.warn('[Auth] Could not verify session:', err)
        }
      }
    }
  }

  async function login(email: string, password: string): Promise<void> {
    loading.value = true
    clearErrors()

    try {
      const result = await authService.login(email, password)
      setSession(result.user, result.tokens.accessToken, result.tokens.refreshToken)
    } catch (err: unknown) {
      handleAuthError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function register(username: string, email: string, password: string, name?: string): Promise<void> {
    loading.value = true
    clearErrors()

    try {
      const result = await authService.register(username, email, password, name)
      setSession(result.user, result.tokens.accessToken, result.tokens.refreshToken)
    } catch (err: unknown) {
      handleAuthError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function refreshToken(): Promise<void> {
    if (!refreshTokenValue.value) {
      throw new AuthApiError('No refresh token available', 401)
    }

    try {
      const result = await authService.refreshToken(refreshTokenValue.value)
      accessToken.value = result.accessToken
      localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, result.accessToken)
    } catch (err) {
      handleAuthError(err)
      throw err
    }
  }

  async function logout(): Promise<void> {
    try {
      await authService.logout()
    } catch {
      // Ignore logout errors
    } finally {
      clearSession()
    }
  }

  function setSession(newUser: User, newAccessToken: string, newRefreshToken: string): void {
    user.value = newUser
    accessToken.value = newAccessToken
    refreshTokenValue.value = newRefreshToken
    isSessionExpired.value = false

    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, newAccessToken)
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, newRefreshToken)
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(newUser))
  }

  function clearSession(): void {
    user.value = null
    accessToken.value = null
    refreshTokenValue.value = null

    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN)
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN)
    localStorage.removeItem(STORAGE_KEYS.USER)
  }

  function updateUser(updatedUser: Partial<User>): void {
    if (user.value) {
      user.value = { ...user.value, ...updatedUser }
      localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user.value))
    }
  }

  return {
    // State
    user,
    accessToken,
    loading,
    error,
    fieldErrors,
    isSessionExpired,
    // Getters
    isAuthenticated,
    isAdmin,
    isModerator,
    hasFieldErrors,
    // Methods
    getFieldError,
    clearErrors,
    // Actions
    initSession,
    login,
    register,
    refreshToken,
    logout,
    updateUser,
    clearSession,
  }
})

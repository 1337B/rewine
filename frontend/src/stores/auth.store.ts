import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '@services/auth.service'
import type { User } from '@domain/user/user.types'
import { STORAGE_KEYS } from '@config/constants'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const accessToken = ref<string | null>(null)
  const refreshTokenValue = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!accessToken.value && !!user.value)
  const isAdmin = computed(() => user.value?.roles?.includes('admin') ?? false)

  // Actions
  async function initSession() {
    const storedToken = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN)
    const storedRefresh = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN)
    const storedUser = localStorage.getItem(STORAGE_KEYS.USER)

    if (storedToken && storedRefresh && storedUser) {
      accessToken.value = storedToken
      refreshTokenValue.value = storedRefresh
      user.value = JSON.parse(storedUser)

      try {
        // Verify token is still valid
        const currentUser = await authService.getCurrentUser()
        user.value = currentUser
        localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(currentUser))
      } catch {
        // Token invalid, try to refresh
        try {
          await refreshToken()
        } catch {
          clearSession()
        }
      }
    }
  }

  async function login(email: string, password: string) {
    loading.value = true
    error.value = null

    try {
      const result = await authService.login(email, password)
      setSession(result.user, result.tokens.accessToken, result.tokens.refreshToken)
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Login failed'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function register(email: string, password: string, name: string) {
    loading.value = true
    error.value = null

    try {
      const result = await authService.register(email, password, name)
      setSession(result.user, result.tokens.accessToken, result.tokens.refreshToken)
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Registration failed'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function refreshToken() {
    if (!refreshTokenValue.value) {
      throw new Error('No refresh token available')
    }

    const result = await authService.refreshToken(refreshTokenValue.value)
    accessToken.value = result.accessToken
    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, result.accessToken)
  }

  async function logout() {
    try {
      await authService.logout()
    } catch {
      // Ignore logout errors
    } finally {
      clearSession()
    }
  }

  function setSession(newUser: User, newAccessToken: string, newRefreshToken: string) {
    user.value = newUser
    accessToken.value = newAccessToken
    refreshTokenValue.value = newRefreshToken

    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, newAccessToken)
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, newRefreshToken)
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(newUser))
  }

  function clearSession() {
    user.value = null
    accessToken.value = null
    refreshTokenValue.value = null

    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN)
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN)
    localStorage.removeItem(STORAGE_KEYS.USER)
  }

  function updateUser(updatedUser: Partial<User>) {
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
    // Getters
    isAuthenticated,
    isAdmin,
    // Actions
    initSession,
    login,
    register,
    refreshToken,
    logout,
    updateUser,
  }
})


import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { STORAGE_KEYS } from '@config/constants'

export interface Toast {
  id: string
  type: 'success' | 'error' | 'warning' | 'info'
  title?: string
  message: string
  duration?: number
}

export type Theme = 'light' | 'dark' | 'system'

export const useUiStore = defineStore('ui', () => {
  // State
  const theme = ref<Theme>('system')
  const sidebarOpen = ref(false)
  const toasts = ref<Toast[]>([])
  const globalLoading = ref(false)
  const loadingMessage = ref('')

  // Getters
  const effectiveTheme = computed(() => {
    if (theme.value === 'system') {
      return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
    }
    return theme.value
  })

  const isDarkMode = computed(() => effectiveTheme.value === 'dark')

  // Actions
  function setTheme(newTheme: Theme) {
    theme.value = newTheme
    localStorage.setItem(STORAGE_KEYS.THEME, newTheme)
    applyTheme()
  }

  function applyTheme() {
    const root = document.documentElement
    if (effectiveTheme.value === 'dark') {
      root.classList.add('dark')
    } else {
      root.classList.remove('dark')
    }
  }

  function initTheme() {
    const storedTheme = localStorage.getItem(STORAGE_KEYS.THEME) as Theme | null
    if (storedTheme) {
      theme.value = storedTheme
    }
    applyTheme()
  }

  function toggleSidebar() {
    sidebarOpen.value = !sidebarOpen.value
  }

  function openSidebar() {
    sidebarOpen.value = true
  }

  function closeSidebar() {
    sidebarOpen.value = false
  }

  function addToast(toast: Omit<Toast, 'id'>) {
    const id = Math.random().toString(36).slice(2, 9)
    const newToast: Toast = {
      ...toast,
      id,
      duration: toast.duration ?? 5000,
    }

    toasts.value.push(newToast)

    // Auto-remove after duration
    if (newToast.duration > 0) {
      setTimeout(() => {
        removeToast(id)
      }, newToast.duration)
    }

    return id
  }

  function removeToast(id: string) {
    const index = toasts.value.findIndex((t) => t.id === id)
    if (index !== -1) {
      toasts.value.splice(index, 1)
    }
  }

  function clearToasts() {
    toasts.value = []
  }

  function showSuccess(message: string, title?: string) {
    return addToast({ type: 'success', message, title })
  }

  function showError(message: string, title?: string) {
    return addToast({ type: 'error', message, title })
  }

  function showWarning(message: string, title?: string) {
    return addToast({ type: 'warning', message, title })
  }

  function showInfo(message: string, title?: string) {
    return addToast({ type: 'info', message, title })
  }

  function setGlobalLoading(loading: boolean, message = '') {
    globalLoading.value = loading
    loadingMessage.value = message
  }

  return {
    // State
    theme,
    sidebarOpen,
    toasts,
    globalLoading,
    loadingMessage,
    // Getters
    effectiveTheme,
    isDarkMode,
    // Actions
    setTheme,
    initTheme,
    toggleSidebar,
    openSidebar,
    closeSidebar,
    addToast,
    removeToast,
    clearToasts,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    setGlobalLoading,
  }
})


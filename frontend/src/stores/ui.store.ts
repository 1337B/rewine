import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { STORAGE_KEYS } from '@config/constants'

// ============================================================================
// Types
// ============================================================================

export interface Toast {
  id: string
  type: 'success' | 'error' | 'warning' | 'info'
  title?: string
  message: string
  duration?: number
}

export type Theme = 'light' | 'dark' | 'system'

export interface ConfirmOptions {
  /** Dialog title */
  title?: string
  /** i18n key for title */
  titleKey?: string
  /** Dialog message */
  message?: string
  /** i18n key for message */
  messageKey?: string
  /** Confirm button text */
  confirmText?: string
  /** i18n key for confirm button */
  confirmTextKey?: string
  /** Cancel button text */
  cancelText?: string
  /** i18n key for cancel button */
  cancelTextKey?: string
  /** Confirm button variant */
  confirmVariant?: 'primary' | 'danger'
  /** Callback when confirmed */
  onConfirm?: () => void
  /** Callback when cancelled */
  onCancel?: () => void
}

export interface ConfirmState {
  isOpen: boolean
  options: ConfirmOptions
  loading: boolean
}

// ============================================================================
// Store
// ============================================================================

export const useUiStore = defineStore('ui', () => {
  // ============================================================================
  // State
  // ============================================================================

  // Theme
  const theme = ref<Theme>('system')

  // Sidebar
  const sidebarOpen = ref(false)

  // Toasts
  const toasts = ref<Toast[]>([])

  // Confirm Dialog
  const confirmState = ref<ConfirmState>({
    isOpen: false,
    options: {},
    loading: false,
  })

  // Global Loading
  const globalLoading = ref(false)
  const loadingMessage = ref('')

  // ============================================================================
  // Getters
  // ============================================================================

  const effectiveTheme = computed(() => {
    if (theme.value === 'system') {
      return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
    }
    return theme.value
  })

  const isDarkMode = computed(() => effectiveTheme.value === 'dark')

  // ============================================================================
  // Theme Actions
  // ============================================================================

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

  // ============================================================================
  // Sidebar Actions
  // ============================================================================

  function toggleSidebar() {
    sidebarOpen.value = !sidebarOpen.value
  }

  function openSidebar() {
    sidebarOpen.value = true
  }

  function closeSidebar() {
    sidebarOpen.value = false
  }

  // ============================================================================
  // Toast Actions
  // ============================================================================

  function addToast(toast: Omit<Toast, 'id'>): string {
    const id = Math.random().toString(36).slice(2, 9)
    const newToast: Toast = {
      ...toast,
      id,
      duration: toast.duration ?? 5000,
    }

    toasts.value.push(newToast)

    // Auto-remove after duration
    if (newToast.duration && newToast.duration > 0) {
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

  function showSuccess(message: string, title?: string): string {
    return addToast({ type: 'success', message, title })
  }

  function showError(message: string, title?: string): string {
    return addToast({ type: 'error', message, title })
  }

  function showWarning(message: string, title?: string): string {
    return addToast({ type: 'warning', message, title })
  }

  function showInfo(message: string, title?: string): string {
    return addToast({ type: 'info', message, title })
  }

  // ============================================================================
  // Confirm Dialog Actions
  // ============================================================================

  function openConfirm(options: ConfirmOptions) {
    confirmState.value = {
      isOpen: true,
      options,
      loading: false,
    }
  }

  function closeConfirm() {
    confirmState.value = {
      isOpen: false,
      options: {},
      loading: false,
    }
  }

  function setConfirmLoading(loading: boolean) {
    confirmState.value.loading = loading
  }

  async function handleConfirm() {
    const { onConfirm } = confirmState.value.options

    if (onConfirm) {
      setConfirmLoading(true)
      try {
        await onConfirm()
      } finally {
        setConfirmLoading(false)
      }
    }

    closeConfirm()
  }

  function handleCancel() {
    const { onCancel } = confirmState.value.options

    if (onCancel) {
      onCancel()
    }

    closeConfirm()
  }

  // ============================================================================
  // Global Loading Actions
  // ============================================================================

  function setGlobalLoading(loading: boolean, message = '') {
    globalLoading.value = loading
    loadingMessage.value = message
  }

  // ============================================================================
  // Return
  // ============================================================================

  return {
    // State
    theme,
    sidebarOpen,
    toasts,
    confirmState,
    globalLoading,
    loadingMessage,
    // Getters
    effectiveTheme,
    isDarkMode,
    // Theme Actions
    setTheme,
    initTheme,
    // Sidebar Actions
    toggleSidebar,
    openSidebar,
    closeSidebar,
    // Toast Actions
    addToast,
    removeToast,
    clearToasts,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    // Confirm Actions
    openConfirm,
    closeConfirm,
    setConfirmLoading,
    handleConfirm,
    handleCancel,
    // Global Loading
    setGlobalLoading,
  }
})

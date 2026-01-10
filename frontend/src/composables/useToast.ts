import { useUiStore } from '@stores/ui.store'
import type { Toast } from '@stores/ui.store'

/**
 * Toast notifications composable
 */
export function useToast() {
  const uiStore = useUiStore()

  function show(options: Omit<Toast, 'id'>) {
    return uiStore.addToast(options)
  }

  function success(message: string, title?: string) {
    return uiStore.showSuccess(message, title)
  }

  function error(message: string, title?: string) {
    return uiStore.showError(message, title)
  }

  function warning(message: string, title?: string) {
    return uiStore.showWarning(message, title)
  }

  function info(message: string, title?: string) {
    return uiStore.showInfo(message, title)
  }

  function remove(id: string) {
    uiStore.removeToast(id)
  }

  function clear() {
    uiStore.clearToasts()
  }

  return {
    show,
    success,
    error,
    warning,
    info,
    remove,
    clear,
  }
}

export default useToast


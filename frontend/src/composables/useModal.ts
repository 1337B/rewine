import { ref } from 'vue'

/**
 * Modal composable
 */
export function useModal() {
  const isOpen = ref(false)
  const data = ref<unknown>(null)

  function open(modalData?: unknown) {
    data.value = modalData ?? null
    isOpen.value = true
  }

  function close() {
    isOpen.value = false
    data.value = null
  }

  function toggle() {
    if (isOpen.value) {
      close()
    } else {
      open()
    }
  }

  return {
    isOpen,
    data,
    open,
    close,
    toggle,
  }
}

/**
 * Confirmation modal composable
 */
export function useConfirmModal() {
  const isOpen = ref(false)
  const title = ref('Confirm')
  const message = ref('Are you sure?')
  const confirmText = ref('Confirm')
  const cancelText = ref('Cancel')
  const loading = ref(false)

  let resolvePromise: ((value: boolean) => void) | null = null

  function confirm(options?: {
    title?: string
    message?: string
    confirmText?: string
    cancelText?: string
  }): Promise<boolean> {
    title.value = options?.title ?? 'Confirm'
    message.value = options?.message ?? 'Are you sure?'
    confirmText.value = options?.confirmText ?? 'Confirm'
    cancelText.value = options?.cancelText ?? 'Cancel'
    isOpen.value = true

    return new Promise((resolve) => {
      resolvePromise = resolve
    })
  }

  function handleConfirm() {
    isOpen.value = false
    resolvePromise?.(true)
    resolvePromise = null
  }

  function handleCancel() {
    isOpen.value = false
    resolvePromise?.(false)
    resolvePromise = null
  }

  function setLoading(value: boolean) {
    loading.value = value
  }

  return {
    isOpen,
    title,
    message,
    confirmText,
    cancelText,
    loading,
    confirm,
    handleConfirm,
    handleCancel,
    setLoading,
  }
}

export default useModal


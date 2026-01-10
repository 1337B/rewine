import { ref, watch } from 'vue'
import type { Ref } from 'vue'

/**
 * Debounce composable
 */
export function useDebounce<T>(value: Ref<T>, delay = 300): Ref<T> {
  const debouncedValue = ref(value.value) as Ref<T>
  let timeout: ReturnType<typeof setTimeout> | null = null

  watch(value, (newValue) => {
    if (timeout) {
      clearTimeout(timeout)
    }

    timeout = setTimeout(() => {
      debouncedValue.value = newValue
    }, delay)
  })

  return debouncedValue
}

/**
 * Debounce function composable
 */
export function useDebounceFn<T extends (...args: unknown[]) => unknown>(
  fn: T,
  delay = 300
): { debounced: (...args: Parameters<T>) => void; cancel: () => void } {
  let timeout: ReturnType<typeof setTimeout> | null = null

  function debounced(...args: Parameters<T>) {
    if (timeout) {
      clearTimeout(timeout)
    }

    timeout = setTimeout(() => {
      fn(...args)
    }, delay)
  }

  function cancel() {
    if (timeout) {
      clearTimeout(timeout)
      timeout = null
    }
  }

  return { debounced, cancel }
}

export default useDebounce


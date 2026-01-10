import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { LocationQuery } from 'vue-router'

export interface QueryStateOptions<T> {
  defaultValue: T
  transform?: {
    parse: (value: string) => T
    stringify: (value: T) => string
  }
}

/**
 * Sync state with URL query parameters
 */
export function useQueryState<T>(
  key: string,
  options: QueryStateOptions<T>
) {
  const route = useRoute()
  const router = useRouter()

  const { defaultValue, transform } = options

  // Parse value from query
  function parseFromQuery(query: LocationQuery): T {
    const value = query[key]
    if (value === undefined || value === null) {
      return defaultValue
    }

    const stringValue = Array.isArray(value) ? value[0] : value
    if (stringValue === null) {
      return defaultValue
    }

    if (transform?.parse) {
      try {
        return transform.parse(stringValue)
      } catch {
        return defaultValue
      }
    }

    return stringValue as unknown as T
  }

  // Initialize state from current query
  const state = ref<T>(parseFromQuery(route.query))

  // Computed to check if state differs from default
  const isDirty = computed(() =>
    JSON.stringify(state.value) !== JSON.stringify(defaultValue)
  )

  // Watch for route query changes
  watch(
    () => route.query,
    (query) => {
      const newValue = parseFromQuery(query)
      if (JSON.stringify(state.value) !== JSON.stringify(newValue)) {
        state.value = newValue
      }
    }
  )

  // Update URL when state changes
  watch(
    state,
    (newValue) => {
      const currentQuery = { ...route.query }

      if (JSON.stringify(newValue) === JSON.stringify(defaultValue)) {
        // Remove from query if default value
        delete currentQuery[key]
      } else {
        // Set in query
        const stringValue = transform?.stringify
          ? transform.stringify(newValue)
          : String(newValue)
        currentQuery[key] = stringValue
      }

      router.replace({ query: currentQuery })
    },
    { deep: true }
  )

  function reset() {
    state.value = defaultValue
  }

  return {
    state,
    isDirty,
    reset,
  }
}

/**
 * Sync multiple values with URL query parameters
 */
export function useQueryParams<T extends Record<string, unknown>>(
  defaults: T,
  transforms?: Partial<{
    [K in keyof T]: {
      parse: (value: string) => T[K]
      stringify: (value: T[K]) => string
    }
  }>
) {
  const route = useRoute()
  const router = useRouter()

  function parseFromQuery(query: LocationQuery): T {
    const result = { ...defaults }

    for (const key of Object.keys(defaults)) {
      const value = query[key]
      if (value !== undefined && value !== null) {
        const stringValue = Array.isArray(value) ? value[0] : value
        if (stringValue !== null) {
          const transform = transforms?.[key as keyof T]
          if (transform?.parse) {
            try {
              (result as Record<string, unknown>)[key] = transform.parse(stringValue)
            } catch {
              // Keep default
            }
          } else {
            (result as Record<string, unknown>)[key] = stringValue
          }
        }
      }
    }

    return result
  }

  const params = ref<T>(parseFromQuery(route.query))

  watch(
    () => route.query,
    (query) => {
      params.value = parseFromQuery(query)
    }
  )

  function setParams(newParams: Partial<T>) {
    params.value = { ...params.value, ...newParams }
    updateQuery()
  }

  function updateQuery() {
    const query: LocationQuery = {}

    for (const [key, value] of Object.entries(params.value)) {
      if (value !== defaults[key as keyof T]) {
        const transform = transforms?.[key as keyof T]
        query[key] = transform?.stringify
          ? transform.stringify(value as T[keyof T])
          : String(value)
      }
    }

    router.replace({ query })
  }

  function reset() {
    params.value = { ...defaults }
    router.replace({ query: {} })
  }

  return {
    params,
    setParams,
    reset,
  }
}

export default useQueryState


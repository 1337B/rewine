import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { winesService } from '@services/wines.service'
import type { Wine, WineSummary, WineFilter, WineComparison, AiWineProfile } from '@domain/wine/wine.types'
import type { PaginationMeta } from '@api/api.types'
import { isNullish, nonNull } from '@utils/object'

// ============================================================================
// Cache Key Helpers
// ============================================================================

/**
 * Generate comparison key for multiple wines
 * Always sorts IDs to ensure A-B and B-A use the same cache key
 */
function getMultiComparisonKey(wineIds: string[]): string {
  return [...wineIds].sort().join('-')
}

export const useWinesStore = defineStore('wines', () => {
  // ============================================================================
  // State
  // ============================================================================

  const wines = ref<WineSummary[]>([])
  const currentWine = ref<Wine | null>(null)
  const pagination = ref<PaginationMeta | null>(null)
  const filter = ref<WineFilter>({})
  const loading = ref(false)
  const error = ref<string | null>(null)
  const comparisonWines = ref<Wine[]>([])

  // Cache stores
  const comparisonsByKey = ref<Map<string, WineComparison & { cachedAt: Date }>>(new Map())
  const aiProfilesByWineId = ref<Map<string, AiWineProfile & { cachedAt: Date }>>(new Map())

  // Loading states for async operations
  const comparingLoading = ref(false)
  const aiProfileLoading = ref(false)

  // ============================================================================
  // Getters
  // ============================================================================

  const hasWines = computed(() => wines.value.length > 0)
  const hasNextPage = computed(() => pagination.value?.hasNext ?? false)
  const hasPreviousPage = computed(() => pagination.value?.hasPrevious ?? false)
  const totalWines = computed(() => pagination.value?.totalItems ?? 0)

  /**
   * Get cached comparison by wine IDs (null-safe)
   */
  function getCachedComparison(wineIds: string[]): (WineComparison & { cachedAt: Date }) | null {
    if (isNullish(wineIds) || wineIds.length < 2) return null
    const key = getMultiComparisonKey(wineIds)
    return comparisonsByKey.value.get(key) ?? null
  }

  /**
   * Check if comparison exists in cache
   */
  function hasComparison(wineIds: string[]): boolean {
    return nonNull(getCachedComparison(wineIds))
  }

  /**
   * Get cached AI profile by wine ID (null-safe)
   */
  function getCachedAiProfile(wineId: string | null | undefined): (AiWineProfile & { cachedAt: Date }) | null {
    if (isNullish(wineId)) return null
    return aiProfilesByWineId.value.get(wineId) ?? null
  }

  /**
   * Check if AI profile exists in cache
   */
  function hasAiProfile(wineId: string | null | undefined): boolean {
    return nonNull(getCachedAiProfile(wineId))
  }

  // ============================================================================
  // Actions - Wine Fetching
  // ============================================================================

  async function fetchWines(page = 1, pageSize = 20) {
    loading.value = true
    error.value = null

    try {
      const result = await winesService.getWines(filter.value, page, pageSize)
      wines.value = result.wines
      pagination.value = result.pagination
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch wines'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchWine(id: string) {
    loading.value = true
    error.value = null

    try {
      currentWine.value = await winesService.getWine(id)
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch wine'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function searchWines(query: string, limit = 10): Promise<WineSummary[]> {
    try {
      const result = await winesService.getWines({ search: query }, 1, limit)
      return result.wines
    } catch {
      return []
    }
  }

  // ============================================================================
  // Actions - Comparison
  // ============================================================================

  function setFilter(newFilter: WineFilter) {
    filter.value = newFilter
  }

  function clearFilter() {
    filter.value = {}
  }

  function addToComparison(wine: Wine) {
    if (comparisonWines.value.length < 4 && !comparisonWines.value.find((w) => w.id === wine.id)) {
      comparisonWines.value.push(wine)
    }
  }

  function removeFromComparison(wineId: string) {
    const index = comparisonWines.value.findIndex((w) => w.id === wineId)
    if (index !== -1) {
      comparisonWines.value.splice(index, 1)
    }
  }

  function clearComparison() {
    comparisonWines.value = []
  }

  /**
   * Compare wines with caching
   * Returns cached result if available, otherwise fetches from API
   */
  async function compareWines(wineIds: string[]): Promise<WineComparison & { fromCache: boolean }> {
    if (wineIds.length < 2) {
      throw new Error('Need at least 2 wines to compare')
    }

    const key = getMultiComparisonKey(wineIds)
    const cached = comparisonsByKey.value.get(key)

    if (cached) {
      return { ...cached, fromCache: true }
    }

    comparingLoading.value = true
    error.value = null

    try {
      const result = await winesService.compareWines(wineIds)
      const cachedResult = { ...result, cachedAt: new Date() }
      comparisonsByKey.value.set(key, cachedResult)
      return { ...cachedResult, fromCache: false }
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to compare wines'
      throw err
    } finally {
      comparingLoading.value = false
    }
  }

  // ============================================================================
  // Actions - AI Profile
  // ============================================================================

  /**
   * Get AI profile with caching
   * Returns cached result if available, otherwise fetches from API
   */
  async function getAiProfile(wineId: string): Promise<AiWineProfile & { fromCache: boolean }> {
    if (isNullish(wineId)) {
      throw new Error('Wine ID is required')
    }

    const cached = aiProfilesByWineId.value.get(wineId)

    if (cached) {
      return { ...cached, fromCache: true }
    }

    aiProfileLoading.value = true
    error.value = null

    try {
      const result = await winesService.getAiProfile(wineId)
      const cachedResult = { ...result, cachedAt: new Date() }
      aiProfilesByWineId.value.set(wineId, cachedResult)
      return { ...cachedResult, fromCache: false }
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to get AI profile'
      throw err
    } finally {
      aiProfileLoading.value = false
    }
  }

  // ============================================================================
  // Actions - Cache Management
  // ============================================================================

  /**
   * Clear specific comparison from cache
   */
  function clearComparisonCache(wineIds: string[]) {
    const key = getMultiComparisonKey(wineIds)
    comparisonsByKey.value.delete(key)
  }

  /**
   * Clear specific AI profile from cache
   */
  function clearAiProfileCache(wineId: string) {
    aiProfilesByWineId.value.delete(wineId)
  }

  /**
   * Clear all caches
   */
  function clearAllCaches() {
    comparisonsByKey.value.clear()
    aiProfilesByWineId.value.clear()
  }

  function reset() {
    wines.value = []
    currentWine.value = null
    pagination.value = null
    filter.value = {}
    error.value = null
    comparisonWines.value = []
    clearAllCaches()
  }

  return {
    // State
    wines,
    currentWine,
    pagination,
    filter,
    loading,
    error,
    comparisonWines,
    comparingLoading,
    aiProfileLoading,
    // Caches (exposed for debugging/inspection)
    comparisonsByKey,
    aiProfilesByWineId,
    // Getters
    hasWines,
    hasNextPage,
    hasPreviousPage,
    totalWines,
    // Cache getters
    getCachedComparison,
    hasComparison,
    getCachedAiProfile,
    hasAiProfile,
    // Actions
    fetchWines,
    fetchWine,
    searchWines,
    setFilter,
    clearFilter,
    addToComparison,
    removeFromComparison,
    clearComparison,
    compareWines,
    getAiProfile,
    clearComparisonCache,
    clearAiProfileCache,
    clearAllCaches,
    reset,
  }
})


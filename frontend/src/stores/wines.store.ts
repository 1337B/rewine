import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { winesService } from '@services/wines.service'
import type { Wine, WineFilter } from '@domain/wine/wine.types'
import type { PaginationMeta } from '@api/api.types'

export const useWinesStore = defineStore('wines', () => {
  // State
  const wines = ref<Wine[]>([])
  const currentWine = ref<Wine | null>(null)
  const pagination = ref<PaginationMeta | null>(null)
  const filter = ref<WineFilter>({})
  const loading = ref(false)
  const error = ref<string | null>(null)
  const comparisonWines = ref<Wine[]>([])

  // Getters
  const hasWines = computed(() => wines.value.length > 0)
  const hasNextPage = computed(() => pagination.value?.hasNext ?? false)
  const hasPreviousPage = computed(() => pagination.value?.hasPrevious ?? false)
  const totalWines = computed(() => pagination.value?.totalItems ?? 0)

  // Actions
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

  function reset() {
    wines.value = []
    currentWine.value = null
    pagination.value = null
    filter.value = {}
    error.value = null
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
    // Getters
    hasWines,
    hasNextPage,
    hasPreviousPage,
    totalWines,
    // Actions
    fetchWines,
    fetchWine,
    setFilter,
    clearFilter,
    addToComparison,
    removeFromComparison,
    clearComparison,
    reset,
  }
})


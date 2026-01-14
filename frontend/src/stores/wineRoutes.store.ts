import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { wineRoutesService } from '@services/wineRoutes.service'
import type { WineRoute, WineRouteFilter } from '@domain/route/route.types'
import type { PaginationMeta } from '@api/api.types'

export const useWineRoutesStore = defineStore('wineRoutes', () => {
  // State
  const routes = ref<WineRoute[]>([])
  const currentRoute = ref<WineRoute | null>(null)
  const pagination = ref<PaginationMeta | null>(null)
  const filter = ref<WineRouteFilter>({})
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const hasRoutes = computed(() => routes.value.length > 0)
  const publishedRoutes = computed(() => routes.value.filter((r) => r.isPublished))
  const topRatedRoutes = computed(() =>
    [...routes.value].filter((r) => r.rating !== null).sort((a, b) => (b.rating ?? 0) - (a.rating ?? 0)).slice(0, 5)
  )

  // Actions
  async function fetchRoutes(page = 1, pageSize = 20) {
    loading.value = true
    error.value = null

    try {
      const result = await wineRoutesService.getWineRoutes(filter.value, page, pageSize)
      routes.value = result.routes
      pagination.value = result.pagination
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch wine routes'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchRoute(id: string) {
    loading.value = true
    error.value = null

    try {
      currentRoute.value = await wineRoutesService.getWineRoute(id)
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch wine route'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createRoute(route: Parameters<typeof wineRoutesService.createWineRoute>[0]) {
    loading.value = true
    error.value = null

    try {
      const newRoute = await wineRoutesService.createWineRoute(route)
      routes.value.unshift(newRoute)
      return newRoute
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to create wine route'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateRoute(id: string, updates: Partial<WineRoute>) {
    loading.value = true
    error.value = null

    try {
      const updatedRoute = await wineRoutesService.updateWineRoute(id, updates)
      const index = routes.value.findIndex((r) => r.id === id)
      if (index !== -1) {
        routes.value[index] = updatedRoute
      }
      if (currentRoute.value?.id === id) {
        currentRoute.value = updatedRoute
      }
      return updatedRoute
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to update wine route'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteRoute(id: string) {
    loading.value = true
    error.value = null

    try {
      await wineRoutesService.deleteWineRoute(id)
      routes.value = routes.value.filter((r) => r.id !== id)
      if (currentRoute.value?.id === id) {
        currentRoute.value = null
      }
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to delete wine route'
      throw err
    } finally {
      loading.value = false
    }
  }

  function setFilter(newFilter: WineRouteFilter) {
    filter.value = newFilter
  }

  function clearFilter() {
    filter.value = {}
  }

  function reset() {
    routes.value = []
    currentRoute.value = null
    pagination.value = null
    filter.value = {}
    error.value = null
  }

  return {
    // State
    routes,
    currentRoute,
    pagination,
    filter,
    loading,
    error,
    // Getters
    hasRoutes,
    publishedRoutes,
    topRatedRoutes,
    // Actions
    fetchRoutes,
    fetchRoute,
    createRoute,
    updateRoute,
    deleteRoute,
    setFilter,
    clearFilter,
    reset,
  }
})


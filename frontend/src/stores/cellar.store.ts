import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Wine } from '@domain/wine/wine.types'

export interface CellarWine {
  id: string
  wine: Wine
  quantity: number
  purchaseDate: Date | null
  purchasePrice: number | null
  location: string | null
  notes: string | null
  addedAt: Date
}

export const useCellarStore = defineStore('cellar', () => {
  // State
  const cellarWines = ref<CellarWine[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const totalBottles = computed(() =>
    cellarWines.value.reduce((sum, cw) => sum + cw.quantity, 0)
  )

  const totalValue = computed(() =>
    cellarWines.value.reduce((sum, cw) => {
      const price = cw.purchasePrice ?? cw.wine.price ?? 0
      return sum + price * cw.quantity
    }, 0)
  )

  const winesByType = computed(() => {
    const grouped: Record<string, CellarWine[]> = {}
    for (const cw of cellarWines.value) {
      const type = cw.wine.type
      if (!grouped[type]) {
        grouped[type] = []
      }
      grouped[type].push(cw)
    }
    return grouped
  })

  const winesByRegion = computed(() => {
    const grouped: Record<string, CellarWine[]> = {}
    for (const cw of cellarWines.value) {
      const region = cw.wine.region
      if (!grouped[region]) {
        grouped[region] = []
      }
      grouped[region].push(cw)
    }
    return grouped
  })

  // Actions
  async function fetchCellar() {
    loading.value = true
    error.value = null

    try {
      // TODO: Implement API call
      // const response = await cellarService.getCellar()
      // cellarWines.value = response
    } catch (err: unknown) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch cellar'
      throw err
    } finally {
      loading.value = false
    }
  }

  function addWine(wine: Wine, quantity = 1, options?: Partial<Omit<CellarWine, 'id' | 'wine' | 'addedAt'>>) {
    const existing = cellarWines.value.find((cw) => cw.wine.id === wine.id)

    if (existing) {
      existing.quantity += quantity
    } else {
      const newCellarWine: CellarWine = {
        id: Math.random().toString(36).slice(2, 9),
        wine,
        quantity,
        purchaseDate: options?.purchaseDate ?? null,
        purchasePrice: options?.purchasePrice ?? null,
        location: options?.location ?? null,
        notes: options?.notes ?? null,
        addedAt: new Date(),
      }
      cellarWines.value.push(newCellarWine)
    }
  }

  function removeWine(cellarWineId: string, quantity = 1) {
    const index = cellarWines.value.findIndex((cw) => cw.id === cellarWineId)
    if (index !== -1) {
      cellarWines.value[index].quantity -= quantity
      if (cellarWines.value[index].quantity <= 0) {
        cellarWines.value.splice(index, 1)
      }
    }
  }

  function updateWine(cellarWineId: string, updates: Partial<Omit<CellarWine, 'id' | 'wine' | 'addedAt'>>) {
    const cellarWine = cellarWines.value.find((cw) => cw.id === cellarWineId)
    if (cellarWine) {
      Object.assign(cellarWine, updates)
    }
  }

  function clearCellar() {
    cellarWines.value = []
  }

  function reset() {
    cellarWines.value = []
    error.value = null
  }

  return {
    // State
    cellarWines,
    loading,
    error,
    // Getters
    totalBottles,
    totalValue,
    winesByType,
    winesByRegion,
    // Actions
    fetchCellar,
    addWine,
    removeWine,
    updateWine,
    clearCellar,
    reset,
  }
})


<script setup lang="ts">
/**
 * WineComparePage - Compare wines side by side
 *
 * Features:
 * - Start with wine A from route param or selection
 * - Search modal to add wine B
 * - Show comparison table with AI recommendations
 * - Cache indicator for previously compared pairs
 */

import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useWinesStore } from '@stores/wines.store'
import BaseButton from '@components/common/BaseButton.vue'
import BaseCard from '@components/common/BaseCard.vue'
import BaseModal from '@components/common/BaseModal.vue'
import BaseInput from '@components/common/BaseInput.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'
import BaseEmptyState from '@components/common/BaseEmptyState.vue'
import type { Wine, WineSummary, WineComparison } from '@domain/wine/wine.types'
import { useDebounce } from '@composables/useDebounce'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const winesStore = useWinesStore()

// State
const wineA = ref<Wine | null>(null)
const wineB = ref<Wine | null>(null)
const comparison = ref<(WineComparison & { fromCache: boolean }) | null>(null)
const loading = ref(false)
const searchModalOpen = ref(false)
const searchQuery = ref('')
const searchResults = ref<WineSummary[]>([])
const searching = ref(false)
const selectingFor = ref<'A' | 'B'>('B')

// Debounced search
const debouncedSearch = useDebounce(searchQuery, 300)

// Watch for route param changes
onMounted(async () => {
  const wineAId = route.params.wineAId as string
  if (wineAId) {
    await loadWineA(wineAId)
  }

  const wineBId = route.query.compareWith as string
  if (wineBId) {
    await loadWineB(wineBId)
  }
})

// Watch for search query changes
watch(debouncedSearch, async (query) => {
  if (query.length >= 2) {
    searching.value = true
    try {
      searchResults.value = await winesStore.searchWines(query, 10)
    } finally {
      searching.value = false
    }
  } else {
    searchResults.value = []
  }
})

// Load wine A
async function loadWineA(id: string) {
  loading.value = true
  try {
    await winesStore.fetchWine(id)
    wineA.value = winesStore.currentWine

    // If we have both wines, compare them
    if (wineA.value && wineB.value) {
      await runComparison()
    }
  } finally {
    loading.value = false
  }
}

// Load wine B
async function loadWineB(id: string) {
  loading.value = true
  try {
    await winesStore.fetchWine(id)
    wineB.value = winesStore.currentWine

    // If we have both wines, compare them
    if (wineA.value && wineB.value) {
      await runComparison()
    }
  } finally {
    loading.value = false
  }
}

// Run comparison
async function runComparison() {
  if (!wineA.value || !wineB.value) return

  loading.value = true
  try {
    comparison.value = await winesStore.compareWines([wineA.value.id, wineB.value.id])
  } catch (error) {
    console.error('Comparison failed:', error)
  } finally {
    loading.value = false
  }
}

// Open search modal
function openSearchModal(forWine: 'A' | 'B') {
  selectingFor.value = forWine
  searchQuery.value = ''
  searchResults.value = []
  searchModalOpen.value = true
}

// Select wine from search
async function selectWine(wine: WineSummary) {
  searchModalOpen.value = false

  if (selectingFor.value === 'A') {
    await loadWineA(wine.id)
    // Update URL
    router.replace({ params: { wineAId: wine.id }, query: route.query })
  } else {
    await loadWineB(wine.id)
    // Update URL
    router.replace({
      params: route.params,
      query: { ...route.query, compareWith: wine.id }
    })
  }
}

// Swap wines
function swapWines() {
  const temp = wineA.value
  wineA.value = wineB.value
  wineB.value = temp

  // Update URL
  if (wineA.value && wineB.value) {
    router.replace({
      params: { wineAId: wineA.value.id },
      query: { compareWith: wineB.value.id }
    })
  }
}

// Clear comparison
function clearAll() {
  wineA.value = null
  wineB.value = null
  comparison.value = null
  router.replace({ params: {}, query: {} })
}

// Navigate to wine details
function goToWineDetails(wineId: string) {
  router.push(`/wines/${wineId}`)
}

// Computed
const canCompare = computed(() => wineA.value && wineB.value)
const isCached = computed(() => comparison.value?.fromCache ?? false)

// Get winner indicator for attribute
function getWinnerClass(index: number, winnerIndex: number | null): string {
  if (winnerIndex === null) return ''
  return index === winnerIndex ? 'bg-green-50 text-green-700 font-semibold' : ''
}
</script>

<template>
  <div class="max-w-5xl mx-auto space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">{{ t('compare.title') }}</h1>
        <p class="text-gray-600 mt-1">{{ t('compare.subtitle') }}</p>
      </div>
      <div class="flex gap-2">
        <BaseButton v-if="canCompare" variant="ghost" size="sm" @click="swapWines">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
          </svg>
          {{ t('compare.swap') }}
        </BaseButton>
        <BaseButton v-if="wineA || wineB" variant="ghost" size="sm" @click="clearAll">
          {{ t('compare.clearAll') }}
        </BaseButton>
      </div>
    </div>

    <!-- Wine Selection Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- Wine A -->
      <BaseCard class="relative">
        <div class="absolute top-2 left-2 px-2 py-0.5 bg-wine-100 text-wine-700 text-xs font-medium rounded">
          {{ t('compare.wineA') }}
        </div>

        <div v-if="wineA" class="pt-6">
          <div class="flex gap-4">
            <div class="w-20 h-24 bg-gray-100 rounded flex-shrink-0 flex items-center justify-center">
              <img v-if="wineA.imageUrl" :src="wineA.imageUrl" :alt="wineA.name" class="w-full h-full object-cover rounded" />
              <svg v-else class="w-10 h-10 text-gray-300" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2L8 8h8l-4-6zm0 8c-2.21 0-4 1.79-4 4v6h8v-6c0-2.21-1.79-4-4-4z"/>
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="font-semibold text-gray-900 truncate cursor-pointer hover:text-wine-600" @click="goToWineDetails(wineA.id)">
                {{ wineA.name }}
              </h3>
              <p class="text-sm text-gray-600">{{ wineA.winery }}</p>
              <p class="text-sm text-gray-500">{{ wineA.vintage || 'NV' }} · {{ wineA.region }}</p>
              <div v-if="wineA.rating" class="flex items-center gap-1 mt-1">
                <svg class="w-4 h-4 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
                </svg>
                <span class="text-sm font-medium">{{ wineA.rating.toFixed(1) }}</span>
              </div>
            </div>
          </div>
          <BaseButton variant="outline" size="sm" class="mt-4 w-full" @click="openSearchModal('A')">
            {{ t('compare.changeWine') }}
          </BaseButton>
        </div>

        <div v-else class="pt-6 text-center py-8">
          <div class="w-16 h-16 mx-auto mb-4 bg-gray-100 rounded-full flex items-center justify-center">
            <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </div>
          <p class="text-gray-600 mb-4">{{ t('compare.selectFirstWine') }}</p>
          <BaseButton @click="openSearchModal('A')">
            {{ t('compare.selectWine') }}
          </BaseButton>
        </div>
      </BaseCard>

      <!-- Wine B -->
      <BaseCard class="relative">
        <div class="absolute top-2 left-2 px-2 py-0.5 bg-gray-100 text-gray-700 text-xs font-medium rounded">
          {{ t('compare.wineB') }}
        </div>

        <div v-if="wineB" class="pt-6">
          <div class="flex gap-4">
            <div class="w-20 h-24 bg-gray-100 rounded flex-shrink-0 flex items-center justify-center">
              <img v-if="wineB.imageUrl" :src="wineB.imageUrl" :alt="wineB.name" class="w-full h-full object-cover rounded" />
              <svg v-else class="w-10 h-10 text-gray-300" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2L8 8h8l-4-6zm0 8c-2.21 0-4 1.79-4 4v6h8v-6c0-2.21-1.79-4-4-4z"/>
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="font-semibold text-gray-900 truncate cursor-pointer hover:text-wine-600" @click="goToWineDetails(wineB.id)">
                {{ wineB.name }}
              </h3>
              <p class="text-sm text-gray-600">{{ wineB.winery }}</p>
              <p class="text-sm text-gray-500">{{ wineB.vintage || 'NV' }} · {{ wineB.region }}</p>
              <div v-if="wineB.rating" class="flex items-center gap-1 mt-1">
                <svg class="w-4 h-4 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
                </svg>
                <span class="text-sm font-medium">{{ wineB.rating.toFixed(1) }}</span>
              </div>
            </div>
          </div>
          <BaseButton variant="outline" size="sm" class="mt-4 w-full" @click="openSearchModal('B')">
            {{ t('compare.changeWine') }}
          </BaseButton>
        </div>

        <div v-else class="pt-6 text-center py-8">
          <div class="w-16 h-16 mx-auto mb-4 bg-gray-100 rounded-full flex items-center justify-center">
            <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </div>
          <p class="text-gray-600 mb-4">{{ t('compare.selectSecondWine') }}</p>
          <BaseButton :disabled="!wineA" @click="openSearchModal('B')">
            {{ t('compare.selectWine') }}
          </BaseButton>
        </div>
      </BaseCard>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-12">
      <BaseSpinner size="lg" />
    </div>

    <!-- Comparison Results -->
    <template v-else-if="comparison">
      <!-- Cache indicator -->
      <div v-if="isCached" class="flex items-center gap-2 p-3 bg-blue-50 border border-blue-200 rounded-lg">
        <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span class="text-sm text-blue-700">{{ t('compare.cachedResult') }}</span>
      </div>

      <!-- Comparison Table -->
      <BaseCard padding="none">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="border-b">
                <th class="text-left p-4 bg-gray-50 font-semibold text-gray-700 w-1/3">
                  {{ t('compare.attribute') }}
                </th>
                <th class="p-4 bg-gray-50 font-semibold text-gray-700 w-1/3 text-center">
                  {{ wineA?.name }}
                </th>
                <th class="p-4 bg-gray-50 font-semibold text-gray-700 w-1/3 text-center">
                  {{ wineB?.name }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="attr in comparison.attributes" :key="attr.name" class="border-b last:border-b-0">
                <td class="p-4 font-medium text-gray-700">{{ attr.name }}</td>
                <td :class="['p-4 text-center', getWinnerClass(0, attr.winnerIndex)]">
                  {{ attr.values[0] ?? '-' }}
                </td>
                <td :class="['p-4 text-center', getWinnerClass(1, attr.winnerIndex)]">
                  {{ attr.values[1] ?? '-' }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </BaseCard>

      <!-- AI Summary -->
      <BaseCard v-if="comparison.aiSummary">
        <div class="flex items-start gap-3">
          <div class="flex-shrink-0 w-10 h-10 bg-wine-100 rounded-full flex items-center justify-center">
            <svg class="w-5 h-5 text-wine-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
            </svg>
          </div>
          <div>
            <h3 class="font-semibold text-gray-900 mb-2">{{ t('compare.aiRecommendation') }}</h3>
            <p class="text-gray-600">{{ comparison.aiSummary }}</p>
          </div>
        </div>
      </BaseCard>
    </template>

    <!-- Empty state when no wines selected -->
    <BaseEmptyState
      v-else-if="!wineA && !wineB"
      icon="wine"
      :title="t('compare.emptyTitle')"
      :description="t('compare.emptyDescription')"
    >
      <template #action>
        <router-link to="/wines">
          <BaseButton>{{ t('compare.browseWines') }}</BaseButton>
        </router-link>
      </template>
    </BaseEmptyState>

    <!-- Search Modal -->
    <BaseModal v-model="searchModalOpen" @close="searchModalOpen = false">
      <template #header>
        <h2 class="text-xl font-semibold text-gray-900">{{ t('compare.searchWine') }}</h2>
      </template>

      <div class="space-y-4">
        <BaseInput
          v-model="searchQuery"
          :placeholder="t('compare.searchPlaceholder')"
          autofocus
        >
          <template #prefix>
            <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </template>
        </BaseInput>

        <!-- Loading -->
        <div v-if="searching" class="flex justify-center py-8">
          <BaseSpinner />
        </div>

        <!-- Results -->
        <div v-else-if="searchResults.length > 0" class="space-y-2 max-h-80 overflow-y-auto">
          <button
            v-for="wine in searchResults"
            :key="wine.id"
            type="button"
            class="w-full flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50 transition-colors text-left"
            :disabled="wine.id === wineA?.id || wine.id === wineB?.id"
            :class="{ 'opacity-50 cursor-not-allowed': wine.id === wineA?.id || wine.id === wineB?.id }"
            @click="selectWine(wine)"
          >
            <div class="w-12 h-14 bg-gray-100 rounded flex-shrink-0 flex items-center justify-center">
              <img v-if="wine.imageUrl" :src="wine.imageUrl" :alt="wine.name" class="w-full h-full object-cover rounded" />
              <svg v-else class="w-6 h-6 text-gray-300" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2L8 8h8l-4-6zm0 8c-2.21 0-4 1.79-4 4v6h8v-6c0-2.21-1.79-4-4-4z"/>
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <p class="font-medium text-gray-900 truncate">{{ wine.name }}</p>
              <p class="text-sm text-gray-500">{{ wine.winery }} · {{ wine.vintage || 'NV' }}</p>
            </div>
            <div v-if="wine.rating" class="flex items-center gap-1 text-sm">
              <svg class="w-4 h-4 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/>
              </svg>
              {{ wine.rating.toFixed(1) }}
            </div>
          </button>
        </div>

        <!-- No results -->
        <div v-else-if="searchQuery.length >= 2" class="text-center py-8 text-gray-500">
          {{ t('compare.noResults') }}
        </div>

        <!-- Initial state -->
        <div v-else class="text-center py-8 text-gray-500">
          {{ t('compare.typeToSearch') }}
        </div>
      </div>
    </BaseModal>
  </div>
</template>


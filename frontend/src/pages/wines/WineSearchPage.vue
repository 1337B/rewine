<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useWinesStore } from '@stores/wines.store'
import { useDebounce } from '@composables/useDebounce'
import BaseInput from '@components/common/BaseInput.vue'
import BaseSelect from '@components/common/BaseSelect.vue'
import BaseCard from '@components/common/BaseCard.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'
import BaseEmptyState from '@components/common/BaseEmptyState.vue'
import { WINE_TYPES, WINE_REGIONS } from '@config/constants'

const winesStore = useWinesStore()

const search = ref('')
const selectedType = ref<string | null>(null)
const selectedRegion = ref<string | null>(null)

const debouncedSearch = useDebounce(search, 300)

const typeOptions = WINE_TYPES.map((t) => ({ value: t.value, label: t.label }))
const regionOptions = WINE_REGIONS.map((r) => ({ value: r.value, label: r.label }))

onMounted(() => {
  winesStore.fetchWines()
})

watch([debouncedSearch, selectedType, selectedRegion], () => {
  winesStore.setFilter({
    search: debouncedSearch.value || undefined,
    type: selectedType.value || undefined,
    region: selectedRegion.value || undefined,
  })
  winesStore.fetchWines()
})

function clearFilters() {
  search.value = ''
  selectedType.value = null
  selectedRegion.value = null
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <h1 class="text-2xl font-bold text-gray-900">Browse Wines</h1>
    </div>

    <!-- Filters -->
    <BaseCard padding="md">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <BaseInput
          v-model="search"
          type="search"
          placeholder="Search wines..."
        />
        <BaseSelect
          v-model="selectedType"
          :options="typeOptions"
          placeholder="All Types"
        />
        <BaseSelect
          v-model="selectedRegion"
          :options="regionOptions"
          placeholder="All Regions"
        />
        <button
          type="button"
          class="text-wine-600 hover:text-wine-700 text-sm font-medium"
          @click="clearFilters"
        >
          Clear filters
        </button>
      </div>
    </BaseCard>

    <!-- Loading -->
    <div v-if="winesStore.loading" class="flex justify-center py-12">
      <BaseSpinner size="lg" />
    </div>

    <!-- Empty State -->
    <BaseEmptyState
      v-else-if="!winesStore.hasWines"
      icon="wine"
      title="No wines found"
      description="Try adjusting your search or filters"
    />

    <!-- Wine Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <router-link
        v-for="wine in winesStore.wines"
        :key="wine.id"
        :to="`/wines/${wine.id}`"
      >
        <BaseCard hoverable padding="none" class="overflow-hidden">
          <div class="aspect-[3/4] bg-gray-100 relative">
            <img
              v-if="wine.imageUrl"
              :src="wine.imageUrl"
              :alt="wine.name"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center bg-gray-50">
              <img src="/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg" alt="Wine" class="w-16 h-16 opacity-40" />
            </div>
          </div>
          <div class="p-4">
            <h3 class="font-semibold text-gray-900 truncate">{{ wine.name }}</h3>
            <p class="text-sm text-gray-600 truncate">{{ wine.winery }}</p>
            <div class="flex items-center justify-between mt-2">
              <span class="text-sm text-wine-600 capitalize">{{ wine.type }}</span>
              <span v-if="wine.rating" class="text-sm text-gray-600">
                ⭐ {{ wine.rating.toFixed(1) }}
              </span>
            </div>
            <p v-if="wine.price" class="text-lg font-semibold text-gray-900 mt-2">
              ${{ wine.price }}
            </p>
          </div>
        </BaseCard>
      </router-link>
    </div>

    <!-- Pagination placeholder -->
    <div v-if="winesStore.pagination" class="flex justify-center gap-2">
      <span class="text-sm text-gray-600">
        Showing {{ winesStore.wines.length }} of {{ winesStore.totalWines }} wines
      </span>
    </div>
  </div>
</template>


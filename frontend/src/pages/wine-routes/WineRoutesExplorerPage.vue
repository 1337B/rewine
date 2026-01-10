<script setup lang="ts">
import { onMounted } from 'vue'
import { useWineRoutesStore } from '@stores/wineRoutes.store'
import BaseCard from '@components/common/BaseCard.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'
import BaseEmptyState from '@components/common/BaseEmptyState.vue'
import { formatDuration, formatDistance } from '@utils/format'

const routesStore = useWineRoutesStore()

onMounted(() => {
  routesStore.fetchRoutes()
})

const difficultyColors = {
  easy: 'bg-green-100 text-green-700',
  moderate: 'bg-yellow-100 text-yellow-700',
  challenging: 'bg-red-100 text-red-700',
}
</script>

<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-900">Wine Routes</h1>

    <!-- Loading -->
    <div v-if="routesStore.loading" class="flex justify-center py-12">
      <BaseSpinner size="lg" />
    </div>

    <!-- Empty State -->
    <BaseEmptyState
      v-else-if="!routesStore.hasRoutes"
      icon="map"
      title="No wine routes found"
      description="Check back later for curated wine routes"
    />

    <!-- Routes List -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <router-link
        v-for="route in routesStore.routes"
        :key="route.id"
        :to="`/wine-routes/${route.id}`"
      >
        <BaseCard hoverable padding="none" class="overflow-hidden">
          <div class="aspect-video bg-gray-100 relative">
            <img
              v-if="route.imageUrl"
              :src="route.imageUrl"
              :alt="route.name"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center bg-gray-50">
              <img src="/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg" alt="Route" class="w-16 h-16 opacity-40" />
            </div>
            <span
              :class="[
                'absolute top-2 right-2 px-2 py-1 text-xs rounded capitalize',
                difficultyColors[route.difficulty]
              ]"
            >
              {{ route.difficulty }}
            </span>
          </div>
          <div class="p-4">
            <h3 class="font-semibold text-gray-900 truncate">{{ route.name }}</h3>
            <p class="text-sm text-gray-600 mt-1">{{ route.region }}, {{ route.country }}</p>
            <div class="flex items-center gap-4 mt-3 text-sm text-gray-500">
              <span>🕐 {{ formatDuration(route.duration * 60) }}</span>
              <span>📏 {{ formatDistance(route.distance) }}</span>
              <span>📍 {{ route.stops.length }} stops</span>
            </div>
            <div class="flex items-center justify-between mt-3">
              <span v-if="route.rating" class="text-wine-600">
                ⭐ {{ route.rating.toFixed(1) }}
              </span>
              <span class="text-sm text-gray-500">
                {{ route.reviewCount }} reviews
              </span>
            </div>
          </div>
        </BaseCard>
      </router-link>
    </div>
  </div>
</template>


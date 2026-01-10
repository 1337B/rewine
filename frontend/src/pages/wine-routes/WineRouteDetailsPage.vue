<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWineRoutesStore } from '@stores/wineRoutes.store'
import BaseButton from '@components/common/BaseButton.vue'
import BaseCard from '@components/common/BaseCard.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'
import { formatDuration, formatDistance } from '@utils/format'

const route = useRoute()
const router = useRouter()
const routesStore = useWineRoutesStore()

const routeId = computed(() => route.params.id as string)
const wineRoute = computed(() => routesStore.currentRoute)

onMounted(() => {
  if (routeId.value) {
    routesStore.fetchRoute(routeId.value)
  }
})

function goBack() {
  router.back()
}

const difficultyColors = {
  easy: 'bg-green-100 text-green-700',
  moderate: 'bg-yellow-100 text-yellow-700',
  challenging: 'bg-red-100 text-red-700',
}

const stopTypeIcons: Record<string, string> = {
  winery: '/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg',
  restaurant: '/images/icons/reshot-icon-menu-UWTLEFDCQN.svg',
  viewpoint: '/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg',
  attraction: '/images/icons/reshot-icon-vine-tasting-P5GDV7FUBS.svg',
  accommodation: '/images/icons/reshot-icon-guided-tour-TN52W87MA6.svg',
}
</script>

<template>
  <div>
    <!-- Back button -->
    <button
      type="button"
      class="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-6"
      @click="goBack"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      Back
    </button>

    <!-- Loading -->
    <div v-if="routesStore.loading" class="flex justify-center py-12">
      <BaseSpinner size="lg" />
    </div>

    <!-- Route Details -->
    <div v-else-if="wineRoute" class="space-y-6">
      <!-- Header -->
      <div class="aspect-[3/1] bg-gray-100 rounded-lg overflow-hidden">
        <img
          v-if="wineRoute.imageUrl"
          :src="wineRoute.imageUrl"
          :alt="wineRoute.name"
          class="w-full h-full object-cover"
        />
        <div v-else class="w-full h-full flex items-center justify-center bg-gray-50">
          <img src="/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg" alt="Route" class="w-24 h-24 opacity-40" />
        </div>
      </div>

      <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">{{ wineRoute.name }}</h1>
          <p class="text-lg text-gray-600">{{ wineRoute.region }}, {{ wineRoute.country }}</p>
        </div>

        <div class="flex flex-wrap gap-2">
          <span
            :class="[
              'px-3 py-1 rounded-full text-sm capitalize',
              difficultyColors[wineRoute.difficulty]
            ]"
          >
            {{ wineRoute.difficulty }}
          </span>
        </div>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <BaseCard class="text-center">
          <div class="text-2xl font-bold text-wine-600">{{ formatDuration(wineRoute.duration * 60) }}</div>
          <div class="text-sm text-gray-500">Duration</div>
        </BaseCard>
        <BaseCard class="text-center">
          <div class="text-2xl font-bold text-gray-900">{{ formatDistance(wineRoute.distance) }}</div>
          <div class="text-sm text-gray-500">Distance</div>
        </BaseCard>
        <BaseCard class="text-center">
          <div class="text-2xl font-bold text-gray-900">{{ wineRoute.stops.length }}</div>
          <div class="text-sm text-gray-500">Stops</div>
        </BaseCard>
        <BaseCard class="text-center">
          <div class="text-2xl font-bold text-wine-600">{{ wineRoute.rating?.toFixed(1) || '-' }}</div>
          <div class="text-sm text-gray-500">{{ wineRoute.reviewCount }} reviews</div>
        </BaseCard>
      </div>

      <!-- Description -->
      <BaseCard>
        <h3 class="font-semibold text-gray-900 mb-2">About this route</h3>
        <p class="text-gray-600 whitespace-pre-line">{{ wineRoute.description }}</p>
      </BaseCard>

      <!-- Stops -->
      <div>
        <h3 class="font-semibold text-gray-900 mb-4">Route Stops</h3>
        <div class="space-y-4">
          <BaseCard
            v-for="(stop, index) in wineRoute.stops"
            :key="stop.id"
            class="flex gap-4"
          >
            <div class="flex-shrink-0 w-10 h-10 bg-wine-100 text-wine-600 rounded-full flex items-center justify-center font-semibold">
              {{ index + 1 }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <img :src="stopTypeIcons[stop.type]" :alt="stop.type" class="w-6 h-6" />
                <h4 class="font-semibold text-gray-900">{{ stop.name }}</h4>
              </div>
              <p class="text-sm text-gray-600 mt-1">{{ stop.description }}</p>
              <div class="flex items-center gap-4 mt-2 text-sm text-gray-500">
                <span>📍 {{ stop.location.city }}</span>
                <span>🕐 {{ formatDuration(stop.duration) }}</span>
              </div>
            </div>
          </BaseCard>
        </div>
      </div>

      <div class="flex justify-center">
        <BaseButton size="lg">Start Route</BaseButton>
      </div>
    </div>

    <!-- Not found -->
    <div v-else class="text-center py-12">
      <p class="text-gray-600">Route not found</p>
    </div>
  </div>
</template>


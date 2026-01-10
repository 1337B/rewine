<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWinesStore } from '@stores/wines.store'
import BaseButton from '@components/common/BaseButton.vue'
import BaseCard from '@components/common/BaseCard.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'

const route = useRoute()
const router = useRouter()
const winesStore = useWinesStore()

const wineId = computed(() => route.params.id as string)
const wine = computed(() => winesStore.currentWine)

onMounted(() => {
  if (wineId.value) {
    winesStore.fetchWine(wineId.value)
  }
})

function goBack() {
  router.back()
}

function addToComparison() {
  if (wine.value) {
    winesStore.addToComparison(wine.value)
  }
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
    <div v-if="winesStore.loading" class="flex justify-center py-12">
      <BaseSpinner size="lg" />
    </div>

    <!-- Wine Details -->
    <div v-else-if="wine" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Image -->
      <div class="lg:col-span-1">
        <BaseCard padding="none" class="overflow-hidden">
          <div class="aspect-[3/4] bg-gray-100">
            <img
              v-if="wine.imageUrl"
              :src="wine.imageUrl"
              :alt="wine.name"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center bg-gray-50">
              <img src="/images/icons/reshot-icon-wine-bottle-T9X8JUFM32.svg" alt="Wine" class="w-24 h-24 opacity-40" />
            </div>
          </div>
        </BaseCard>
      </div>

      <!-- Info -->
      <div class="lg:col-span-2 space-y-6">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ wine.name }}</h1>
          <p class="text-xl text-gray-600">{{ wine.winery }}</p>
        </div>

        <div class="flex flex-wrap gap-4">
          <span class="px-3 py-1 bg-wine-100 text-wine-700 rounded-full capitalize">
            {{ wine.type }}
          </span>
          <span class="px-3 py-1 bg-gray-100 text-gray-700 rounded-full">
            {{ wine.region }}, {{ wine.country }}
          </span>
          <span v-if="wine.vintage" class="px-3 py-1 bg-gray-100 text-gray-700 rounded-full">
            {{ wine.vintage }}
          </span>
        </div>

        <div class="flex items-center gap-6">
          <div v-if="wine.rating" class="text-center">
            <div class="text-3xl font-bold text-wine-600">{{ wine.rating.toFixed(1) }}</div>
            <div class="text-sm text-gray-500">{{ wine.reviewCount }} reviews</div>
          </div>
          <div v-if="wine.price" class="text-center">
            <div class="text-3xl font-bold text-gray-900">${{ wine.price }}</div>
            <div class="text-sm text-gray-500">Price</div>
          </div>
          <div v-if="wine.alcoholContent" class="text-center">
            <div class="text-3xl font-bold text-gray-900">{{ wine.alcoholContent }}%</div>
            <div class="text-sm text-gray-500">Alcohol</div>
          </div>
        </div>

        <div class="flex gap-4">
          <BaseButton @click="addToComparison">Add to Compare</BaseButton>
          <BaseButton variant="outline">Add to Cellar</BaseButton>
        </div>

        <BaseCard v-if="wine.description">
          <h3 class="font-semibold text-gray-900 mb-2">Description</h3>
          <p class="text-gray-600">{{ wine.description }}</p>
        </BaseCard>

        <BaseCard v-if="wine.grapeVarieties?.length">
          <h3 class="font-semibold text-gray-900 mb-2">Grape Varieties</h3>
          <div class="flex flex-wrap gap-2">
            <span
              v-for="grape in wine.grapeVarieties"
              :key="grape"
              class="px-2 py-1 bg-gray-100 text-gray-700 rounded text-sm"
            >
              {{ grape }}
            </span>
          </div>
        </BaseCard>

        <BaseCard v-if="wine.foodPairings?.length">
          <h3 class="font-semibold text-gray-900 mb-2">Food Pairings</h3>
          <div class="flex flex-wrap gap-2">
            <span
              v-for="pairing in wine.foodPairings"
              :key="pairing"
              class="px-2 py-1 bg-gray-100 text-gray-700 rounded text-sm"
            >
              {{ pairing }}
            </span>
          </div>
        </BaseCard>
      </div>
    </div>

    <!-- Not found -->
    <div v-else class="text-center py-12">
      <p class="text-gray-600">Wine not found</p>
    </div>
  </div>
</template>


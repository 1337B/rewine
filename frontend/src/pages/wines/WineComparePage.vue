<script setup lang="ts">
import { computed } from 'vue'
import { useWinesStore } from '@stores/wines.store'
import BaseButton from '@components/common/BaseButton.vue'
import BaseEmptyState from '@components/common/BaseEmptyState.vue'

const winesStore = useWinesStore()
const wines = computed(() => winesStore.comparisonWines)

function removeWine(wineId: string) {
  winesStore.removeFromComparison(wineId)
}

function clearAll() {
  winesStore.clearComparison()
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-bold text-gray-900">Compare Wines</h1>
      <BaseButton v-if="wines.length > 0" variant="ghost" @click="clearAll">
        Clear All
      </BaseButton>
    </div>

    <BaseEmptyState
      v-if="wines.length === 0"
      icon="wine"
      title="No wines to compare"
      description="Add wines to compare from the wine search or details page"
    >
      <template #action>
        <router-link to="/wines">
          <BaseButton>Browse Wines</BaseButton>
        </router-link>
      </template>
    </BaseEmptyState>

    <div v-else class="overflow-x-auto">
      <table class="w-full">
        <thead>
          <tr>
            <th class="text-left p-4 bg-gray-50 font-semibold text-gray-900">Attribute</th>
            <th
              v-for="wine in wines"
              :key="wine.id"
              class="p-4 bg-gray-50 min-w-[200px]"
            >
              <div class="relative">
                <button
                  type="button"
                  class="absolute -top-2 -right-2 p-1 text-gray-400 hover:text-red-500"
                  @click="removeWine(wine.id)"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
                <div class="text-center">
                  <div class="w-16 h-20 mx-auto mb-2 bg-gray-100 rounded flex items-center justify-center">
                    <img src="/images/icons/reshot-icon-wine-bottle-T9X8JUFM32.svg" alt="Wine" class="w-10 h-10 opacity-60" />
                  </div>
                  <p class="font-semibold text-gray-900 text-sm">{{ wine.name }}</p>
                  <p class="text-xs text-gray-500">{{ wine.winery }}</p>
                </div>
              </div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr class="border-t">
            <td class="p-4 font-medium text-gray-700">Type</td>
            <td v-for="wine in wines" :key="wine.id" class="p-4 text-center capitalize">
              {{ wine.type }}
            </td>
          </tr>
          <tr class="border-t">
            <td class="p-4 font-medium text-gray-700">Region</td>
            <td v-for="wine in wines" :key="wine.id" class="p-4 text-center">
              {{ wine.region }}
            </td>
          </tr>
          <tr class="border-t">
            <td class="p-4 font-medium text-gray-700">Vintage</td>
            <td v-for="wine in wines" :key="wine.id" class="p-4 text-center">
              {{ wine.vintage || '-' }}
            </td>
          </tr>
          <tr class="border-t">
            <td class="p-4 font-medium text-gray-700">Rating</td>
            <td v-for="wine in wines" :key="wine.id" class="p-4 text-center">
              {{ wine.rating ? `${wine.rating.toFixed(1)} ⭐` : '-' }}
            </td>
          </tr>
          <tr class="border-t">
            <td class="p-4 font-medium text-gray-700">Price</td>
            <td v-for="wine in wines" :key="wine.id" class="p-4 text-center font-semibold">
              {{ wine.price ? `$${wine.price}` : '-' }}
            </td>
          </tr>
          <tr class="border-t">
            <td class="p-4 font-medium text-gray-700">Alcohol</td>
            <td v-for="wine in wines" :key="wine.id" class="p-4 text-center">
              {{ wine.alcoholContent ? `${wine.alcoholContent}%` : '-' }}
            </td>
          </tr>
          <tr class="border-t">
            <td class="p-4 font-medium text-gray-700">Grapes</td>
            <td v-for="wine in wines" :key="wine.id" class="p-4 text-center text-sm">
              {{ wine.grapeVarieties?.join(', ') || '-' }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>


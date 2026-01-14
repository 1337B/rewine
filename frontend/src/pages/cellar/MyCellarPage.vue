<script setup lang="ts">
import { computed } from 'vue'
import { useCellarStore } from '@stores/cellar.store'
import BaseCard from '@components/common/BaseCard.vue'
import BaseButton from '@components/common/BaseButton.vue'
import BaseEmptyState from '@components/common/BaseEmptyState.vue'
import { formatCurrency } from '@utils/format'

const cellarStore = useCellarStore()

const cellarWines = computed(() => cellarStore.cellarWines)
const totalBottles = computed(() => cellarStore.totalBottles)
const totalValue = computed(() => cellarStore.totalValue)
</script>

<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-bold text-gray-900">My Cellar</h1>
      <router-link to="/wines">
        <BaseButton>Add Wine</BaseButton>
      </router-link>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <BaseCard class="text-center">
        <div class="text-3xl font-bold text-wine-600">{{ totalBottles }}</div>
        <div class="text-sm text-gray-500">Total Bottles</div>
      </BaseCard>
      <BaseCard class="text-center">
        <div class="text-3xl font-bold text-gray-900">{{ formatCurrency(totalValue) }}</div>
        <div class="text-sm text-gray-500">Estimated Value</div>
      </BaseCard>
      <BaseCard class="text-center">
        <div class="text-3xl font-bold text-gray-900">{{ Object.keys(cellarStore.winesByType).length }}</div>
        <div class="text-sm text-gray-500">Wine Types</div>
      </BaseCard>
    </div>

    <!-- Empty State -->
    <BaseEmptyState
      v-if="cellarWines.length === 0"
      icon="wine"
      title="Your cellar is empty"
      description="Start adding wines to track your collection"
    >
      <template #action>
        <router-link to="/wines">
          <BaseButton>Browse Wines</BaseButton>
        </router-link>
      </template>
    </BaseEmptyState>

    <!-- Cellar List -->
    <div v-else class="space-y-4">
      <BaseCard
        v-for="cellarWine in cellarWines"
        :key="cellarWine.id"
        class="flex gap-4"
      >
        <div class="w-16 h-20 bg-gray-100 rounded flex items-center justify-center flex-shrink-0">
          <img src="/images/icons/reshot-icon-wine-bottle-T9X8JUFM32.svg" alt="Wine" class="w-10 h-10 opacity-60" />
        </div>
        <div class="flex-1 min-w-0">
          <h3 class="font-semibold text-gray-900 truncate">{{ cellarWine.wine.name }}</h3>
          <p class="text-sm text-gray-600">{{ cellarWine.wine.winery }}</p>
          <div class="flex items-center gap-4 mt-2 text-sm text-gray-500">
            <span>{{ cellarWine.quantity }} bottles</span>
            <span v-if="cellarWine.location">📍 {{ cellarWine.location }}</span>
          </div>
        </div>
        <div class="text-right">
          <p v-if="cellarWine.purchasePrice" class="font-semibold text-gray-900">
            {{ formatCurrency(cellarWine.purchasePrice) }}
          </p>
          <p class="text-sm text-gray-500 capitalize">{{ cellarWine.wine.type }}</p>
        </div>
      </BaseCard>
    </div>
  </div>
</template>


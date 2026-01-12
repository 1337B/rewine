<script setup lang="ts">
/**
 * WineDetailsPage - Display wine details with AI profile integration
 */

import { onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useWinesStore } from '@stores/wines.store'
import BaseButton from '@components/common/BaseButton.vue'
import BaseCard from '@components/common/BaseCard.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const winesStore = useWinesStore()

const wineId = computed(() => route.params.id as string)
const wine = computed(() => winesStore.currentWine)
const hasAiProfile = computed(() => winesStore.hasAiProfile(wineId.value))

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

function goToCompare() {
  if (wine.value) {
    router.push(`/wines/compare/${wine.value.id}`)
  }
}

function goToAiProfile() {
  if (wine.value) {
    router.push(`/wines/${wine.value.id}/ai-profile`)
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
      {{ t('common.back') }}
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
            <div class="text-sm text-gray-500">{{ wine.reviewCount }} {{ t('wines.reviews') }}</div>
          </div>
          <div v-if="wine.price" class="text-center">
            <div class="text-3xl font-bold text-gray-900">${{ wine.price }}</div>
            <div class="text-sm text-gray-500">{{ t('wines.price') }}</div>
          </div>
          <div v-if="wine.alcoholContent" class="text-center">
            <div class="text-3xl font-bold text-gray-900">{{ wine.alcoholContent }}%</div>
            <div class="text-sm text-gray-500">{{ t('wineDetails.alcohol') }}</div>
          </div>
        </div>

        <!-- Action buttons -->
        <div class="flex flex-wrap gap-3">
          <BaseButton @click="addToComparison">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
            {{ t('wines.addToCompare') }}
          </BaseButton>
          <BaseButton variant="outline" @click="goToCompare">
            {{ t('wines.compare') }}
          </BaseButton>
          <BaseButton variant="outline">
            {{ t('wineDetails.addToCellar') }}
          </BaseButton>
        </div>

        <!-- AI Profile Button -->
        <BaseCard class="bg-gradient-to-r from-wine-50 to-wine-100 border-wine-200">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-wine-600 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                </svg>
              </div>
              <div>
                <h3 class="font-semibold text-gray-900">{{ t('wineDetails.aiSommelier') }}</h3>
                <p class="text-sm text-gray-600">
                  {{ hasAiProfile ? t('wineDetails.profileGenerated') : t('wineDetails.consultAi') }}
                </p>
              </div>
            </div>
            <BaseButton
              :variant="hasAiProfile ? 'outline' : 'primary'"
              @click="goToAiProfile"
            >
              {{ hasAiProfile ? t('wineDetails.viewProfile') : t('wineDetails.generateProfile') }}
            </BaseButton>
          </div>
        </BaseCard>

        <BaseCard v-if="wine.description">
          <h3 class="font-semibold text-gray-900 mb-2">{{ t('wines.description') }}</h3>
          <p class="text-gray-600">{{ wine.description }}</p>
        </BaseCard>

        <BaseCard v-if="wine.grapeVarieties?.length">
          <h3 class="font-semibold text-gray-900 mb-2">{{ t('wines.grapeVariety') }}</h3>
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
          <h3 class="font-semibold text-gray-900 mb-2">{{ t('wines.foodPairings') }}</h3>
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
      <p class="text-gray-600">{{ t('wineDetails.notFound') }}</p>
    </div>
  </div>
</template>


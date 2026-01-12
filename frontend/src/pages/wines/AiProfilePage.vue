<script setup lang="ts">
/**
 * AiProfilePage - Display AI-generated wine profile
 *
 * Features:
 * - Show AI-generated wine analysis
 * - Display generation timestamp
 * - Food pairing recommendations with match scores
 * - Similar wines suggestions
 */

import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useWinesStore } from '@stores/wines.store'
import BaseButton from '@components/common/BaseButton.vue'
import BaseCard from '@components/common/BaseCard.vue'
import BaseSpinner from '@components/common/BaseSpinner.vue'
import type { Wine, AiWineProfile } from '@domain/wine/wine.types'
import { formatDate } from '@utils/date'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const winesStore = useWinesStore()

// State
const wine = ref<Wine | null>(null)
const profile = ref<(AiWineProfile & { fromCache: boolean }) | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

// Computed
const wineId = computed(() => route.params.id as string)
const isCached = computed(() => profile.value?.fromCache ?? false)

onMounted(async () => {
  if (!wineId.value) {
    router.replace('/wines')
    return
  }

  loading.value = true
  error.value = null

  try {
    // Load wine details
    await winesStore.fetchWine(wineId.value)
    wine.value = winesStore.currentWine

    // Load AI profile
    profile.value = await winesStore.getAiProfile(wineId.value)
  } catch (err) {
    console.error('Failed to load AI profile:', err)
    error.value = err instanceof Error ? err.message : 'Failed to load profile'
  } finally {
    loading.value = false
  }
})

function goBack() {
  router.back()
}

function goToWineDetails() {
  if (wine.value) {
    router.push(`/wines/${wine.value.id}`)
  }
}

// Get color class based on match score
function getMatchScoreClass(score: number): string {
  if (score >= 90) return 'bg-green-100 text-green-700'
  if (score >= 70) return 'bg-yellow-100 text-yellow-700'
  return 'bg-gray-100 text-gray-700'
}
</script>

<template>
  <div class="max-w-4xl mx-auto space-y-6">
    <!-- Back button -->
    <button
      type="button"
      class="flex items-center gap-2 text-gray-600 hover:text-gray-900"
      @click="goBack"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      {{ t('common.back') }}
    </button>

    <!-- Loading -->
    <div v-if="loading" class="flex flex-col items-center justify-center py-16">
      <BaseSpinner size="lg" />
      <p class="mt-4 text-gray-600">{{ t('aiProfile.loading') }}</p>
    </div>

    <!-- Error -->
    <BaseCard v-else-if="error" class="text-center py-12">
      <div class="w-16 h-16 mx-auto mb-4 bg-red-100 rounded-full flex items-center justify-center">
        <svg class="w-8 h-8 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
      </div>
      <h2 class="text-xl font-semibold text-gray-900 mb-2">{{ t('aiProfile.errorTitle') }}</h2>
      <p class="text-gray-600 mb-4">{{ error }}</p>
      <BaseButton @click="goBack">{{ t('common.goBack') }}</BaseButton>
    </BaseCard>

    <!-- Profile Content -->
    <template v-else-if="profile && wine">
      <!-- Header -->
      <div class="flex items-start justify-between">
        <div>
          <div class="flex items-center gap-3 mb-2">
            <div class="w-10 h-10 bg-gradient-to-br from-wine-500 to-wine-700 rounded-full flex items-center justify-center">
              <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
            </div>
            <div>
              <h1 class="text-2xl font-bold text-gray-900">{{ t('aiProfile.title') }}</h1>
              <p class="text-gray-500 text-sm">{{ t('aiProfile.poweredBy') }}</p>
            </div>
          </div>
        </div>

        <!-- Cache/Timestamp indicator -->
        <div class="text-right">
          <div v-if="isCached" class="inline-flex items-center gap-1 px-2 py-1 bg-blue-50 text-blue-700 text-xs font-medium rounded-full mb-1">
            <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            {{ t('aiProfile.cached') }}
          </div>
          <p class="text-xs text-gray-500">
            {{ t('aiProfile.generatedAt') }}: {{ formatDate(profile.generatedAt) }}
          </p>
        </div>
      </div>

      <!-- Wine Info -->
      <BaseCard class="bg-gray-50">
        <div class="flex items-center gap-4 cursor-pointer" @click="goToWineDetails">
          <div class="w-16 h-20 bg-white rounded flex-shrink-0 flex items-center justify-center shadow-sm">
            <img v-if="wine.imageUrl" :src="wine.imageUrl" :alt="wine.name" class="w-full h-full object-cover rounded" />
            <svg v-else class="w-8 h-8 text-gray-300" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2L8 8h8l-4-6zm0 8c-2.21 0-4 1.79-4 4v6h8v-6c0-2.21-1.79-4-4-4z"/>
            </svg>
          </div>
          <div>
            <h2 class="text-lg font-semibold text-gray-900 hover:text-wine-600">{{ wine.name }}</h2>
            <p class="text-gray-600">{{ wine.winery }}</p>
            <p class="text-sm text-gray-500">{{ wine.vintage || 'NV' }} · {{ wine.region }}, {{ wine.country }}</p>
          </div>
        </div>
      </BaseCard>

      <!-- AI Summary -->
      <BaseCard>
        <h3 class="font-semibold text-gray-900 mb-3">{{ t('aiProfile.summary') }}</h3>
        <p class="text-gray-600 leading-relaxed">{{ profile.summary }}</p>
      </BaseCard>

      <!-- Personality Traits -->
      <BaseCard v-if="profile.personalityTraits?.length">
        <h3 class="font-semibold text-gray-900 mb-3">{{ t('aiProfile.personality') }}</h3>
        <div class="flex flex-wrap gap-2">
          <span
            v-for="trait in profile.personalityTraits"
            :key="trait"
            class="px-3 py-1 bg-wine-50 text-wine-700 rounded-full text-sm font-medium"
          >
            {{ trait }}
          </span>
        </div>
      </BaseCard>

      <!-- Ideal Occasions -->
      <BaseCard v-if="profile.idealOccasions?.length">
        <h3 class="font-semibold text-gray-900 mb-3">{{ t('aiProfile.idealOccasions') }}</h3>
        <ul class="space-y-2">
          <li
            v-for="occasion in profile.idealOccasions"
            :key="occasion"
            class="flex items-center gap-2"
          >
            <svg class="w-5 h-5 text-wine-500 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="text-gray-700">{{ occasion }}</span>
          </li>
        </ul>
      </BaseCard>

      <!-- Food Pairings -->
      <BaseCard v-if="profile.foodPairingsDetailed?.length">
        <h3 class="font-semibold text-gray-900 mb-4">{{ t('aiProfile.foodPairings') }}</h3>
        <div class="space-y-4">
          <div
            v-for="pairing in profile.foodPairingsDetailed"
            :key="pairing.dish"
            class="flex items-start gap-4 p-3 bg-gray-50 rounded-lg"
          >
            <div :class="['flex-shrink-0 px-2 py-1 rounded text-xs font-bold', getMatchScoreClass(pairing.matchScore)]">
              {{ pairing.matchScore }}%
            </div>
            <div class="flex-1">
              <div class="flex items-center gap-2">
                <h4 class="font-medium text-gray-900">{{ pairing.dish }}</h4>
                <span class="text-xs text-gray-500 bg-gray-200 px-2 py-0.5 rounded">{{ pairing.category }}</span>
              </div>
              <p class="text-sm text-gray-600 mt-1">{{ pairing.reason }}</p>
            </div>
          </div>
        </div>
      </BaseCard>

      <!-- Similar Wines -->
      <BaseCard v-if="profile.similarWines?.length">
        <h3 class="font-semibold text-gray-900 mb-3">{{ t('aiProfile.similarWines') }}</h3>
        <div class="flex flex-wrap gap-2">
          <span
            v-for="similar in profile.similarWines"
            :key="similar"
            class="px-3 py-2 bg-gray-100 text-gray-700 rounded-lg text-sm"
          >
            {{ similar }}
          </span>
        </div>
      </BaseCard>

      <!-- Actions -->
      <div class="flex gap-4 justify-center pt-4">
        <BaseButton variant="outline" @click="goToWineDetails">
          {{ t('aiProfile.viewWineDetails') }}
        </BaseButton>
        <router-link :to="`/wines/compare/${wine.id}`">
          <BaseButton variant="outline">
            {{ t('aiProfile.compareThisWine') }}
          </BaseButton>
        </router-link>
      </div>
    </template>
  </div>
</template>


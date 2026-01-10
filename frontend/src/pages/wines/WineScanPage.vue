<script setup lang="ts">
import BaseCard from '@components/common/BaseCard.vue'
import BaseButton from '@components/common/BaseButton.vue'
</script>

<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold text-gray-900">Scan Wine Label</h1>

    <BaseCard class="text-center py-12">
      <img src="/images/icons/reshot-icon-wine-bottle-and-cup-375YBXTJSG.svg" alt="Scan Wine" class="w-24 h-24 mx-auto mb-6" />
      <h2 class="text-xl font-semibold text-gray-900 mb-2">
        Scan a wine label
      </h2>
      <p class="text-gray-600 mb-6 max-w-md mx-auto">
        Point your camera at a wine label to get instant information, reviews, and recommendations.
      </p>
      <BaseButton size="lg">
        Open Camera
      </BaseButton>
    </BaseCard>

    <BaseCard>
      <h3 class="font-semibold text-gray-900 mb-4">How it works</h3>
      <ol class="space-y-4">
        <li class="flex gap-4">
          <span class="flex-shrink-0 w-8 h-8 bg-wine-100 text-wine-600 rounded-full flex items-center justify-center font-semibold">
            1
          </span>
          <div>
            <p class="font-medium text-gray-900">Point your camera at the wine label</p>
            <p class="text-sm text-gray-600">Make sure the label is clearly visible and well-lit</p>
          </div>
        </li>
        <li class="flex gap-4">
          <span class="flex-shrink-0 w-8 h-8 bg-wine-100 text-wine-600 rounded-full flex items-center justify-center font-semibold">
            2
          </span>
          <div>
            <p class="font-medium text-gray-900">We'll identify the wine</p>
            <p class="text-sm text-gray-600">Our AI recognizes thousands of wine labels</p>
          </div>
        </li>
        <li class="flex gap-4">
          <span class="flex-shrink-0 w-8 h-8 bg-wine-100 text-wine-600 rounded-full flex items-center justify-center font-semibold">
            3
          </span>
          <div>
            <p class="font-medium text-gray-900">Get instant information</p>
            <p class="text-sm text-gray-600">View details, ratings, reviews, and food pairings</p>
          </div>
        </li>
      </ol>
    </BaseCard>
  </div>
</template>


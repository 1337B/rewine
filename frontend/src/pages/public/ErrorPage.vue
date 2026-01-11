<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import BaseButton from '@components/common/BaseButton.vue'

const { t } = useI18n()

function handleReload() {
  window.location.reload()
}
</script>

<template>
  <div class="min-h-[60vh] flex flex-col items-center justify-center text-center px-4">
    <div class="w-24 h-24 mb-6">
      <img
        src="/images/icons/reshot-icon-barrel-58VZT4EFAQ.svg"
        alt="Server Error"
        class="w-full h-full opacity-60"
      />
    </div>
    <h1 class="text-5xl font-bold text-gray-900 mb-4">500</h1>
    <h2 class="text-2xl font-semibold text-gray-700 mb-2">
      {{ t('errors.serverError') }}
    </h2>
    <p class="text-lg text-gray-600 mb-8 max-w-md">
      {{ t('errors.serverErrorDescription') }}
    </p>
    <div class="flex flex-wrap gap-4 justify-center">
      <BaseButton size="lg" @click="handleReload">
        {{ t('common.tryAgain') }}
      </BaseButton>
      <router-link to="/">
        <BaseButton variant="outline" size="lg">
          {{ t('common.goHome') }}
        </BaseButton>
      </router-link>
    </div>
  </div>
</template>


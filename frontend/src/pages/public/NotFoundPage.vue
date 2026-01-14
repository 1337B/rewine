<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import BaseButton from '@components/common/BaseButton.vue'

const { t } = useI18n()
</script>

<template>
  <div class="min-h-[60vh] flex flex-col items-center justify-center text-center px-4">
    <div class="w-24 h-24 mb-6">
      <img
        src="/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg"
        alt="Not Found"
        class="w-full h-full opacity-60"
      />
    </div>
    <h1 class="text-5xl font-bold text-gray-900 mb-4">404</h1>
    <h2 class="text-2xl font-semibold text-gray-700 mb-2">
      {{ t('errors.notFound') }}
    </h2>
    <p class="text-lg text-gray-600 mb-8 max-w-md">
      {{ t('errors.notFoundDescription') }}
    </p>
    <router-link to="/">
      <BaseButton size="lg">{{ t('common.goHome') }}</BaseButton>
    </router-link>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import BaseButton from '@components/common/BaseButton.vue'

const { t } = useI18n()
</script>

<template>
  <div class="min-h-[60vh] flex flex-col items-center justify-center text-center px-4">
    <div class="w-24 h-24 mb-6">
      <img
        src="/images/icons/reshot-icon-cork-KXC7J2FAHZ.svg"
        alt="Access Denied"
        class="w-full h-full opacity-60"
      />
    </div>
    <h1 class="text-5xl font-bold text-gray-900 mb-4">403</h1>
    <h2 class="text-2xl font-semibold text-gray-700 mb-2">
      {{ t('errors.forbidden') }}
    </h2>
    <p class="text-lg text-gray-600 mb-8 max-w-md">
      {{ t('errors.forbiddenDescription') }}
    </p>
    <div class="flex flex-wrap gap-4 justify-center">
      <router-link to="/">
        <BaseButton size="lg">
          {{ t('common.goHome') }}
        </BaseButton>
      </router-link>
      <router-link to="/login">
        <BaseButton variant="outline" size="lg">
          {{ t('auth.login') }}
        </BaseButton>
      </router-link>
    </div>
  </div>
</template>


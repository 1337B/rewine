<script setup lang="ts">
import BaseButton from '@components/common/BaseButton.vue'
</script>

<template>
  <div class="min-h-[60vh] flex flex-col items-center justify-center text-center">
    <div class="text-6xl mb-6">🍷</div>
    <h1 class="text-4xl font-bold text-gray-900 mb-4">404</h1>
    <p class="text-xl text-gray-600 mb-8">
      Oops! This page seems to have disappeared like wine at a party.
    </p>
    <router-link to="/">
      <BaseButton size="lg">Go Home</BaseButton>
    </router-link>
  </div>
</template>


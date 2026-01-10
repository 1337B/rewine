<script setup lang="ts">
import BaseCard from '@components/common/BaseCard.vue'
import BaseButton from '@components/common/BaseButton.vue'
</script>

<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-bold text-gray-900">Manage Wines</h1>
      <BaseButton>Add Wine</BaseButton>
    </div>

    <BaseCard>
      <p class="text-gray-500">Wine management interface coming soon...</p>
    </BaseCard>
  </div>
</template>


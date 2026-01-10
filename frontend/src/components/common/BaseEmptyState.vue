<script setup lang="ts">
interface Props {
  title?: string
  description?: string
  icon?: 'search' | 'folder' | 'wine' | 'calendar' | 'map' | 'cellar'
}

withDefaults(defineProps<Props>(), {
  title: 'No results found',
  description: 'Try adjusting your search or filters',
  icon: 'search',
})

const iconMap: Record<string, string> = {
  search: '/images/icons/reshot-icon-enology-MW9AB2GQPR.svg',
  folder: '/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg',
  wine: '/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg',
  calendar: '/images/icons/reshot-icon-toast-65A7YV3EXC.svg',
  map: '/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg',
  cellar: '/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg',
}
</script>

<template>
  <div class="flex flex-col items-center justify-center py-12 px-4 text-center">
    <!-- Icon -->
    <div class="mb-4">
      <img
        :src="iconMap[icon]"
        :alt="title"
        class="h-20 w-20 opacity-40"
      />
    </div>

    <h3 class="text-lg font-medium text-gray-900 mb-1">
      {{ title }}
    </h3>

    <p class="text-sm text-gray-500 max-w-sm">
      {{ description }}
    </p>

    <div v-if="$slots.action" class="mt-6">
      <slot name="action" />
    </div>
  </div>
</template>


<script setup lang="ts">
/**
 * BaseEmptyState - Empty state placeholder component
 *
 * Displays a friendly message when there's no data to show.
 * Includes an icon, title, description, and optional action slot.
 */

import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

interface Props {
  /** Title text or i18n key */
  title?: string
  /** Description text or i18n key */
  description?: string
  /** Icon type */
  icon?: 'search' | 'folder' | 'wine' | 'calendar' | 'map' | 'cellar' | 'error' | 'empty'
  /** Custom icon URL (overrides icon prop) */
  iconUrl?: string
  /** Size variant */
  size?: 'sm' | 'md' | 'lg'
}

const props = withDefaults(defineProps<Props>(), {
  title: 'No results found',
  description: 'Try adjusting your search or filters',
  icon: 'search',
  iconUrl: undefined,
  size: 'md',
})

const { t, te } = useI18n()

// Resolve text with i18n support
function resolveText(text: string): string {
  return te(text) ? t(text) : text
}

const resolvedTitle = computed(() => resolveText(props.title))
const resolvedDescription = computed(() => resolveText(props.description))

const iconMap: Record<string, string> = {
  search: '/images/icons/reshot-icon-enology-MW9AB2GQPR.svg',
  folder: '/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg',
  wine: '/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg',
  calendar: '/images/icons/reshot-icon-toast-65A7YV3EXC.svg',
  map: '/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg',
  cellar: '/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg',
  error: '/images/icons/reshot-icon-barrel-58VZT4EFAQ.svg',
  empty: '/images/icons/reshot-icon-wine-bottle-T9X8JUFM32.svg',
}

const iconSrc = computed(() => props.iconUrl ?? iconMap[props.icon])

const sizeClasses = {
  sm: {
    container: 'py-6 px-4',
    icon: 'h-12 w-12',
    title: 'text-base',
    description: 'text-sm',
  },
  md: {
    container: 'py-12 px-4',
    icon: 'h-20 w-20',
    title: 'text-lg',
    description: 'text-sm',
  },
  lg: {
    container: 'py-16 px-4',
    icon: 'h-28 w-28',
    title: 'text-xl',
    description: 'text-base',
  },
}
</script>

<template>
  <div :class="['flex flex-col items-center justify-center text-center', sizeClasses[size].container]">
    <!-- Icon -->
    <div class="mb-4">
      <img
        :src="iconSrc"
        :alt="resolvedTitle"
        :class="['opacity-40', sizeClasses[size].icon]"
      />
    </div>

    <h3 :class="['font-medium text-gray-900 mb-1', sizeClasses[size].title]">
      {{ resolvedTitle }}
    </h3>

    <p :class="['text-gray-500 max-w-sm', sizeClasses[size].description]">
      {{ resolvedDescription }}
    </p>

    <div v-if="$slots.action" class="mt-6">
      <slot name="action" />
    </div>
  </div>
</template>


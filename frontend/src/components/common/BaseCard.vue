<script setup lang="ts">
interface Props {
  padding?: 'none' | 'sm' | 'md' | 'lg'
  shadow?: 'none' | 'sm' | 'md' | 'lg'
  rounded?: 'none' | 'sm' | 'md' | 'lg' | 'full'
  hoverable?: boolean
}

withDefaults(defineProps<Props>(), {
  padding: 'md',
  shadow: 'sm',
  rounded: 'lg',
  hoverable: false,
})

const paddingClasses = {
  none: '',
  sm: 'p-3',
  md: 'p-4',
  lg: 'p-6',
}

const shadowClasses = {
  none: '',
  sm: 'shadow-sm',
  md: 'shadow-md',
  lg: 'shadow-lg',
}

const roundedClasses = {
  none: '',
  sm: 'rounded-sm',
  md: 'rounded-md',
  lg: 'rounded-lg',
  full: 'rounded-full',
}
</script>

<template>
  <div
    :class="[
      'bg-white border border-gray-200',
      paddingClasses[padding],
      shadowClasses[shadow],
      roundedClasses[rounded],
      hoverable && 'hover:shadow-md transition-shadow cursor-pointer',
    ]"
  >
    <slot />
  </div>
</template>


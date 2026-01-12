<script setup lang="ts">
/**
 * BaseSkeleton - Loading placeholder component
 *
 * Displays an animated skeleton placeholder while content is loading.
 * Supports various shapes and sizes for different UI elements.
 */

interface Props {
  /** Skeleton variant type */
  variant?: 'text' | 'circular' | 'rectangular' | 'rounded'
  /** Width (CSS value or Tailwind class) */
  width?: string
  /** Height (CSS value or Tailwind class) */
  height?: string
  /** Number of skeleton lines (for text variant) */
  lines?: number
  /** Animation type */
  animation?: 'pulse' | 'wave' | 'none'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'text',
  width: '100%',
  height: 'auto',
  lines: 1,
  animation: 'pulse',
})

const variantClasses = {
  text: 'rounded',
  circular: 'rounded-full',
  rectangular: 'rounded-none',
  rounded: 'rounded-lg',
}

const animationClasses = {
  pulse: 'animate-pulse',
  wave: 'animate-shimmer',
  none: '',
}

// Default heights for text variant
const textHeights = {
  1: 'h-4',
  default: 'h-4',
}

function getHeightClass(index: number): string {
  if (props.height !== 'auto') return ''
  if (props.variant !== 'text') return 'h-full'
  // Last line is shorter for text variant
  return index === props.lines - 1 && props.lines > 1 ? 'h-4 w-3/4' : 'h-4'
}
</script>

<template>
  <div
    v-if="variant === 'text' && lines > 1"
    class="space-y-2"
    :style="{ width }"
  >
    <div
      v-for="i in lines"
      :key="i"
      :class="[
        'bg-gray-200',
        animationClasses[animation],
        variantClasses[variant],
        getHeightClass(i - 1),
      ]"
      :style="{ height: height !== 'auto' ? height : undefined }"
    />
  </div>
  <div
    v-else
    :class="[
      'bg-gray-200',
      animationClasses[animation],
      variantClasses[variant],
      variant === 'circular' ? 'aspect-square' : '',
    ]"
    :style="{
      width,
      height: height !== 'auto' ? height : (variant === 'text' ? '1rem' : undefined),
    }"
  />
</template>

<style scoped>
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.animate-shimmer {
  background: linear-gradient(
    90deg,
    theme('colors.gray.200') 0%,
    theme('colors.gray.100') 50%,
    theme('colors.gray.200') 100%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
}
</style>


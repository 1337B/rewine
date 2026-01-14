<script setup lang="ts">
/**
 * BaseButton - Reusable button component
 *
 * Features:
 * - Multiple variants (primary, secondary, outline, ghost, danger)
 * - Multiple sizes (sm, md, lg)
 * - Loading state with spinner
 * - Disabled state
 * - Full width option
 * - Icon support (left/right slots)
 */

import { computed } from 'vue'

interface Props {
  /** Button visual variant */
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger'
  /** Button size */
  size?: 'sm' | 'md' | 'lg'
  /** Disable the button */
  disabled?: boolean
  /** Show loading spinner */
  loading?: boolean
  /** Button type attribute */
  type?: 'button' | 'submit' | 'reset'
  /** Make button full width */
  fullWidth?: boolean
  /** Accessibility label */
  ariaLabel?: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
  type: 'button',
  fullWidth: false,
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const isDisabled = computed(() => props.disabled || props.loading)

const variantClasses: Record<string, string> = {
  primary: 'bg-wine-600 text-white hover:bg-wine-700 focus:ring-wine-500 active:bg-wine-800',
  secondary: 'bg-gray-600 text-white hover:bg-gray-700 focus:ring-gray-500 active:bg-gray-800',
  outline: 'border-2 border-wine-600 text-wine-600 hover:bg-wine-50 focus:ring-wine-500 active:bg-wine-100',
  ghost: 'text-wine-600 hover:bg-wine-50 focus:ring-wine-500 active:bg-wine-100',
  danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500 active:bg-red-800',
}

const sizeClasses: Record<string, string> = {
  sm: 'px-3 py-1.5 text-sm gap-1.5',
  md: 'px-4 py-2 text-base gap-2',
  lg: 'px-6 py-3 text-lg gap-2.5',
}

const spinnerSizeClasses: Record<string, string> = {
  sm: 'h-3.5 w-3.5',
  md: 'h-4 w-4',
  lg: 'h-5 w-5',
}

const buttonClasses = computed(() => [
  'inline-flex items-center justify-center font-medium rounded-lg',
  'transition-colors duration-150',
  'focus:outline-none focus:ring-2 focus:ring-offset-2',
  'disabled:opacity-50 disabled:cursor-not-allowed',
  variantClasses[props.variant],
  sizeClasses[props.size],
  props.fullWidth && 'w-full',
])

function handleClick(event: MouseEvent) {
  if (!isDisabled.value) {
    emit('click', event)
  }
}
</script>

<template>
  <button
    :type="type"
    :disabled="isDisabled"
    :class="buttonClasses"
    :aria-label="ariaLabel"
    :aria-busy="loading"
    @click="handleClick"
  >
    <!-- Loading spinner -->
    <svg
      v-if="loading"
      :class="['animate-spin', spinnerSizeClasses[size]]"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <circle
        class="opacity-25"
        cx="12"
        cy="12"
        r="10"
        stroke="currentColor"
        stroke-width="4"
      />
      <path
        class="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
      />
    </svg>

    <!-- Left icon slot -->
    <span v-if="$slots.iconLeft && !loading" class="flex-shrink-0">
      <slot name="iconLeft" />
    </span>

    <!-- Button content -->
    <span :class="{ 'opacity-0': loading && !$slots.default }">
      <slot />
    </span>

    <!-- Right icon slot -->
    <span v-if="$slots.iconRight" class="flex-shrink-0">
      <slot name="iconRight" />
    </span>
  </button>
</template>


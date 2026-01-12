<script setup lang="ts">
/**
 * BaseInput - Reusable form input component
 *
 * Features:
 * - Multiple input types
 * - Label and placeholder support
 * - Error message display
 * - Hint text support
 * - Disabled state
 * - Auto-trim on blur
 * - Accessibility attributes
 */

import { computed, ref } from 'vue'

interface Props {
  /** Input value (v-model) */
  modelValue: string | number | null
  /** Input type */
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'search'
  /** Input label */
  label?: string
  /** Placeholder text */
  placeholder?: string
  /** Error message to display */
  error?: string
  /** Hint text below input */
  hint?: string
  /** Disable the input */
  disabled?: boolean
  /** Mark as required */
  required?: boolean
  /** Input ID (auto-generated if not provided) */
  id?: string
  /** Input name */
  name?: string
  /** Autocomplete attribute */
  autocomplete?: string
  /** Auto-trim whitespace on blur */
  autoTrim?: boolean
  /** Minimum value (for number type) */
  min?: number
  /** Maximum value (for number type) */
  max?: number
  /** Step value (for number type) */
  step?: number
  /** Maximum length */
  maxlength?: number
  /** Input size variant */
  size?: 'sm' | 'md' | 'lg'
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  label: '',
  placeholder: '',
  error: '',
  hint: '',
  disabled: false,
  required: false,
  autoTrim: true,
  size: 'md',
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
  blur: [event: FocusEvent]
  focus: [event: FocusEvent]
  enter: [event: KeyboardEvent]
}>()

const inputRef = ref<HTMLInputElement | null>(null)
const isFocused = ref(false)

const inputId = computed(() =>
  props.id || `input-${Math.random().toString(36).slice(2, 9)}`
)

const errorId = computed(() => `${inputId.value}-error`)
const hintId = computed(() => `${inputId.value}-hint`)

const hasError = computed(() => !!props.error)

const describedBy = computed(() => {
  const ids: string[] = []
  if (hasError.value) ids.push(errorId.value)
  else if (props.hint) ids.push(hintId.value)
  return ids.length > 0 ? ids.join(' ') : undefined
})

const sizeClasses = {
  sm: 'px-2.5 py-1.5 text-sm',
  md: 'px-3 py-2 text-base',
  lg: 'px-4 py-3 text-lg',
}

const inputClasses = computed(() => [
  'block w-full rounded-lg border transition-colors',
  'text-gray-900 placeholder:text-gray-400',
  'focus:outline-none focus:ring-2 focus:ring-offset-0',
  sizeClasses[props.size],
  hasError.value
    ? 'border-red-300 focus:border-red-500 focus:ring-red-500'
    : 'border-gray-300 focus:border-wine-500 focus:ring-wine-500',
  props.disabled && 'bg-gray-100 cursor-not-allowed opacity-60',
])

function handleInput(event: Event) {
  const target = event.target as HTMLInputElement
  const value = props.type === 'number' ? Number(target.value) : target.value
  emit('update:modelValue', value)
}

function handleBlur(event: FocusEvent) {
  isFocused.value = false

  // Auto-trim on blur for text inputs
  if (props.autoTrim && typeof props.modelValue === 'string') {
    const trimmed = props.modelValue.trim()
    if (trimmed !== props.modelValue) {
      emit('update:modelValue', trimmed)
    }
  }

  emit('blur', event)
}

function handleFocus(event: FocusEvent) {
  isFocused.value = true
  emit('focus', event)
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter') {
    emit('enter', event)
  }
}

// Expose focus method for parent components
function focus() {
  inputRef.value?.focus()
}

function blur() {
  inputRef.value?.blur()
}

defineExpose({ focus, blur })
</script>

<template>
  <div class="space-y-1">
    <!-- Label -->
    <label
      v-if="label"
      :for="inputId"
      class="block text-sm font-medium text-gray-700"
    >
      {{ label }}
      <span v-if="required" class="text-red-500 ml-0.5">*</span>
    </label>

    <!-- Input -->
    <input
      :id="inputId"
      ref="inputRef"
      :type="type"
      :value="modelValue"
      :name="name"
      :placeholder="placeholder"
      :disabled="disabled"
      :required="required"
      :autocomplete="autocomplete"
      :min="min"
      :max="max"
      :step="step"
      :maxlength="maxlength"
      :class="inputClasses"
      :aria-invalid="hasError"
      :aria-describedby="describedBy"
      @input="handleInput"
      @blur="handleBlur"
      @focus="handleFocus"
      @keydown="handleKeydown"
    />

    <!-- Error message -->
    <p
      v-if="hasError"
      :id="errorId"
      class="text-sm text-red-600 flex items-center gap-1"
      role="alert"
    >
      <svg class="w-4 h-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
        <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
      </svg>
      {{ error }}
    </p>

    <!-- Hint text -->
    <p
      v-else-if="hint"
      :id="hintId"
      class="text-sm text-gray-500"
    >
      {{ hint }}
    </p>
  </div>
</template>


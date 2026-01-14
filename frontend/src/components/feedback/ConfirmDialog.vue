<script setup lang="ts">
/**
 * ConfirmDialog - Global confirmation dialog component
 *
 * Can be used in two ways:
 * 1. Controlled mode (v-model) - Use props to control
 * 2. Store mode - Use useConfirm composable for promise-based API
 */

import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUiStore } from '@stores/ui.store'
import BaseModal from '@components/common/BaseModal.vue'
import BaseButton from '@components/common/BaseButton.vue'

interface Props {
  /** Controlled mode: dialog visibility */
  modelValue?: boolean
  /** Dialog title (overrides store) */
  title?: string
  /** Dialog message (overrides store) */
  message?: string
  /** Confirm button text */
  confirmText?: string
  /** Cancel button text */
  cancelText?: string
  /** Confirm button variant */
  confirmVariant?: 'primary' | 'danger'
  /** Loading state */
  loading?: boolean
  /** Use store mode (global confirm dialog) */
  useStore?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  title: undefined,
  message: undefined,
  confirmText: undefined,
  cancelText: undefined,
  confirmVariant: undefined,
  loading: undefined,
  useStore: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
  cancel: []
}>()

const { t, te } = useI18n()
const uiStore = useUiStore()

// Determine if we're in controlled or store mode
const isStoreMode = computed(() => props.useStore || props.modelValue === undefined)

// Computed state based on mode
const isOpen = computed(() => {
  if (isStoreMode.value) {
    return uiStore.confirmState.isOpen
  }
  return props.modelValue ?? false
})

const isLoading = computed(() => {
  if (isStoreMode.value) {
    return uiStore.confirmState.loading
  }
  return props.loading ?? false
})

// Resolve text with i18n support
function resolveText(
  propValue: string | undefined,
  storeKey: string | undefined,
  storeValue: string | undefined,
  fallback: string
): string {
  // Props take precedence
  if (propValue) return propValue

  // Store i18n key
  if (storeKey && te(storeKey)) {
    return t(storeKey)
  }

  // Store plain text
  if (storeValue) return storeValue

  // Fallback
  return fallback
}

const dialogTitle = computed(() =>
  resolveText(
    props.title,
    uiStore.confirmState.options.titleKey,
    uiStore.confirmState.options.title,
    t('common.confirm')
  )
)

const dialogMessage = computed(() =>
  resolveText(
    props.message,
    uiStore.confirmState.options.messageKey,
    uiStore.confirmState.options.message,
    t('common.confirmMessage')
  )
)

const confirmButtonText = computed(() =>
  resolveText(
    props.confirmText,
    uiStore.confirmState.options.confirmTextKey,
    uiStore.confirmState.options.confirmText,
    t('common.confirm')
  )
)

const cancelButtonText = computed(() =>
  resolveText(
    props.cancelText,
    uiStore.confirmState.options.cancelTextKey,
    uiStore.confirmState.options.cancelText,
    t('common.cancel')
  )
)

const buttonVariant = computed(() =>
  props.confirmVariant ?? uiStore.confirmState.options.confirmVariant ?? 'primary'
)

// Actions
function handleClose() {
  if (isStoreMode.value) {
    uiStore.handleCancel()
  } else {
    emit('update:modelValue', false)
    emit('cancel')
  }
}

function handleConfirm() {
  if (isStoreMode.value) {
    uiStore.handleConfirm()
  } else {
    emit('confirm')
  }
}

function handleCancel() {
  handleClose()
}
</script>

<template>
  <BaseModal
    :model-value="isOpen"
    :title="dialogTitle"
    size="sm"
    :close-on-overlay="!isLoading"
    :close-on-escape="!isLoading"
    @update:model-value="handleClose"
  >
    <p class="text-gray-600">{{ dialogMessage }}</p>

    <template #footer>
      <div class="flex justify-end gap-3">
        <BaseButton
          variant="ghost"
          :disabled="isLoading"
          @click="handleCancel"
        >
          {{ cancelButtonText }}
        </BaseButton>
        <BaseButton
          :variant="buttonVariant"
          :loading="isLoading"
          @click="handleConfirm"
        >
          {{ confirmButtonText }}
        </BaseButton>
      </div>
    </template>
  </BaseModal>
</template>


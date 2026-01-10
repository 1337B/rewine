<script setup lang="ts">
import BaseModal from '@components/common/BaseModal.vue'
import BaseButton from '@components/common/BaseButton.vue'

interface Props {
  modelValue: boolean
  title?: string
  message?: string
  confirmText?: string
  cancelText?: string
  confirmVariant?: 'primary' | 'danger'
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Confirm Action',
  message: 'Are you sure you want to proceed?',
  confirmText: 'Confirm',
  cancelText: 'Cancel',
  confirmVariant: 'primary',
  loading: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
  cancel: []
}>()

function close() {
  emit('update:modelValue', false)
}

function handleConfirm() {
  emit('confirm')
}

function handleCancel() {
  emit('cancel')
  close()
}
</script>

<template>
  <BaseModal
    :model-value="modelValue"
    :title="title"
    size="sm"
    :close-on-overlay="!loading"
    :close-on-escape="!loading"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <p class="text-gray-600">{{ message }}</p>

    <template #footer>
      <div class="flex justify-end gap-3">
        <BaseButton
          variant="ghost"
          :disabled="loading"
          @click="handleCancel"
        >
          {{ cancelText }}
        </BaseButton>
        <BaseButton
          :variant="confirmVariant"
          :loading="loading"
          @click="handleConfirm"
        >
          {{ confirmText }}
        </BaseButton>
      </div>
    </template>
  </BaseModal>
</template>


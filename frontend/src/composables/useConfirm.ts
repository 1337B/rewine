/**
 * Confirm dialog composable
 *
 * Provides a promise-based API for confirmation dialogs.
 * Works with the global ConfirmDialog component in AppShell.
 */

import { useUiStore } from '@stores/ui.store'
import type { ConfirmOptions } from '@stores/ui.store'

export interface UseConfirmOptions {
  /** i18n key for the dialog title */
  titleKey?: string
  /** Plain text title (used if titleKey not provided) */
  title?: string
  /** i18n key for the dialog message */
  messageKey?: string
  /** Plain text message (used if messageKey not provided) */
  message?: string
  /** i18n key for confirm button text */
  confirmTextKey?: string
  /** Plain text for confirm button */
  confirmText?: string
  /** i18n key for cancel button text */
  cancelTextKey?: string
  /** Plain text for cancel button */
  cancelText?: string
  /** Button variant for confirm action */
  confirmVariant?: 'primary' | 'danger'
}

/**
 * Confirm dialog composable
 */
export function useConfirm() {
  const uiStore = useUiStore()

  /**
   * Open a confirmation dialog and return a promise
   * Resolves to true if confirmed, false if cancelled
   */
  function confirm(options: UseConfirmOptions = {}): Promise<boolean> {
    return new Promise((resolve) => {
      const confirmOptions: ConfirmOptions = {
        title: options.title,
        titleKey: options.titleKey,
        message: options.message,
        messageKey: options.messageKey,
        confirmText: options.confirmText,
        confirmTextKey: options.confirmTextKey,
        cancelText: options.cancelText,
        cancelTextKey: options.cancelTextKey,
        confirmVariant: options.confirmVariant ?? 'primary',
        onConfirm: () => {
          resolve(true)
        },
        onCancel: () => {
          resolve(false)
        },
      }

      uiStore.openConfirm(confirmOptions)
    })
  }

  /**
   * Shorthand for danger confirmation (e.g., delete actions)
   */
  function confirmDanger(options: Omit<UseConfirmOptions, 'confirmVariant'> = {}): Promise<boolean> {
    return confirm({
      ...options,
      confirmVariant: 'danger',
    })
  }

  /**
   * Shorthand for delete confirmation
   */
  function confirmDelete(itemName?: string): Promise<boolean> {
    return confirmDanger({
      titleKey: 'common.confirmDelete',
      title: 'Delete',
      messageKey: itemName ? undefined : 'common.confirmDeleteMessage',
      message: itemName
        ? `Are you sure you want to delete "${itemName}"? This action cannot be undone.`
        : 'Are you sure you want to delete this item? This action cannot be undone.',
      confirmTextKey: 'common.delete',
      confirmText: 'Delete',
    })
  }

  /**
   * Close the confirm dialog programmatically
   */
  function close() {
    uiStore.closeConfirm()
  }

  return {
    confirm,
    confirmDanger,
    confirmDelete,
    close,
  }
}

export default useConfirm


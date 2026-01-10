import type { Directive } from 'vue'

/**
 * Auto-focus directive
 * Usage: v-focus or v-focus="condition"
 */
export const vFocus: Directive<HTMLElement, boolean | undefined> = {
  mounted(el, binding) {
    if (binding.value !== false) {
      el.focus()
    }
  },
  updated(el, binding) {
    if (binding.value && !binding.oldValue) {
      el.focus()
    }
  },
}

export default vFocus


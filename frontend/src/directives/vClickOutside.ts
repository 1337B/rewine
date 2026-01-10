import type { Directive, DirectiveBinding } from 'vue'

interface ClickOutsideElement extends HTMLElement {
  _clickOutside?: {
    handler: (event: MouseEvent) => void
    exclude: string[]
  }
}

/**
 * Click outside directive
 * Usage: v-click-outside="handler" or v-click-outside="{ handler, exclude: ['.selector'] }"
 */
export const vClickOutside: Directive<ClickOutsideElement> = {
  mounted(el, binding: DirectiveBinding) {
    const { value } = binding

    let handler: (event: MouseEvent) => void
    let exclude: string[] = []

    if (typeof value === 'function') {
      handler = value
    } else if (typeof value === 'object' && value.handler) {
      handler = value.handler
      exclude = value.exclude || []
    } else {
      return
    }

    const clickHandler = (event: MouseEvent) => {
      const target = event.target as Node

      if (el.contains(target)) {
        return
      }

      for (const selector of exclude) {
        const excludedEl = document.querySelector(selector)
        if (excludedEl && excludedEl.contains(target)) {
          return
        }
      }

      handler(event)
    }

    el._clickOutside = {
      handler: clickHandler,
      exclude,
    }

    document.addEventListener('click', clickHandler, true)
  },

  unmounted(el) {
    if (el._clickOutside) {
      document.removeEventListener('click', el._clickOutside.handler, true)
      delete el._clickOutside
    }
  },
}

export default vClickOutside


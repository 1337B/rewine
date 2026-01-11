<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUiStore } from '@stores/ui.store'
import { useAuthStore } from '@stores/auth.store'
import { navigationItems, type NavigationItem } from '@config/navigation'
import { filterNavigationByRoles } from '@utils/guard'

const { t } = useI18n()
const route = useRoute()
const uiStore = useUiStore()
const authStore = useAuthStore()

const isOpen = computed(() => uiStore.sidebarOpen)
const isAuthenticated = computed(() => authStore.isAuthenticated)
const userRoles = computed(() => authStore.user?.roles)

// Filter navigation items based on authentication and roles
const visibleNavItems = computed<NavigationItem[]>(() => {
  return filterNavigationByRoles(
    navigationItems,
    isAuthenticated.value,
    userRoles.value
  )
})

function isActive(item: NavigationItem): boolean {
  if (item.exact) {
    return route.path === item.path
  }
  return route.path.startsWith(item.path)
}

function closeSidebar() {
  uiStore.closeSidebar()
}
</script>

<template>
  <!-- Overlay for mobile -->
  <Transition name="fade">
    <div
      v-if="isOpen"
      class="fixed inset-0 bg-black/50 z-40"
      @click="closeSidebar"
    />
  </Transition>

  <!-- Mobile Sidebar -->
  <aside
    :class="[
      'fixed top-16 left-0 z-40 h-[calc(100vh-4rem)] w-72 bg-white border-r border-gray-200 shadow-xl transform transition-transform duration-300 ease-in-out',
      isOpen ? 'translate-x-0' : '-translate-x-full',
    ]"
  >
    <nav class="h-full overflow-y-auto py-4">
      <ul class="space-y-1 px-3">
        <li v-for="item in visibleNavItems" :key="item.path">
          <router-link
            :to="item.path"
            :class="[
              'flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200',
              isActive(item)
                ? 'bg-wine-50 text-wine-700'
                : 'text-gray-700 hover:bg-gray-50',
            ]"
            @click="closeSidebar"
          >
            <img :src="item.icon" :alt="t(item.labelKey)" class="w-6 h-6" />
            <span>{{ t(item.labelKey) }}</span>
          </router-link>
        </li>
      </ul>

      <!-- Login/Register for mobile -->
      <div v-if="!isAuthenticated" class="mt-6 px-3 space-y-2">
        <router-link
          to="/login"
          class="block w-full px-4 py-3 text-center text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-xl transition-colors"
          @click="closeSidebar"
        >
          {{ t('auth.login') }}
        </router-link>
        <router-link
          to="/register"
          class="block w-full px-4 py-3 text-center text-sm font-medium text-white bg-wine-600 hover:bg-wine-700 rounded-xl transition-colors"
          @click="closeSidebar"
        >
          {{ t('auth.register') }}
        </router-link>
      </div>
    </nav>
  </aside>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

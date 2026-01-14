<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@stores/auth.store'
import { useUiStore } from '@stores/ui.store'
import { setLocale, getCurrentLocale, type SupportedLocale } from '@i18n/index'
import { navigationItems, type NavigationItem } from '@config/navigation'
import { filterNavigationByRoles } from '@utils/guard'
import BaseButton from '@components/common/BaseButton.vue'

const { t } = useI18n()
const route = useRoute()
const authStore = useAuthStore()
const uiStore = useUiStore()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const user = computed(() => authStore.user)
const userRoles = computed(() => authStore.user?.roles)

// Language selector
const currentLocale = ref<SupportedLocale>(getCurrentLocale())
const showLanguageMenu = ref(false)
const showUserMenu = ref(false)

const languages = [
  { code: 'en-US' as SupportedLocale, label: 'English', flag: '🇺🇸' },
  { code: 'es-AR' as SupportedLocale, label: 'Español', flag: '🇦🇷' },
]

const currentLanguage = computed(() =>
  languages.find(l => l.code === currentLocale.value) || languages[0]
)

function changeLanguage(locale: SupportedLocale) {
  currentLocale.value = locale
  setLocale(locale)
  showLanguageMenu.value = false
}

// Helper functions for delayed menu close
function delayedCloseLanguageMenu() {
  setTimeout(() => { showLanguageMenu.value = false }, 150)
}

function delayedCloseUserMenu() {
  setTimeout(() => { showUserMenu.value = false }, 150)
}

// Filter navigation items based on authentication and roles
const visibleNavItems = computed<NavigationItem[]>(() => {
  return filterNavigationByRoles(
    navigationItems,
    isAuthenticated.value,
    userRoles.value
  )
})

function isActive(path: string, exact?: boolean): boolean {
  if (exact) return route.path === path
  return route.path.startsWith(path)
}

function toggleSidebar() {
  uiStore.toggleSidebar()
}

async function handleLogout() {
  await authStore.logout()
  showUserMenu.value = false
}
</script>

<template>
  <header class="sticky top-0 z-50 bg-white border-b border-gray-100 shadow-sm">
    <div class="max-w-7xl mx-auto">
      <div class="flex items-center justify-between h-16 px-4 sm:px-6 lg:px-8">
        <!-- Left: Mobile menu & Logo -->
        <div class="flex items-center gap-6">
          <!-- Mobile menu button -->
          <button
            type="button"
            class="lg:hidden p-2 text-gray-500 hover:text-gray-700 rounded-lg hover:bg-gray-100"
            @click="toggleSidebar"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>

          <!-- Logo -->
          <router-link to="/" class="flex items-center gap-2 group">
            <img src="/images/icons/reshot-icon-grapes-DH3FRP42X5.svg" alt="Rewine" class="w-8 h-8" />
            <span class="text-2xl font-bold text-wine-700 group-hover:text-wine-800 transition-colors">rewine</span>
          </router-link>

          <!-- Desktop Navigation -->
          <nav class="hidden lg:flex items-center gap-1 ml-8">
            <router-link
              v-for="item in visibleNavItems"
              :key="item.path"
              :to="item.path"
              :class="[
                'flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-lg transition-all duration-200',
                isActive(item.path, item.exact)
                  ? 'text-wine-700 bg-wine-50'
                  : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
              ]"
            >
              <img :src="item.icon" :alt="t(item.labelKey)" class="w-5 h-5" />
              <span>{{ t(item.labelKey) }}</span>
            </router-link>
          </nav>
        </div>

        <!-- Right: Search, Language, User -->
        <div class="flex items-center gap-3">
          <!-- Search (placeholder) -->
          <div class="hidden md:flex items-center">
            <div class="relative">
              <input
                type="text"
                :placeholder="t('common.search') + '...'"
                class="w-64 pl-10 pr-4 py-2 text-sm bg-gray-50 border border-gray-200 rounded-full focus:outline-none focus:ring-2 focus:ring-wine-500 focus:border-transparent transition-all"
              />
              <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>

          <!-- Language Selector -->
          <div class="relative">
            <button
              type="button"
              class="flex items-center gap-2 px-3 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
              @click="showLanguageMenu = !showLanguageMenu"
              @blur="delayedCloseLanguageMenu"
            >
              <span class="text-lg">{{ currentLanguage.flag }}</span>
              <span class="hidden sm:inline">{{ currentLanguage.label }}</span>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            <!-- Language Dropdown -->
            <Transition
              enter-active-class="transition ease-out duration-100"
              enter-from-class="transform opacity-0 scale-95"
              enter-to-class="transform opacity-100 scale-100"
              leave-active-class="transition ease-in duration-75"
              leave-from-class="transform opacity-100 scale-100"
              leave-to-class="transform opacity-0 scale-95"
            >
              <div
                v-if="showLanguageMenu"
                class="absolute right-0 mt-2 w-40 bg-white rounded-lg shadow-lg border border-gray-100 py-1 z-50"
              >
                <button
                  v-for="lang in languages"
                  :key="lang.code"
                  type="button"
                  class="w-full flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                  :class="{ 'bg-wine-50 text-wine-700': currentLocale === lang.code }"
                  @mousedown.prevent="changeLanguage(lang.code)"
                >
                  <span class="text-lg">{{ lang.flag }}</span>
                  <span>{{ lang.label }}</span>
                </button>
              </div>
            </Transition>
          </div>

          <!-- User Menu -->
          <template v-if="isAuthenticated">
            <div class="relative">
              <button
                type="button"
                class="flex items-center gap-2 px-3 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
                @click="showUserMenu = !showUserMenu"
                @blur="delayedCloseUserMenu"
              >
                <span class="w-8 h-8 bg-wine-100 rounded-full flex items-center justify-center">
                  <span class="text-wine-700 font-medium text-sm">
                    {{ user?.name?.charAt(0)?.toUpperCase() || 'U' }}
                  </span>
                </span>
                <span class="hidden sm:inline font-medium">{{ user?.name }}</span>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              <!-- User Dropdown -->
              <Transition
                enter-active-class="transition ease-out duration-100"
                enter-from-class="transform opacity-0 scale-95"
                enter-to-class="transform opacity-100 scale-100"
                leave-active-class="transition ease-in duration-75"
                leave-from-class="transform opacity-100 scale-100"
                leave-to-class="transform opacity-0 scale-95"
              >
                <div
                  v-if="showUserMenu"
                  class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-100 py-1 z-50"
                >
                  <router-link
                    to="/cellar"
                    class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    @click="showUserMenu = false"
                  >
                    <img src="/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg" alt="" class="w-4 h-4" />
                    {{ t('nav.cellar') }}
                  </router-link>
                  <hr class="my-1 border-gray-100" />
                  <button
                    type="button"
                    class="w-full flex items-center gap-3 px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                    @mousedown.prevent="handleLogout"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                    </svg>
                    {{ t('auth.logout') }}
                  </button>
                </div>
              </Transition>
            </div>
          </template>

          <!-- Login/Register buttons -->
          <template v-else>
            <router-link to="/login">
              <BaseButton variant="ghost" size="sm">{{ t('auth.login') }}</BaseButton>
            </router-link>
            <router-link to="/register" class="hidden sm:block">
              <BaseButton variant="primary" size="sm">{{ t('auth.register') }}</BaseButton>
            </router-link>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>


<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'
import AppFooter from './AppFooter.vue'

const route = useRoute()

// Pages that need full-width layout (no container constraints)
const fullWidthPages = ['/', '/home']
const isFullWidth = computed(() => fullWidthPages.includes(route.path))
</script>

<template>
  <div class="min-h-screen flex flex-col bg-white">
    <AppHeader />

    <div class="flex flex-1">
      <!-- Mobile Sidebar (hidden by default, shown on toggle) -->
      <AppSidebar class="lg:hidden" />

      <!-- Main content -->
      <main class="flex-1">
        <!-- Full width for home page -->
        <div v-if="isFullWidth" class="py-6">
          <slot />
        </div>
        <!-- Contained width for other pages -->
        <div v-else class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <slot />
        </div>
      </main>
    </div>

    <AppFooter />
  </div>
</template>


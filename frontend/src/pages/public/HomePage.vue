<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@stores/auth.store'
import BaseButton from '@components/common/BaseButton.vue'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated)

const features = [
  {
    icon: '/images/icons/reshot-icon-vine-tasting-P5GDV7FUBS.svg',
    title: 'Discover Wines',
    description: 'Explore thousands of wines from Argentina and around the world.',
  },
  {
    icon: '/images/icons/reshot-icon-wine-bottle-and-cup-375YBXTJSG.svg',
    title: 'Scan & Learn',
    description: 'Scan wine labels to get instant information and reviews.',
  },
  {
    icon: '/images/icons/reshot-icon-toast-65A7YV3EXC.svg',
    title: 'Events',
    description: 'Find wine tastings, festivals, and events near you.',
  },
  {
    icon: '/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg',
    title: 'Wine Routes',
    description: 'Discover curated wine routes and plan your wine tourism.',
  },
]

const wineTypes = [
  { type: 'Red', icon: '/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg', path: '/wines?type=red' },
  { type: 'White', icon: '/images/icons/reshot-icon-white-wine-WMRH4UFN83.svg', path: '/wines?type=white' },
  { type: 'Rosé', icon: '/images/icons/reshot-icon-rose-5UAFMK7JNE.svg', path: '/wines?type=rose' },
  { type: 'Sparkling', icon: '/images/icons/reshot-icon-sparkly-wine-4YT69UR85S.svg', path: '/wines?type=sparkling' },
]

const popularGrapes = [
  'Malbec', 'Cabernet Sauvignon', 'Merlot', 'Pinot Noir',
  'Chardonnay', 'Sauvignon Blanc', 'Torrontés', 'Syrah'
]
</script>

<template>
  <div class="space-y-16">
    <!-- Hero Section with Background Image - Full Width -->
    <section class="relative overflow-hidden -mt-6">
      <!-- Background Image -->
      <div class="absolute inset-0">
        <img
          src="/images/winery/wineyard.jpg"
          alt="Vineyard"
          class="w-full h-full object-cover"
        />
        <!-- Dark overlay for text readability -->
        <div class="absolute inset-0 bg-gradient-to-r from-black/70 via-black/50 to-black/40"></div>
      </div>

      <!-- Content -->
      <div class="relative z-10 px-4 sm:px-6 lg:px-8 py-24 md:py-32 lg:py-40">
        <div class="max-w-4xl mx-auto text-center">
          <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-white mb-6 leading-tight drop-shadow-lg">
            Discover the perfect wine<br class="hidden md:block" />
            <span class="text-wine-300">for every moment</span>
          </h1>
          <p class="text-lg md:text-xl text-gray-200 mb-10 max-w-2xl mx-auto drop-shadow">
            Your personal sommelier. Explore, compare, and find wines from the best wineries in Argentina and around the world.
          </p>
          <div class="flex flex-wrap justify-center gap-4">
            <router-link to="/wines">
              <BaseButton size="lg" class="px-8">
                <img src="/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg" alt="" class="w-5 h-5 mr-2 inline-block" />Explore Wines
              </BaseButton>
            </router-link>
            <router-link to="/wines/scan">
              <BaseButton variant="outline" size="lg" class="px-8 !border-wine-300 !text-white hover:!bg-wine-300/20 hover:!border-wine-200">
                <svg class="w-5 h-5 mr-2 inline-block" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" /></svg>Scan Wine
              </BaseButton>
            </router-link>
            <router-link v-if="!isAuthenticated" to="/register">
              <BaseButton variant="outline" size="lg" class="px-8 !border-wine-300 !text-white hover:!bg-wine-300/20 hover:!border-wine-200">
                Get Started Free
              </BaseButton>
            </router-link>
            <router-link v-else to="/cellar">
              <BaseButton variant="outline" size="lg" class="px-8 !border-wine-300 !text-white hover:!bg-wine-300/20 hover:!border-wine-200">
                <img src="/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg" alt="" class="w-5 h-5 mr-2 invert inline-block" />My Cellar
              </BaseButton>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Wine Types Section -->
    <section class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-900 text-center mb-10">
        Explore by type
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
        <router-link
          v-for="wine in wineTypes"
          :key="wine.type"
          :to="wine.path"
          class="group flex flex-col items-center p-6 bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-lg hover:border-wine-200 transition-all duration-300"
        >
          <div class="w-24 h-24 mb-4 bg-stone-50 rounded-full flex items-center justify-center group-hover:bg-wine-50 transition-colors">
            <img :src="wine.icon" :alt="wine.type" class="w-16 h-16" />
          </div>
          <span class="font-semibold text-gray-900 group-hover:text-wine-700 transition-colors">{{ wine.type }}</span>
        </router-link>
      </div>
    </section>

    <!-- Popular Grapes -->
    <section class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-900 text-center mb-8">
        Popular grape varieties
      </h2>
      <div class="flex flex-wrap justify-center gap-3">
        <router-link
          v-for="grape in popularGrapes"
          :key="grape"
          :to="`/wines?grape=${grape.toLowerCase()}`"
          class="px-5 py-2.5 bg-white border border-gray-200 rounded-full text-sm font-medium text-gray-700 hover:border-wine-300 hover:text-wine-700 hover:bg-wine-50 transition-all duration-200"
        >
          {{ grape }}
        </router-link>
      </div>
    </section>

    <!-- Features Section -->
    <section class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
      <h2 class="text-2xl md:text-3xl font-bold text-gray-900 text-center mb-4">
        Everything you need for wine discovery
      </h2>
      <p class="text-gray-600 text-center mb-10 max-w-2xl mx-auto">
        From scanning labels to planning wine routes, we've got you covered.
      </p>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div
          v-for="feature in features"
          :key="feature.title"
          class="group p-6 bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md hover:border-wine-100 transition-all duration-300"
        >
          <div class="w-14 h-14 mb-4 bg-wine-50 rounded-xl flex items-center justify-center group-hover:bg-wine-100 transition-colors">
            <img :src="feature.icon" :alt="feature.title" class="w-8 h-8" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ feature.title }}</h3>
          <p class="text-gray-600 text-sm">{{ feature.description }}</p>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
      <div class="bg-gradient-to-r from-wine-600 to-wine-700 rounded-3xl p-10 md:p-14">
        <h2 class="text-2xl md:text-3xl font-bold text-white mb-4">
          Ready to start your wine journey?
        </h2>
        <p class="text-wine-100 mb-8 max-w-xl mx-auto">
          Join thousands of wine enthusiasts discovering new favorites every day. It's free to get started.
        </p>
        <router-link v-if="!isAuthenticated" to="/register">
          <BaseButton variant="secondary" size="lg" class="px-10 bg-white text-wine-700 hover:bg-wine-50">
            Create Free Account
          </BaseButton>
        </router-link>
        <router-link v-else to="/wines">
          <BaseButton variant="secondary" size="lg" class="px-10 bg-white text-wine-700 hover:bg-wine-50">
            Start Exploring
          </BaseButton>
        </router-link>
      </div>
    </section>
  </div>
</template>


import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig(({ mode }) => {
  // Load environment variables
  const env = loadEnv(mode, process.cwd(), '')

  // Get base path from environment or default to '/'
  const basePath = env.VITE_BASE_PATH || '/'

  return {
    // Base public path - supports deployment to subdirectories
    base: basePath,

    plugins: [vue()],

    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        '@app': fileURLToPath(new URL('./src/app', import.meta.url)),
        '@components': fileURLToPath(new URL('./src/components', import.meta.url)),
        '@pages': fileURLToPath(new URL('./src/pages', import.meta.url)),
        '@domain': fileURLToPath(new URL('./src/domain', import.meta.url)),
        '@services': fileURLToPath(new URL('./src/services', import.meta.url)),
        '@api': fileURLToPath(new URL('./src/api', import.meta.url)),
        '@stores': fileURLToPath(new URL('./src/stores', import.meta.url)),
        '@composables': fileURLToPath(new URL('./src/composables', import.meta.url)),
        '@utils': fileURLToPath(new URL('./src/utils', import.meta.url)),
        '@config': fileURLToPath(new URL('./src/config', import.meta.url)),
        '@i18n': fileURLToPath(new URL('./src/i18n', import.meta.url)),
        '@types': fileURLToPath(new URL('./src/types', import.meta.url)),
      },
    },

    server: {
      port: 3000,
      open: true,
    },

    build: {
      // Output directory
      outDir: 'dist',

      // Generate sourcemaps based on env
      sourcemap: env.VITE_GENERATE_SOURCEMAP === 'true',

      // Chunk size warnings
      chunkSizeWarningLimit: 1000,

      // Rollup options for better chunking
      rollupOptions: {
        output: {
          // Manual chunks for better caching
          manualChunks: {
            'vendor-vue': ['vue', 'vue-router', 'pinia'],
            'vendor-utils': ['axios', 'zod'],
            'vendor-i18n': ['vue-i18n'],
          },
          // Asset file naming
          assetFileNames: (assetInfo) => {
            const info = assetInfo.name?.split('.') || []
            const ext = info[info.length - 1]
            if (/png|jpe?g|svg|gif|tiff|bmp|ico/i.test(ext)) {
              return 'assets/images/[name]-[hash][extname]'
            }
            if (/woff2?|eot|ttf|otf/i.test(ext)) {
              return 'assets/fonts/[name]-[hash][extname]'
            }
            return 'assets/[name]-[hash][extname]'
          },
          chunkFileNames: 'assets/js/[name]-[hash].js',
          entryFileNames: 'assets/js/[name]-[hash].js',
        },
      },
    },

    // Preview server config (for `npm run preview`)
    preview: {
      port: 4173,
    },
  }
})

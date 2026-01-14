import { createRouter, createWebHistory } from 'vue-router'
import { routes } from '@config/routes'
import { authGuard } from '@utils/guard'

export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
})

router.beforeEach(authGuard)

export default router


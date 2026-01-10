import { ROLES } from '@config/roles'

/**
 * Navigation item interface with RBAC support
 */
export interface NavigationItem {
  path: string
  labelKey: string
  icon: string
  exact?: boolean
  requiresAuth?: boolean
  guestOnly?: boolean
  roles?: string[]
}

/**
 * Main navigation items for the application
 * Items are filtered based on authentication and user roles
 */
export const navigationItems: NavigationItem[] = [
  {
    path: '/',
    labelKey: 'nav.home',
    icon: '/images/icons/reshot-icon-grapes-DH3FRP42X5.svg',
    exact: true,
  },
  {
    path: '/wines',
    labelKey: 'nav.wines',
    icon: '/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg',
  },
  {
    path: '/events',
    labelKey: 'nav.events',
    icon: '/images/icons/reshot-icon-toast-65A7YV3EXC.svg',
  },
  {
    path: '/wine-routes',
    labelKey: 'nav.routes',
    icon: '/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg',
  },
  {
    path: '/cellar',
    labelKey: 'nav.cellar',
    icon: '/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg',
    requiresAuth: true,
    roles: [ROLES.USER, ROLES.PARTNER, ROLES.MODERATOR, ROLES.ADMIN],
  },
  {
    path: '/admin',
    labelKey: 'nav.admin',
    icon: '/images/icons/reshot-icon-enology-MW9AB2GQPR.svg',
    requiresAuth: true,
    roles: [ROLES.ADMIN, ROLES.MODERATOR],
  },
]

/**
 * Secondary navigation items (user menu, etc.)
 */
export const userMenuItems: NavigationItem[] = [
  {
    path: '/cellar',
    labelKey: 'nav.cellar',
    icon: '/images/icons/reshot-icon-vine-cellar-PK3MZL62NG.svg',
    requiresAuth: true,
  },
]

/**
 * Admin sub-navigation items
 */
export const adminNavItems: NavigationItem[] = [
  {
    path: '/admin',
    labelKey: 'nav.adminDashboard',
    icon: '/images/icons/reshot-icon-enology-MW9AB2GQPR.svg',
    exact: true,
    requiresAuth: true,
    roles: [ROLES.ADMIN, ROLES.MODERATOR],
  },
  {
    path: '/admin/wines',
    labelKey: 'nav.adminWines',
    icon: '/images/icons/reshot-icon-red-wine-L2HFAY75WG.svg',
    requiresAuth: true,
    roles: [ROLES.ADMIN, ROLES.MODERATOR],
  },
  {
    path: '/admin/events',
    labelKey: 'nav.adminEvents',
    icon: '/images/icons/reshot-icon-toast-65A7YV3EXC.svg',
    requiresAuth: true,
    roles: [ROLES.ADMIN, ROLES.MODERATOR],
  },
  {
    path: '/admin/routes',
    labelKey: 'nav.adminRoutes',
    icon: '/images/icons/reshot-icon-vineyard-H8S2KEC3PT.svg',
    requiresAuth: true,
    roles: [ROLES.ADMIN, ROLES.MODERATOR],
  },
  {
    path: '/admin/users',
    labelKey: 'nav.adminUsers',
    icon: '/images/icons/reshot-icon-male-sommelier-4J9YXUNDT3.svg',
    requiresAuth: true,
    roles: [ROLES.ADMIN],
  },
]

export default navigationItems


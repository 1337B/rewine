import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    /**
     * If true, the route requires authentication
     */
    requiresAuth?: boolean

    /**
     * Required roles to access the route
     */
    roles?: string[]

    /**
     * Page title for the route
     */
    title?: string

    /**
     * If true, the route is only accessible to guests (not logged in)
     */
    guestOnly?: boolean

    /**
     * Custom layout to use for the route
     */
    layout?: 'default' | 'auth' | 'admin'
  }
}

export {}


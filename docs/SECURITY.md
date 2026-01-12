# Security Documentation

This document outlines the security architecture, authentication model, and best practices for the Rewine platform.

---

## Table of Contents

1. [Authentication Model](#authentication-model)
2. [Token Storage Strategy](#token-storage-strategy)
3. [CORS Configuration](#cors-configuration)
4. [CSRF Protection](#csrf-protection)
5. [Secrets Management](#secrets-management)
6. [Security Headers](#security-headers)
7. [Input Validation](#input-validation)
8. [Role-Based Access Control](#role-based-access-control)

---

## Authentication Model

Rewine uses **JWT (JSON Web Token)** based authentication with a dual-token strategy:

### Token Types

| Token | Purpose | Lifetime | Storage |
|-------|---------|----------|---------|
| **Access Token** | Authorize API requests | 15 minutes | Memory (JavaScript) |
| **Refresh Token** | Obtain new access tokens | 7 days | HttpOnly Cookie |

### Authentication Flow

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│  User   │                    │Frontend │                    │ Backend │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  1. Enter credentials        │                              │
     │─────────────────────────────▶│                              │
     │                              │                              │
     │                              │  2. POST /auth/login         │
     │                              │─────────────────────────────▶│
     │                              │                              │
     │                              │  3. Validate credentials     │
     │                              │                              │
     │                              │  4. Access token (body)      │
     │                              │     + Refresh token (cookie) │
     │                              │◀─────────────────────────────│
     │                              │                              │
     │  5. Store access token       │                              │
     │     in memory                │                              │
     │                              │                              │
     │  6. Subsequent requests      │                              │
     │─────────────────────────────▶│                              │
     │                              │  7. Request + Bearer token   │
     │                              │─────────────────────────────▶│
     │                              │                              │
```

### Token Refresh Flow

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│Frontend │                    │  API    │                    │ Backend │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  1. Request returns 401      │                              │
     │◀─────────────────────────────│                              │
     │                              │                              │
     │  2. POST /auth/refresh       │                              │
     │     (refresh token in cookie)│                              │
     │─────────────────────────────▶│─────────────────────────────▶│
     │                              │                              │
     │                              │  3. Validate refresh token   │
     │                              │                              │
     │  4. New access token         │                              │
     │◀─────────────────────────────│◀─────────────────────────────│
     │                              │                              │
     │  5. Retry original request   │                              │
     │─────────────────────────────▶│                              │
     │                              │                              │
```

---

## Token Storage Strategy

### Recommended Approach

| Token | Storage | Rationale |
|-------|---------|-----------|
| Access Token | In-memory (Pinia store) | Not accessible to XSS, lost on page reload |
| Refresh Token | HttpOnly Secure cookie | Not accessible to JavaScript |

### Why Not localStorage?

```
❌ localStorage/sessionStorage for tokens
   - Vulnerable to XSS attacks
   - Any script can read the token
   - Persists across tabs (potential security issue)

✅ In-memory + HttpOnly cookie
   - Access token in memory: XSS can't directly steal it
   - Refresh token HttpOnly: JavaScript cannot access
   - Short-lived access token limits exposure window
```

### Implementation (Frontend)

```typescript
// src/stores/auth.store.ts
export const useAuthStore = defineStore('auth', () => {
  // Access token in memory only (not persisted)
  const accessToken = ref<string | null>(null)
  
  // User data can be persisted
  const user = ref<User | null>(null)

  function setAccessToken(token: string) {
    accessToken.value = token
  }

  function clearSession() {
    accessToken.value = null
    user.value = null
  }

  return { accessToken, user, setAccessToken, clearSession }
})
```

### Implementation (Backend - Planned)

```java
// Set refresh token as HttpOnly cookie
ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
    .httpOnly(true)
    .secure(true)           // HTTPS only
    .path("/api/auth")      // Limited scope
    .maxAge(Duration.ofDays(7))
    .sameSite("Strict")
    .build();
```

---

## CORS Configuration

### Frontend Considerations

For development with different origins:

```typescript
// vite.config.ts - Proxy for development
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

### Backend Configuration (Planned)

```java
// CorsConfig.java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allowed origins (specific, not *)
        config.setAllowedOrigins(List.of(
            "https://rewine.com",
            "https://uat.rewine.com"
        ));
        
        // Allow credentials (for cookies)
        config.setAllowCredentials(true);
        
        // Allowed methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        
        // Allowed headers
        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With"
        ));
        
        // Exposed headers (for custom headers in response)
        config.setExposedHeaders(List.of("X-Request-Id"));
        
        return new UrlBasedCorsConfigurationSource() {{
            registerCorsConfiguration("/api/**", config);
        }};
    }
}
```

### CORS Checklist

- [ ] Never use `Access-Control-Allow-Origin: *` with credentials
- [ ] Specify exact origins in production
- [ ] Limit allowed methods to what's needed
- [ ] Limit allowed headers to what's needed
- [ ] Use `SameSite` cookie attribute

---

## CSRF Protection

### SPA Considerations

For SPAs using JWT authentication:

1. **Access token in header** provides implicit CSRF protection
   - Attacker site cannot read the token to include in header
   
2. **Refresh token in HttpOnly cookie** needs CSRF protection
   - Use `SameSite=Strict` attribute
   - Consider double-submit cookie pattern

### Implementation

```java
// Refresh endpoint CSRF protection
@PostMapping("/auth/refresh")
public ResponseEntity<?> refresh(
    @CookieValue("refreshToken") String refreshToken,
    @RequestHeader("X-CSRF-Token") String csrfToken  // Optional: double-submit
) {
    // Validate CSRF token matches cookie
    // Validate refresh token
    // Issue new access token
}
```

### SameSite Cookie Configuration

```
Set-Cookie: refreshToken=xxx; 
    HttpOnly; 
    Secure; 
    SameSite=Strict;     // Prevents CSRF
    Path=/api/auth
```

---

## Secrets Management

### Golden Rules

1. **Never commit secrets** to version control
2. **Never log secrets** or tokens
3. **Rotate secrets** regularly
4. **Use different secrets** per environment

### Environment Variables

```bash
# ❌ Never do this
VITE_API_KEY=super-secret-key-12345

# ✅ API keys should be backend-only
# Frontend .env should only have non-sensitive values
VITE_API_BASE_URL=/api/v1
VITE_APP_NAME=Rewine
```

### Handling Secrets

| Environment | Method |
|-------------|--------|
| Local Dev | `.env.local` (gitignored) |
| CI/CD | Pipeline secrets (Jenkins, GitHub Actions) |
| Kubernetes | K8s Secrets or Vault |
| Docker | Docker secrets, not build args |

### .gitignore Entries

```gitignore
# Environment files with secrets
.env.local
.env.*.local
.env.production

# IDE files that may contain secrets
.idea/
.vscode/settings.json
```

---

## Security Headers

The Nginx configuration includes security headers for the frontend SPA:

### nginx.conf Security Headers

```nginx
# Prevent clickjacking
add_header X-Frame-Options "SAMEORIGIN" always;

# Prevent MIME type sniffing
add_header X-Content-Type-Options "nosniff" always;

# XSS filter (legacy browsers)
add_header X-XSS-Protection "1; mode=block" always;

# Referrer policy
add_header Referrer-Policy "strict-origin-when-cross-origin" always;

# Permissions policy (camera, mic, geolocation)
add_header Permissions-Policy "camera=(), microphone=(), geolocation=(self)" always;
```

### Content Security Policy (Recommended)

```nginx
# CSP - Customize based on your needs
add_header Content-Security-Policy "
    default-src 'self';
    script-src 'self' 'unsafe-inline' 'unsafe-eval';
    style-src 'self' 'unsafe-inline';
    img-src 'self' data: https:;
    font-src 'self' data:;
    connect-src 'self' https://api.rewine.com;
    frame-ancestors 'self';
" always;
```

### Header Checklist

| Header | Purpose | Recommended Value |
|--------|---------|-------------------|
| `X-Frame-Options` | Prevent clickjacking | `SAMEORIGIN` |
| `X-Content-Type-Options` | Prevent MIME sniffing | `nosniff` |
| `X-XSS-Protection` | XSS filter (legacy) | `1; mode=block` |
| `Referrer-Policy` | Control referrer info | `strict-origin-when-cross-origin` |
| `Content-Security-Policy` | Control resource loading | Custom per app |
| `Strict-Transport-Security` | Force HTTPS | `max-age=31536000; includeSubDomains` |

---

## Input Validation

### Frontend Validation (Zod)

```typescript
// src/domain/user/user.validators.ts
import { z } from 'zod'

export const loginSchema = z.object({
  email: z.string()
    .email('Invalid email address')
    .max(255, 'Email too long'),
  password: z.string()
    .min(8, 'Password must be at least 8 characters')
    .max(100, 'Password too long'),
})

export type LoginInput = z.infer<typeof loginSchema>
```

### Usage

```typescript
// In service or component
const result = loginSchema.safeParse(formData)
if (!result.success) {
  // Handle validation errors
  const errors = result.error.flatten().fieldErrors
}
```

### Backend Validation (Planned)

```java
// DTO with validation annotations
public class LoginRequest {
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
```

### Validation Rules

| Field | Frontend | Backend |
|-------|----------|---------|
| Email | Zod email + max length | @Email + @Size |
| Password | Zod min/max + regex | @Size + @Pattern |
| IDs | UUID format | UUID validation |
| Dates | ISO 8601 format | @DateTimeFormat |

---

## Role-Based Access Control

### Roles

| Role | Code | Permissions |
|------|------|-------------|
| Admin | `ROLE_ADMIN` | Full system access |
| Moderator | `ROLE_MODERATOR` | Review moderation, user management |
| Partner | `ROLE_PARTNER` | Manage own wines, events, routes |
| User | `ROLE_USER` | Browse, review, manage personal cellar |

### Frontend Route Protection

```typescript
// src/config/routes.ts
{
  path: '/admin',
  meta: {
    requiresAuth: true,
    roles: ['ROLE_ADMIN']
  }
}

// src/app/router.ts - Guard
router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return next({ path: '/login', query: { returnUrl: to.fullPath } })
  }
  
  if (to.meta.roles && !auth.hasAnyRole(to.meta.roles)) {
    return next('/403')
  }
  
  next()
})
```

### Backend Authorization (Planned)

```java
// Controller method security
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public List<UserDto> getAllUsers() { ... }

@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
@DeleteMapping("/reviews/{id}")
public void deleteReview(@PathVariable Long id) { ... }

@PreAuthorize("hasRole('PARTNER') and @wineService.isOwner(#id, principal)")
@PutMapping("/wines/{id}")
public WineDto updateWine(@PathVariable Long id, @RequestBody WineDto dto) { ... }
```

---

## Security Checklist

### Development

- [ ] No secrets in code or version control
- [ ] HTTPS in all environments (except localhost)
- [ ] Input validation on all user input
- [ ] Proper error handling (no stack traces to users)

### Authentication

- [ ] Access tokens short-lived (15 min)
- [ ] Refresh tokens HttpOnly + Secure
- [ ] Token rotation on refresh
- [ ] Session invalidation on logout

### Authorization

- [ ] RBAC enforced on frontend routes
- [ ] RBAC enforced on backend endpoints
- [ ] Resource ownership checks

### Headers & CORS

- [ ] Security headers configured
- [ ] CORS properly restricted
- [ ] SameSite cookies

---

## Related Documentation

- [Architecture](ARCHITECTURE.md) - System architecture
- [Environments](ENVIRONMENTS.md) - Environment configuration
- [Frontend Guide](FRONTEND.md) - Frontend development


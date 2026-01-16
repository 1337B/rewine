# Security Documentation

This document outlines the security architecture, authentication model, and best practices for the Rewine platform.

---

## Table of Contents

1. [Authentication Model](#authentication-model)
2. [Backend JWT Implementation](#backend-jwt-implementation)
3. [Token Storage Strategy](#token-storage-strategy)
4. [Refresh Token Rotation](#refresh-token-rotation)
5. [CORS Configuration](#cors-configuration)
6. [CSRF Protection](#csrf-protection)
7. [Secrets Management](#secrets-management)
8. [Security Headers](#security-headers)
9. [Input Validation](#input-validation)
10. [Role-Based Access Control](#role-based-access-control)

---

## Authentication Model

Rewine uses **JWT (JSON Web Token)** based authentication with a dual-token strategy and refresh token rotation.

### Token Types

| Token | Purpose | Lifetime | Storage |
|-------|---------|----------|---------|
| **Access Token** | Authorize API requests | 15 minutes | Memory (JavaScript) or Client storage |
| **Refresh Token** | Obtain new access tokens | 7 days | Client storage, hash stored in DB |

### Authentication Flow

```
┌─────────┐                    ┌─────────┐                    ┌─────────┐
│  User   │                    │Frontend │                    │ Backend │
└────┬────┘                    └────┬────┘                    └────┬────┘
     │                              │                              │
     │  1. Enter credentials        │                              │
     │─────────────────────────────▶│                              │
     │                              │                              │
     │                              │  2. POST /api/v1/auth/login  │
     │                              │─────────────────────────────▶│
     │                              │                              │
     │                              │  3. Validate credentials     │
     │                              │     (BCrypt password check)  │
     │                              │                              │
     │                              │  4. Generate tokens          │
     │                              │     Store refresh hash in DB │
     │                              │                              │
     │                              │  5. {accessToken,            │
     │                              │      refreshToken, user}     │
     │                              │◀─────────────────────────────│
     │                              │                              │
     │  6. Store tokens             │                              │
     │                              │                              │
     │  7. Subsequent requests      │                              │
     │─────────────────────────────▶│                              │
     │                              │  8. Authorization: Bearer xxx│
     │                              │─────────────────────────────▶│
     │                              │                              │
     │                              │  9. JWT validated,           │
     │                              │     user context extracted   │
     │                              │                              │
```

---

## Backend JWT Implementation

### Configuration Properties

```yaml
# application.yml
jwt:
  issuer: rewine-backend
  secret: ${JWT_SECRET:your-256-bit-secret-key-minimum-32-chars}
  access-token-expiration: 900000    # 15 minutes in milliseconds
  refresh-token-expiration: 604800000 # 7 days in milliseconds
```

### JWT Token Structure

**Access Token Claims:**
```json
{
  "sub": "username",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "iss": "rewine-backend",
  "iat": 1704067200,
  "exp": 1704068100
}
```

### Key Security Components

| Component | Description |
|-----------|-------------|
| `JwtProperties` | Configuration properties for JWT (secret, expiration times) |
| `JwtTokenServiceImpl` | Generates and validates JWT tokens |
| `JwtAuthenticationFilter` | Spring Security filter that extracts and validates JWT from requests |
| `JwtAuthenticationEntryPoint` | Handles 401 Unauthorized responses with proper error format |
| `UserDetailsServiceImpl` | Loads user details from database for authentication |
| `AuthServiceImpl` | Handles login, register, refresh, and logout operations |

### Password Hashing

Passwords are hashed using BCrypt:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // Default strength: 10
}
```

### API Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/v1/auth/register` | POST | Register new user | No |
| `/api/v1/auth/login` | POST | Login, get tokens | No |
| `/api/v1/auth/refresh` | POST | Refresh access token | No (uses refresh token) |
| `/api/v1/auth/logout` | POST | Revoke refresh token | No |
| `/api/v1/auth/me` | GET | Get current user profile | Yes |

---

## Token Storage Strategy

### Recommended Approach

| Token | Storage | Rationale |
|-------|---------|-----------|
| Access Token | In-memory (Pinia store) | Not accessible to XSS, lost on page reload |
| Refresh Token | sessionStorage or HttpOnly Cookie | Limited exposure |

### Why Not localStorage for Access Tokens?

```
❌ localStorage/sessionStorage for access tokens
   - Vulnerable to XSS attacks
   - Any script can read the token
   - Persists across tabs (potential security issue)

✅ In-memory + HttpOnly cookie (for refresh)
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

---

## Refresh Token Rotation

For enhanced security, refresh tokens are rotated on each use. This prevents token reuse attacks.

### How It Works

1. Client sends refresh token to `/api/v1/auth/refresh`
2. Backend validates:
   - Token hash exists in database
   - Token is not expired
   - Token is not revoked
3. Backend generates **new** access token AND **new** refresh token
4. Old refresh token is marked as revoked with `replaced_by_token_hash`
5. New tokens returned to client

### Database Schema

```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    token_hash VARCHAR(255) NOT NULL,      -- SHA-256 hash of token
    device_info VARCHAR(500),
    ip_address VARCHAR(45),
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,                   -- NULL if active
    revoked_reason VARCHAR(100),
    replaced_by_token_hash VARCHAR(255),   -- For rotation tracking
    created_at TIMESTAMP DEFAULT NOW()
);
```

### Token Revocation Reasons

| Reason | Description |
|--------|-------------|
| `User logout` | User explicitly logged out |
| `Token rotation` | Token was replaced by a new one |
| `Security concern` | Suspicious activity detected |
| `Password changed` | User changed their password |
| `Revoked by admin` | Administrator revoked the token |

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
- [Credentials & Accounts](./CREDENTIALS_AND_ACCOUNTS.md) - Environment variables, API keys, test users
- [Environments](ENVIRONMENTS.md) - Environment configuration
- [Backend Guide](../backend/README.md) - Backend configuration
- [Frontend Guide](FRONTEND.md) - Frontend development
- [Troubleshooting](./TROUBLESHOOTING.md) - Common issues and solutions


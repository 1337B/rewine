# Troubleshooting Guide

This document provides solutions for common issues encountered during development and deployment of the Rewine platform.

---

## Table of Contents

1. [Development Issues](#development-issues)
2. [Build Issues](#build-issues)
3. [Runtime Issues](#runtime-issues)
4. [Testing Issues](#testing-issues)
5. [Docker Issues](#docker-issues)
6. [API Issues](#api-issues)

---

## Development Issues

### Node Version Mismatch

**Symptoms:**
- `npm install` fails with engine errors
- Unexpected syntax errors
- Package compatibility issues

**Solution:**

```bash
# Check current Node version
node --version

# Required: Node 20.x or higher
# Install correct version using nvm
nvm install 20
nvm use 20

# Or install Node 20 from nodejs.org

# Clear npm cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

**Windows:**
```powershell
# Using nvm-windows
nvm install 20
nvm use 20

# Clear and reinstall
Remove-Item -Recurse -Force node_modules
Remove-Item package-lock.json
npm install
```

---

### Port Already in Use

**Symptoms:**
- Error: `EADDRINUSE: address already in use :::3000`
- Dev server fails to start

**Solution (macOS/Linux):**

```bash
# Find process using port 3000
lsof -i :3000

# Kill the process
kill -9 <PID>

# Or use a different port
npm run dev -- --port 3001
```

**Solution (Windows):**

```powershell
# Find process using port 3000
netstat -ano | findstr :3000

# Kill the process (replace PID)
taskkill /PID <PID> /F

# Or use a different port
npm run dev -- --port 3001
```

---

### Environment Variables Not Loading

**Symptoms:**
- `undefined` values for env variables
- Features not working as expected
- API calls going to wrong URL

**Diagnosis:**

```typescript
// Add to main.ts temporarily
console.log('Environment:', {
  API_URL: import.meta.env.VITE_API_BASE_URL,
  MOCK: import.meta.env.VITE_MOCK_API,
  MODE: import.meta.env.MODE,
})
```

**Solutions:**

1. **Check file naming:**
   ```
   ✅ .env
   ✅ .env.local
   ✅ .env.development
   ❌ .env.txt
   ❌ env.local
   ```

2. **Check variable prefix:**
   ```bash
   # ✅ Correct - exposed to client
   VITE_API_BASE_URL=/api/v1
   
   # ❌ Wrong - not exposed
   API_BASE_URL=/api/v1
   ```

3. **Restart dev server** after changing `.env` files

4. **Check for typos:**
   ```bash
   # In .env
   VITE_API_BASE_URL=/api/v1
   
   # In code - must match exactly
   import.meta.env.VITE_API_BASE_URL
   ```

---

### Dependencies Not Found

**Symptoms:**
- `Module not found` errors
- Import resolution failures

**Solution:**

```bash
# Clear everything and reinstall
rm -rf node_modules
rm package-lock.json
npm cache clean --force
npm install

# If using path aliases, check tsconfig.json and vite.config.ts
```

---

## Build Issues

### TypeScript Compilation Errors

**Symptoms:**
- `vue-tsc` fails with type errors
- Build process stops

**Diagnosis:**

```bash
# Run type check separately
npx vue-tsc --noEmit

# Check specific file
npx vue-tsc --noEmit src/path/to/file.ts
```

**Common Fixes:**

1. **Missing types:**
   ```bash
   npm install -D @types/node
   ```

2. **Strict null checks:**
   ```typescript
   // ❌ Error: possibly undefined
   const name = user.name.toLowerCase()
   
   // ✅ Fix: optional chaining
   const name = user?.name?.toLowerCase()
   
   // ✅ Or: null check
   if (user && user.name) {
     const name = user.name.toLowerCase()
   }
   ```

3. **Import errors:**
   ```typescript
   // ❌ Error: cannot find module
   import { Wine } from '@domain/wine'
   
   // ✅ Fix: correct path
   import { Wine } from '@domain/wine/wine.types'
   ```

---

### Vite Build Fails

**Symptoms:**
- `vite build` exits with error
- "Failed to resolve import"

**Solutions:**

1. **Check for circular imports:**
   ```bash
   # Install circular dependency checker
   npm install -D madge
   
   # Check for cycles
   npx madge --circular src/
   ```

2. **Check for missing files:**
   ```bash
   # Verify all imports exist
   npm run lint
   ```

3. **Clear Vite cache:**
   ```bash
   rm -rf node_modules/.vite
   npm run build
   ```

---

### Out of Memory During Build

**Symptoms:**
- `FATAL ERROR: CALL_AND_RETRY_LAST Allocation failed`
- Build process killed

**Solution:**

```bash
# Increase Node memory limit
export NODE_OPTIONS="--max-old-space-size=4096"
npm run build

# Or inline
NODE_OPTIONS="--max-old-space-size=4096" npm run build
```

**Windows:**
```powershell
$env:NODE_OPTIONS="--max-old-space-size=4096"
npm run build
```

---

## Runtime Issues

### Blank White Page

**Symptoms:**
- App loads but shows nothing
- No errors in network tab

**Diagnosis:**

1. **Check browser console for errors**

2. **Check for JavaScript errors:**
   ```
   Uncaught TypeError: Cannot read property 'x' of undefined
   ```

3. **Check router configuration:**
   ```typescript
   // Ensure routes are defined
   console.log(router.getRoutes())
   ```

**Common Causes:**

1. **Missing router view:**
   ```vue
   <!-- App.vue must have -->
   <router-view />
   ```

2. **Error in root component:**
   ```vue
   <!-- Check App.vue for errors -->
   <script setup>
   // Any error here will blank the page
   </script>
   ```

3. **Invalid route configuration:**
   ```typescript
   // Check routes.ts for syntax errors
   ```

---

### CORS Errors

**Symptoms:**
- `Access-Control-Allow-Origin` errors in console
- API calls blocked

**Solutions:**

1. **Development with proxy:**
   ```typescript
   // vite.config.ts
   server: {
     proxy: {
       '/api': {
         target: 'http://localhost:8080',
         changeOrigin: true,
       }
     }
   }
   ```

2. **Use mock API:**
   ```bash
   npm run dev:mock
   ```

3. **Backend CORS configuration** (if you control the backend)

---

### MSW Not Intercepting Requests

**Symptoms:**
- Requests go to real API instead of mocks
- 404 errors in development

**Diagnosis:**

```bash
# Check if MSW is enabled
echo $VITE_MOCK_API  # Should be 'true'
```

**Solutions:**

1. **Verify environment:**
   ```bash
   # .env.local
   VITE_MOCK_API=true
   ```

2. **Check service worker registration:**
   ```typescript
   // src/mocks/browser.ts should exist
   // Check console for: [MSW] Mocking enabled
   ```

3. **Reinstall MSW:**
   ```bash
   npm run msw:init
   ```

4. **Check handler paths:**
   ```typescript
   // Ensure paths match
   http.get('/api/v1/wines', ...)  // Must match VITE_API_BASE_URL
   ```

---

## Testing Issues

### Vitest Tests Failing

**Symptoms:**
- Tests pass locally but fail in CI
- Timeout errors

**Solutions:**

1. **Increase timeout:**
   ```typescript
   // vitest.config.ts
   test: {
     testTimeout: 10000,
   }
   ```

2. **Check for async issues:**
   ```typescript
   // ❌ Missing await
   it('should load', () => {
     const result = fetchData()
     expect(result).toBeDefined()
   })
   
   // ✅ With await
   it('should load', async () => {
     const result = await fetchData()
     expect(result).toBeDefined()
   })
   ```

3. **Mock timers properly:**
   ```typescript
   import { vi } from 'vitest'
   
   beforeEach(() => {
     vi.useFakeTimers()
   })
   
   afterEach(() => {
     vi.useRealTimers()
   })
   ```

---

### Playwright Tests Failing

**Symptoms:**
- E2E tests timeout
- Element not found errors

**Solutions:**

1. **Install browsers:**
   ```bash
   npm run test:e2e:setup
   ```

2. **Increase timeouts:**
   ```typescript
   // playwright.config.ts
   timeout: 60000,
   expect: { timeout: 10000 },
   ```

3. **Debug with UI mode:**
   ```bash
   npm run test:e2e:ui
   ```

4. **Check for race conditions:**
   ```typescript
   // ❌ Element might not exist yet
   await page.click('button')
   
   // ✅ Wait for element
   await page.waitForSelector('button')
   await page.click('button')
   ```

---

## Docker Issues

### Docker Build Fails

**Symptoms:**
- Build stage errors
- TypeScript errors in Docker

**Solutions:**

1. **Check .dockerignore:**
   ```
   node_modules/
   dist/
   tests/
   ```

2. **Verify all files are copied:**
   ```dockerfile
   COPY package*.json ./
   COPY tsconfig*.json ./
   COPY . .
   ```

3. **Build locally first:**
   ```bash
   npm run build  # Fix any errors here first
   ```

---

### Container Exits Immediately

**Symptoms:**
- Container starts and stops
- No logs or brief error

**Diagnosis:**

```bash
# Check logs
docker logs <container_id>

# Run interactively
docker run -it rewine-frontend sh
```

**Common Causes:**

1. **Nginx config error:**
   ```bash
   # Test nginx config
   docker run -it rewine-frontend nginx -t
   ```

2. **Permission issues:**
   ```bash
   # Check file permissions
   docker run -it rewine-frontend ls -la /usr/share/nginx/html
   ```

---

## API Issues

### 401 Unauthorized Errors

**Symptoms:**
- API calls return 401
- User appears logged in but requests fail

**Solutions:**

1. **Check token storage:**
   ```typescript
   // In browser console
   console.log(useAuthStore().accessToken)
   ```

2. **Check token expiration:**
   ```typescript
   // Decode JWT to check exp
   const payload = JSON.parse(atob(token.split('.')[1]))
   console.log(new Date(payload.exp * 1000))
   ```

3. **Verify Authorization header:**
   ```typescript
   // In http.ts interceptor
   console.log('Auth header:', config.headers.Authorization)
   ```

---

### 500 Server Errors

**Symptoms:**
- API returns 500 status
- Generic error messages

**Solutions:**

1. **Check request payload:**
   ```typescript
   // Log what's being sent
   console.log('Request:', JSON.stringify(data, null, 2))
   ```

2. **Check backend logs** (if available)

3. **Use mock API to isolate issue:**
   ```bash
   VITE_MOCK_API=true npm run dev
   ```

---

## Quick Reference Commands

### Diagnostic Commands

```bash
# Check Node version
node --version

# Check npm version
npm --version

# Check for outdated packages
npm outdated

# Check for security issues
npm audit

# Check TypeScript errors
npx vue-tsc --noEmit

# Check for circular dependencies
npx madge --circular src/

# Check bundle size
npm run build -- --report
```

### Reset Commands

```bash
# Full reset (macOS/Linux)
rm -rf node_modules package-lock.json dist .vite
npm cache clean --force
npm install

# Full reset (Windows)
Remove-Item -Recurse -Force node_modules, dist, .vite
Remove-Item package-lock.json
npm cache clean --force
npm install
```

---

## Getting Help

If you've tried the above solutions and still have issues:

1. Check existing issues in the repository
2. Search error messages online
3. Create a detailed issue with:
   - Error message
   - Steps to reproduce
   - Environment (OS, Node version)
   - Relevant configuration

---

## Related Documentation

- [Frontend Guide](FRONTEND.md) - Development guide
- [Environments](ENVIRONMENTS.md) - Environment configuration
- [Architecture](ARCHITECTURE.md) - System architecture


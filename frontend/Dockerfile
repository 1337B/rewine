# =============================================================================
# Rewine Frontend - Multi-stage Dockerfile
# =============================================================================
# Build: docker build -t rewine-frontend .
# Run:   docker run -p 80:80 rewine-frontend
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Build
# -----------------------------------------------------------------------------
FROM node:20-alpine AS builder

# Set working directory
WORKDIR /app

# Install dependencies first (better layer caching)
COPY package*.json ./
RUN npm ci --ignore-scripts

# Copy source files
COPY . .

# Build arguments for environment variables
# Note: For sensitive values like API keys, consider using Docker secrets
# or injecting at runtime via environment variables
ARG VITE_API_BASE_URL=/api/v1
ARG VITE_MOCK_API=false
ARG VITE_APP_NAME=Rewine
ARG VITE_APP_ENV=production
ARG VITE_BASE_PATH=/
ARG VITE_FEATURE_WINE_SCAN=true
ARG VITE_FEATURE_SOCIAL_LOGIN=false
# Optional: Pass these at build time if needed, otherwise leave empty
ARG VITE_GOOGLE_MAPS_API_KEY
ARG VITE_SENTRY_DSN

# Set environment variables for build
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL \
    VITE_MOCK_API=$VITE_MOCK_API \
    VITE_APP_NAME=$VITE_APP_NAME \
    VITE_APP_ENV=$VITE_APP_ENV \
    VITE_BASE_PATH=$VITE_BASE_PATH \
    VITE_FEATURE_WINE_SCAN=$VITE_FEATURE_WINE_SCAN \
    VITE_FEATURE_SOCIAL_LOGIN=$VITE_FEATURE_SOCIAL_LOGIN \
    VITE_GOOGLE_MAPS_API_KEY=${VITE_GOOGLE_MAPS_API_KEY:-} \
    VITE_SENTRY_DSN=${VITE_SENTRY_DSN:-}

# Build the application using production tsconfig (excludes test references)
RUN npm run build:docker

# -----------------------------------------------------------------------------
# Stage 2: Production
# -----------------------------------------------------------------------------
FROM nginx:1.25-alpine AS production

# Install curl for healthcheck
RUN apk add --no-cache curl

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy built assets from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup && \
    chown -R appuser:appgroup /usr/share/nginx/html && \
    chown -R appuser:appgroup /var/cache/nginx && \
    chown -R appuser:appgroup /var/log/nginx && \
    touch /var/run/nginx.pid && \
    chown -R appuser:appgroup /var/run/nginx.pid

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]


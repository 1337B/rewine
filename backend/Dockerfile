# =============================================================================
# Rewine Backend - Dockerfile
# =============================================================================
# Multi-stage build for optimal image size
#
# Build:  docker build -t rewine-backend .
# Run:    docker run -p 8080:8080 rewine-backend

# -----------------------------------------------------------------------------
# Stage 1: Build
# -----------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests - they should run in CI)
RUN mvn clean package -DskipTests -B

# -----------------------------------------------------------------------------
# Stage 2: Runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Labels for container metadata
LABEL maintainer="Rewine Team"
LABEL description="Rewine Backend API"
LABEL version="0.0.1-SNAPSHOT"

# Create non-root user for security
RUN addgroup -g 1001 -S rewine && \
    adduser -u 1001 -S rewine -G rewine

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R rewine:rewine /app

# Switch to non-root user
USER rewine

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health/liveness || exit 1

# JVM configuration for containers
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]


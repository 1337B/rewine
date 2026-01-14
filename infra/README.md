# Rewine Infrastructure

This directory contains all infrastructure-related configurations for the Rewine platform.

## Directory Structure

```
infra/
├── docker-compose.yml      # Local development environment
├── .env.example           # Environment variables template
└── k8s/                   # Kubernetes/OpenShift manifests
    ├── base/              # Base manifests (shared across environments)
    │   ├── configmap.yaml
    │   ├── deployment.yaml
    │   ├── secret.yaml
    │   ├── service.yaml
    │   └── kustomization.yaml
    └── overlays/          # Environment-specific configurations
        ├── dev/
        │   └── kustomization.yaml
        ├── uat/
        │   └── kustomization.yaml
        └── prod/
            └── kustomization.yaml
```

## Local Development with Docker Compose

### Prerequisites

- Docker Engine 24+
- Docker Compose v2+

### Quick Start

1. Copy the environment template:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your configuration

3. Start all services:
   ```bash
   docker-compose up -d
   ```

4. View logs:
   ```bash
   docker-compose logs -f
   ```

5. Stop services:
   ```bash
   docker-compose down
   ```

### Services

| Service   | Port | Description          |
|-----------|------|----------------------|
| postgres  | 5432 | PostgreSQL database  |
| backend   | 8080 | Rewine Backend API   |

### Health Checks

- Backend API: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Kubernetes Deployment

### Prerequisites

- kubectl CLI
- kustomize (or kubectl with kustomize support)
- Access to Kubernetes/OpenShift cluster

### Deploy to Environment

```bash
# Deploy to DEV
kubectl apply -k k8s/overlays/dev/

# Deploy to UAT
kubectl apply -k k8s/overlays/uat/

# Deploy to PROD
kubectl apply -k k8s/overlays/prod/
```

### Environment Configuration

| Environment | Replicas | CPU Request | Memory Request |
|-------------|----------|-------------|----------------|
| DEV         | 1        | 100m        | 256Mi          |
| UAT         | 2        | 250m        | 512Mi          |
| PROD        | 3        | 500m        | 1Gi            |

### Secrets Management

**Important:** The `secret.yaml` file is a placeholder. Do not commit real secrets!

For production use:
- [Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets)
- [External Secrets Operator](https://external-secrets.io/)
- [HashiCorp Vault](https://www.vaultproject.io/)

## CI/CD Pipeline

The Jenkinsfile in the repository root defines the CI/CD pipeline:

### Branch Strategy

| Branch       | Environment | Approval   |
|--------------|-------------|------------|
| development  | DEV         | Automatic  |
| release/*    | UAT         | Automatic  |
| main (v*.*.*)| PROD        | Manual     |

### Pipeline Stages

1. **Checkout** - Clone repository
2. **Checkstyle** - Code style validation
3. **Unit Tests** - Run unit tests
4. **Integration Tests** - Run integration tests with Testcontainers
5. **Code Coverage** - Generate JaCoCo report
6. **SonarQube Analysis** - Static code analysis
7. **Quality Gate** - Wait for SonarQube quality gate
8. **Build** - Package application
9. **Build Docker Image** - Create container image
10. **Push Docker Image** - Push to registry
11. **Deploy** - Deploy to target environment

## Troubleshooting

### Docker Compose Issues

```bash
# Rebuild containers
docker-compose build --no-cache

# Reset everything
docker-compose down -v
docker-compose up -d --build
```

### Kubernetes Issues

```bash
# Check pod status
kubectl get pods -n rewine-dev

# View pod logs
kubectl logs -f deployment/dev-rewine-backend -n rewine-dev

# Describe pod for events
kubectl describe pod <pod-name> -n rewine-dev
```


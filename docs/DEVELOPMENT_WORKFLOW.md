# Development Workflow

This document describes the branching strategy, commit conventions, PR process, and release workflow for the Rewine project.

---

## Table of Contents

1. [Branching Model](#branching-model)
2. [Branch Naming Conventions](#branch-naming-conventions)
3. [Commit Conventions](#commit-conventions)
4. [Pull Request Process](#pull-request-process)
5. [Code Quality Gates](#code-quality-gates)
6. [Release Flow](#release-flow)
7. [Versioning](#versioning)

---

## Branching Model

Rewine uses a modified Git Flow branching model:

```
                            ┌─────────────────┐
                            │      main       │  ← Production releases
                            └────────┬────────┘
                                     │
                            ┌────────┴────────┐
                            │   development   │  ← Integration branch
                            └────────┬────────┘
                                     │
         ┌───────────────────────────┼───────────────────────────┐
         │                           │                           │
┌────────┴────────┐         ┌────────┴────────┐         ┌────────┴────────┐
│ feature/xxx     │         │   bugfix/xxx    │         │   chore/xxx     │
└─────────────────┘         └─────────────────┘         └─────────────────┘
```

### Branch Types

| Branch | Purpose | Source | Merges Into |
|--------|---------|--------|-------------|
| `main` | Production-ready code | release | Protected, prod deploys |
| `development` | Integration branch | feature/bugfix | Staging deploys |
| `release/x.y.z` | Release preparation | development | main + development |
| `feature/*` | New features | development | development |
| `bugfix/*` | Bug fixes | development | development |
| `chore/*` | Maintenance tasks | development | development |
| `hotfix/*` | Production fixes | main | main + development |

### Branch Protection Rules

#### `main`
- Requires pull request
- Requires at least 1 approval
- Requires status checks to pass
- No direct pushes

#### `development`
- Requires pull request
- Requires at least 1 approval
- Requires status checks to pass

---

## Branch Naming Conventions

### Format

```
<type>/<ticket-id>-<short-description>
```

### Examples

```bash
# Feature branches
feature/RW-123-wine-scanning
feature/RW-456-ai-sommelier-profile

# Bug fix branches
bugfix/RW-789-login-validation-error
bugfix/RW-101-wine-image-not-loading

# Chore branches (refactoring, deps, docs)
chore/RW-102-upgrade-vue-version
chore/RW-103-improve-test-coverage
chore/update-dependencies

# Hotfix branches (production issues)
hotfix/RW-999-critical-auth-bug

# Release branches
release/1.2.0
release/2.0.0-rc.1
```

### Guidelines

- Use lowercase
- Use hyphens as separators
- Keep descriptions short but meaningful
- Include ticket ID when available

---

## Commit Conventions

Rewine follows [Conventional Commits](https://www.conventionalcommits.org/) specification.

### Format

```
<type>(<scope>): <subject>

[optional body]

[optional footer(s)]
```

### Types

| Type | Description | Example |
|------|-------------|---------|
| `feat` | New feature | `feat(wines): add scan functionality` |
| `fix` | Bug fix | `fix(auth): resolve token refresh loop` |
| `docs` | Documentation | `docs: update API documentation` |
| `style` | Code style (formatting) | `style: fix linting errors` |
| `refactor` | Code refactoring | `refactor(stores): simplify wine store` |
| `perf` | Performance improvement | `perf: optimize wine list rendering` |
| `test` | Adding/fixing tests | `test(wines): add mapper tests` |
| `build` | Build system changes | `build: upgrade vite to v6` |
| `ci` | CI configuration | `ci: add coverage reporting` |
| `chore` | Maintenance | `chore: update dependencies` |
| `revert` | Revert commit | `revert: feat(wines): add scan` |

### Scopes (Optional)

| Scope | Description |
|-------|-------------|
| `wines` | Wine catalog feature |
| `events` | Events feature |
| `routes` | Wine routes feature |
| `cellar` | Personal cellar feature |
| `auth` | Authentication |
| `stores` | Pinia stores |
| `api` | API client layer |
| `ui` | UI components |
| `i18n` | Internationalization |
| `tests` | Test infrastructure |

### Examples

```bash
# Feature
feat(wines): add AI comparison functionality

Implement wine comparison using AI service.
- Add comparison page
- Create comparison store
- Integrate AI API

Closes RW-456

# Bug fix
fix(auth): prevent token refresh race condition

Multiple 401 responses were triggering parallel refresh
requests. Added a queue mechanism to ensure only one
refresh request is made at a time.

Fixes RW-789

# Breaking change
feat(api)!: change wine DTO structure

BREAKING CHANGE: WineDto now uses snake_case for all fields.
Update all mappers accordingly.
```

### Commit Message Rules

1. **Subject line**
   - Max 72 characters
   - Use imperative mood ("add" not "added")
   - No period at the end

2. **Body** (optional)
   - Separate from subject with blank line
   - Explain what and why, not how
   - Wrap at 72 characters

3. **Footer** (optional)
   - Reference issues: `Closes RW-123`, `Fixes RW-456`
   - Breaking changes: `BREAKING CHANGE: description`

---

## Pull Request Process

### Creating a PR

1. **Ensure branch is up to date**
   ```bash
   git fetch origin
   git rebase origin/development
   ```

2. **Run quality checks locally**
   ```bash
   npm run lint
   npm run test:unit
   npm run build
   ```

3. **Create PR with descriptive title**
   - Use conventional commit format
   - Example: `feat(wines): add scan functionality`

4. **Fill PR template**
   - Description of changes
   - Related issue/ticket
   - Testing performed
   - Screenshots (if UI changes)

### PR Template

```markdown
## Description
Brief description of the changes.

## Related Issue
Closes RW-XXX

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] E2E tests added/updated
- [ ] Manual testing performed

## Screenshots (if applicable)
<!-- Add screenshots here -->

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No console errors or warnings
```

### Review Process

1. **Automated Checks**
   - Lint passes
   - Tests pass
   - Build succeeds
   - Coverage threshold met

2. **Code Review**
   - At least 1 approval required
   - Address all comments
   - Re-request review after changes

3. **Merge**
   - Squash and merge preferred
   - Delete branch after merge

### Review Checklist

For reviewers:

- [ ] Code is readable and well-structured
- [ ] Changes match PR description
- [ ] No security vulnerabilities
- [ ] Error handling is adequate
- [ ] Tests cover the changes
- [ ] No unnecessary dependencies added
- [ ] Performance considerations addressed
- [ ] i18n keys added for new text

---

## Code Quality Gates

### CI Pipeline Checks

| Check | Tool | Requirement |
|-------|------|-------------|
| Linting | ESLint | No errors |
| Type Check | vue-tsc | No errors |
| Unit Tests | Vitest | All pass |
| Build | Vite | Success |
| Coverage | Vitest | ≥70% (target) |

### Local Verification

Before creating a PR, run:

```bash
# All checks
npm run lint && npm run test:unit && npm run build

# Or individually
npm run lint          # Check code style
npm run test:unit     # Run unit tests
npm run build         # Verify production build
```

### Fixing Issues

```bash
# Auto-fix lint issues
npm run lint -- --fix

# Format code
npm run format
```

---

## Release Flow

### Standard Release

```
development → release/x.y.z → main → tag vX.Y.Z
```

1. **Create release branch**
   ```bash
   git checkout development
   git pull origin development
   git checkout -b release/1.2.0
   ```

2. **Prepare release**
   - Update version in `package.json`
   - Update CHANGELOG (if maintained)
   - Final testing

3. **Merge to main**
   - Create PR: `release/1.2.0` → `main`
   - Get approval and merge

4. **Tag release**
   ```bash
   git checkout main
   git pull origin main
   git tag -a v1.2.0 -m "Release v1.2.0"
   git push origin v1.2.0
   ```

5. **Back-merge to development**
   - Create PR: `main` → `development`
   - Merge to sync any release changes

### Hotfix Release

```
main → hotfix/xxx → main → tag vX.Y.Z
           ↓
      development
```

1. **Create hotfix branch from main**
   ```bash
   git checkout main
   git pull origin main
   git checkout -b hotfix/critical-auth-bug
   ```

2. **Fix and test**

3. **Merge to main and tag**

4. **Back-merge to development**

---

## Versioning

Rewine follows [Semantic Versioning](https://semver.org/):

```
MAJOR.MINOR.PATCH
```

### Version Bumps

| Change Type | Version Bump | Example |
|-------------|--------------|---------|
| Breaking changes | MAJOR | 1.0.0 → 2.0.0 |
| New features | MINOR | 1.0.0 → 1.1.0 |
| Bug fixes | PATCH | 1.0.0 → 1.0.1 |

### Pre-release Tags

```
v1.0.0-alpha.1    # Alpha release
v1.0.0-beta.1     # Beta release
v1.0.0-rc.1       # Release candidate
v1.0.0            # Production release
```

### Tag Format

```bash
# Production release
v1.2.3

# Pre-release
v1.2.3-rc.1
v1.2.3-beta.2
```

---

## Quick Reference

### Daily Workflow

```bash
# Start new feature
git checkout development
git pull origin development
git checkout -b feature/RW-123-my-feature

# Work on feature...
git add .
git commit -m "feat(scope): description"

# Push and create PR
git push -u origin feature/RW-123-my-feature
```

### Before PR

```bash
# Update branch
git fetch origin
git rebase origin/development

# Run checks
npm run lint
npm run test:unit
npm run build
```

### Commit Examples

```bash
git commit -m "feat(wines): add search filters"
git commit -m "fix(auth): handle expired token"
git commit -m "docs: update README"
git commit -m "refactor(stores): extract pagination logic"
git commit -m "test(wines): add mapper unit tests"
```

---

## Related Documentation

- [Frontend Guide](FRONTEND.md) - Frontend development guide
- [Backend Guide](../backend/README.md) - Backend development guide
- [Architecture](ARCHITECTURE.md) - System architecture
- [Environments](ENVIRONMENTS.md) - Environment configuration
- [Troubleshooting](TROUBLESHOOTING.md) - Common issues


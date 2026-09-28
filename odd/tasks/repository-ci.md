# Repository CI

Objective: Add a small GitHub Actions gate for the Java API and Angular frontend on pull requests and pushes to `main`, using the real test commands and no production secrets. The workflow is distinct from issue #25's already merged implementation.

Base: clean Orca worktree `repository-ci` from `origin/main` at `f11e62d` (the five merged PRs' final application tree). Branch: `ci/backend-frontend`. No private task history is an ancestor. Issue #25 is still open after stacked PRs merged; do not assert it closed or modify its status as part of CI implementation.

- [x] CI-1 Establish the backend/frontend CI contract: required tool versions, disposable MySQL/Testcontainers setup, local-only placeholder configuration, ChromeHeadless, and published workflow scope. Read-only mapping first; record the plan and commit it before implementation. Rollback: this tracker document only.
- [x] CI-2 Implement one workflow with isolated API/frontend jobs, lockfile-based setup, and actionable checks. Validate YAML/workflow semantics and run proportional local checks; record exact results and commit workflow with any necessary documentation. Rollback: `.github/workflows/ci.yml` and this CI-only tracker.
- [ ] CI-3 Publish a CI PR and observe its actual hosted runs if the user explicitly authorizes push/PR. Do not merge without a separate decision; report missing or failed checks instead of claiming success. Rollback: no remote deletions without new authorization.

## CI contract

| Job | Runner/tooling | Check | Dependency |
| --- | --- | --- | --- |
| API | `ubuntu-24.04`, Java 21 (Temurin), Maven cache | `mvn -B -DargLine=-Dapi.version=1.40 test` | Disposable `mysql:8.0` service on `127.0.0.1:3306`, Docker daemon for Testcontainers `mysql:8.0`. |
| Frontend | `ubuntu-24.04`, Node 22, npm cache, preinstalled Google Chrome | `npm ci`, `npm test -- --watch=false --browsers=ChromeHeadless`, `npm run build` | No backend service. |

The API's full Spring context requires `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `SERVER_PORT`, `SPRING_MAIL_HOST/PORT/USERNAME/PASSWORD`, `JWT_SECRET`, `MERCADOPAGO_ACCESS_TOKEN/PUBLIC_KEY`, and `FRONTEND_URL`. Use non-production CI-only placeholders; no GitHub secret or live payment/API credential. Password-recovery settings and WebSocket origins have defaults. Testcontainers concurrency tests use `disabledWithoutDocker = true`; check Surefire XML for zero skips so a missing Docker daemon cannot silently pass. GitHub's official service-container guidance confirms a host job reaches mapped MySQL at `127.0.0.1:3306`. Keep read-only token permissions and trigger on all PRs plus pushes to `main`.

Out of scope: deployment, secrets from developer environments, production database access, changes to refresh-token behavior, and the original private worktree. The previously observed backend/frontend tests do not prove that a newly written GitHub workflow itself runs; hosted results must be observed after publication.

## Implementation evidence

CI-2 adds `.github/workflows/ci.yml` with independent API/frontend jobs, non-production API placeholders, MySQL health gating, a Surefire XML no-skips guard, lockfile-backed Java/npm caches, and read-only token permissions. Writer checked Ruby YAML parsing and whitespace; independent read-only verifier found no static mismatch in trigger/indentation, shell heredoc, required env, MySQL port/credentials, Maven override or Karma command. The independent whitespace check returned exit 1 for the expected new-file diff with no diagnostics. `actionlint` is unavailable locally; no full local suite or hosted run was executed for this workflow. CI-3 remains the execution proof and is gated on publication authorization.

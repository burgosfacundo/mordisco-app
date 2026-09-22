# Vercel Portfolio Deployment

## Objective

Prepare Mordisco as a portfolio-ready MVP deployed from one Vercel project: Angular at `/`, Spring Boot in a `Dockerfile.vercel` container at `/api`, and a MySQL-compatible TiDB Cloud database.

## Problem and rationale

The repository currently assumes Docker Compose, localhost service URLs, an always-running JVM, local MySQL, instance-local WebSockets, and in-process scheduled/background work. Vercel container functions are stateless, scale to zero, and terminate idle instances. Deployment must preserve the demonstrable product flows while removing assumptions that would make payments, email, authentication, or persisted data unreliable.

## Authorized scope

- Vercel multi-service deployment configuration for `mordisco-front` and `mordisco-api`.
- Production runtime/environment configuration for same-origin routing and Vercel edge TLS.
- TiDB Cloud JDBC configuration and a documented portfolio bootstrap path.
- Reliable portfolio email behavior.
- Mercado Pago sandbox checkout and public idempotent webhook processing.
- A portfolio-safe fallback for WebSocket notifications and scheduled maintenance.
- Focused tests and deployment documentation.

## Constraints and non-goals

- Target Vercel Hobby plus free TiDB Cloud.
- Prefer one public Vercel domain with `/api/*` routed to the backend.
- Use Mercado Pago sandbox for the portfolio deployment.
- Do not introduce paid infrastructure.
- Do not claim durable realtime behavior from the in-memory STOMP broker.
- Do not push, open a PR, publish, or deploy without explicit user authorization.
- Do not commit unless separately authorized by the user.
- Preserve existing local Docker Compose development where practical.

## Execution settings

- Workflow: ODD delegated direct implementation.
- TDD mode: not explicitly configured; use focused regression tests and ordinary functional checks.
- Backend runner: `cd mordisco-api && mvn test` (focused `-Dtest=...` runs allowed per task).
- Frontend runner: `cd mordisco-front && npm test -- --watch=false` where supported, plus `npm run build`.
- Delivery strategy: `ask-on-risk`.
- Chain strategy: pending; no commit is authorized yet.
- Forecast: approximately 700–1100 authored changed lines. This exceeds the 400-line review guideline and should be sliced by the work units below if commits/PRs are later authorized.

## Tasks

### DEPLOY-1 — Establish Vercel service topology and production runtime configuration

**Route:** delegated writer; multi-file write and preparation trigger.

- [x] Add root Vercel service/rewrite configuration and a backend `Dockerfile.vercel` that listens on `$PORT`.
- [x] Build/serve Angular through the appropriate Vercel service and preserve SPA route fallback. A clean dependency install and production build passed; the configured Angular static fallback was independently inspected. Live Vercel behavior remains intentionally untested.
- [x] Change the production frontend API base to same-origin `/api` while retaining localhost development behavior.
- [x] Adapt Spring production configuration for Vercel edge TLS, same-origin cookies/CORS, production profile selection, and environment validation.
- [x] Add focused configuration tests or static checks where practical. JSON parsing, backend packaging, and whitespace validation passed; no new unit tests were warranted for static deployment files.

**Acceptance checks:** Vercel configuration parses; frontend production build resolves API calls through `/api`; backend image/config uses `$PORT` without an application keystore; local development remains documented and usable.

### DEPLOY-2 — Make TiDB persistence bootstrap safe for a portfolio deployment

**Route:** delegated writer; database configuration and test changes span multiple files.

- [x] Configure JDBC TLS, bounded connection pooling, and externally supplied TiDB credentials.
- [x] Define a deterministic portfolio schema/bootstrap strategy without automatically replaying non-idempotent demo data.
- [x] Validate repository-native MySQL queries used by portfolio flows against TiDB-compatible SQL assumptions, correcting only proven incompatibilities. Static audit found no established incompatibility; live TiDB execution remains unavailable without credentials.
- [x] Document one-time demo data initialization and reset behavior in the configuration contract for inclusion in DEPLOY-5 operator documentation.

**Acceptance checks:** backend starts against an external MySQL-compatible database configuration; schema initialization behavior is explicit; no credentials are committed; focused persistence tests pass or unavailable external TiDB proof is reported honestly.

### DEPLOY-3 — Complete portfolio-safe Mercado Pago sandbox and email flows

**Route:** delegated writer; payment controller/service/security/tests and email lifecycle are multi-file changes.

- [x] Expose a public Mercado Pago webhook endpoint with validation, remote payment lookup, transactional update, and idempotency.
- [x] Remove localhost callback assumptions and select sandbox versus production checkout URLs correctly.
- [x] Correct pending/success/failure frontend routing behavior.
- [x] Make critical portfolio email delivery complete within a reliable request/transaction boundary or persist delivery intent for later processing.
- [x] Add focused payment webhook and email behavior tests.

**Acceptance checks:** repeated sandbox webhook delivery cannot duplicate state changes; unauthenticated webhook requests reach only the intended endpoint; critical emails are not abandoned as untracked `@Async` work; sandbox checkout returns to valid frontend routes.

### DEPLOY-4 — Add stateless fallbacks for realtime and scheduled behavior

**Route:** delegated writer; backend scheduling and frontend notification behavior span multiple files.

- [x] Keep WebSocket/STOMP explicitly best-effort or disable it in the Vercel profile. Production frontend disables STOMP; local development keeps it enabled.
- [x] Add a bounded polling fallback for user-visible notifications required by the portfolio demo. Production polls authoritative order state for `CLIENTE` and `RESTAURANTE` only and payment state on the pending page; courier remains persisted-state/navigation based.
- [x] Replace required `@Scheduled` behavior with idempotent callable maintenance operations suitable for cron invocation, or document/disable nonessential jobs in the Vercel profile. Production scheduling is disabled; local scheduling remains enabled.
- [x] Protect maintenance endpoints from public misuse. `POST /api/internal/maintenance` requires a production-validated secret and delegates only to idempotent/bounded cleanup operations.

**Acceptance checks:** core order/payment flows remain usable after WebSocket disconnect or backend scale-down; maintenance operations are idempotent and do not depend on an always-running JVM; frontend build and focused notification tests pass.

### DEPLOY-5 — Verify and document the deployment contract

**Route:** delegated verifier after writer completion; verification trigger.

- [x] Update README and environment examples with Vercel, TiDB, SMTP/email, Mercado Pago sandbox, callable maintenance, stateless limitations, bootstrap, and rollback guidance.
- [x] Run focused backend tests, full backend tests where environment permits, frontend tests, frontend production build, and container/config checks. Full backend now passes 173 tests with 4 Testcontainers skips caused by Java Docker discovery; frontend passes 79/79; package/build/JSON/diff and Docker image build pass.
- [x] Inspect the final diff for secrets, localhost production references, accidental generated files, and review size. No secret leak or production localhost defect found; `.codegraph/` is ignored; candidate is ~3k added lines and requires review slicing.
- [x] Record failed, skipped, unavailable, and passing checks exactly.

**Acceptance checks:** a new maintainer can configure the portfolio deployment without source edits; no secret values are tracked; all applicable tests/builds pass; external-account steps are clearly separated from repository work.

## Progress

- DEPLOY-1 is repository-verified, including a successful final `Dockerfile.vercel` image build after the Docker daemon became available.
- DEPLOY-2 is implementation-complete after independent verification; live TiDB TLS/schema/query execution remains an external hold.
- DEPLOY-3 passed 25/25 focused Java 21 tests plus backend/frontend builds, but independent review found provider-contract and transaction/UX defects.
- Two correction workers and one diagnosis worker previously timed out. After resume, a fresh diagnosis confirmed the Mercado Pago comma-delimited signature and ID-normalization correction landed correctly with focused fixtures.
- DEPLOY-3 is implementation-complete after final independent verification. Payment checkout requires a nonblank selected URL, cash orders return no checkout payload, and all webhook, routing, idempotency, and transactional-email requirements passed.
- DEPLOY-4 exploration mapped instance-local STOMP, authoritative REST polling surfaces, four scheduled jobs, and a protected callable maintenance design. Product scope is resolved to client + restaurant polling only.
- DEPLOY-4 backend maintenance implementation received a bounded cleanup correction after independent review: password-recovery cleanup now performs at most one 100-row query/delete per invocation and terminates on zero progress. Backend independent re-verification passed 21/21 focused tests plus package/diff checks.
- DEPLOY-4 is implementation-complete after final independent verification. Backend focused tests passed 21/21, frontend polling tests passed 23/23, and Maven package, Angular production build, and `git diff --check` passed.
- DEPLOY-5 is repository-complete after final independent verification. Documentation, safe environment examples, self-contained context tests, Docker context exclusions, and verification records are aligned with source.

## Verification evidence

### DEPLOY-1

- Vercel JSON parsing, clean Angular install/build, Maven package, configuration inspection, and `git diff --check`: passed.
- Docker image build: passed in final DEPLOY-5 verification (`mordisco-api-vercel:deploy5-verify`).
- npm install reported 53 dependency vulnerabilities for DEPLOY-5 triage.

### DEPLOY-2

- Final Spring configuration suite: 8/8 tests passed.
- Maven package and `git diff --check`: passed.
- Verified external credentials, TLS binding, bounded Hikari defaults/overrides, fixed `validate`, explicit `schema-bootstrap` update, disabled production seed replay, and development isolation.
- Live TiDB smoke test: unavailable without credentials.

### DEPLOY-3

- Initial independent Java 21 focused suite: 25/25 tests passed without experimental flags.
- Maven package, Angular production build, credential scan, and `git diff --check`: passed.
- Signature re-diagnosis: canonical comma parsing, malformed/duplicate handling, alphanumeric lowercasing, non-alphanumeric preservation, and fixtures are present.
- Corrected focused backend suite: 28/28 passed under Java 21; focused frontend pending tests: 3/3 passed.
- Maven package, Angular production build, and `git diff --check`: passed after correction.
- Independent verification passed every DEPLOY-3 requirement except backend rejection of blank provider checkout URLs. The follow-up correction passed 10/10 focused checkout tests and the expanded 34/34 backend suite; Maven package and `git diff --check` passed.
- Final re-verification ran 38/38 backend and 3/3 frontend tests plus builds/checks successfully, but failed acceptance because the tests encoded a production cash-order constructor regression.
- The cash-order correction added focused coverage (5/5), retained 38/38 passing DEPLOY-3 backend tests, and passed Maven package plus `git diff --check`.
- Final independent DEPLOY-3 verification: PASS. Java 21 suite 39/39, pending-page suite 3/3, Maven package, Angular production build, and `git diff --check` all passed.

### DEPLOY-4

- Final independent verification: backend 21/21 and frontend 23/23 focused tests passed.
- Maven package, Angular production build, and `git diff --check`: passed.
- Live scale-to-zero, TiDB-backed polling, and external cron invocation remain deployment holds.

### DEPLOY-5

- Java 21 full backend suite: 177 discovered, 173 passed, 4 MySQL Testcontainers skipped, 0 failures/errors.
- Full frontend suite: 79/79 passed.
- Maven package, Angular production build, Vercel JSON, `git diff --check`, Docker daemon check, and `Dockerfile.vercel` image build: passed.
- Four Docker-backed MySQL tests remain skipped because Testcontainers Java discovery cannot use the available Docker Desktop daemon (`/var/run/docker.sock` absent; Desktop strategy returned HTTP 400/empty info).
- No production secrets, localhost defects, tracked generated files, or CodeGraph artifacts were found.
- Candidate size is approximately 59 files and 3,293 changed lines; delivery must be review-sliced if commits/PRs are authorized.
- External holds: live TiDB TLS/schema/query execution, Mercado Pago sandbox/webhook delivery, SMTP, Vercel routing/scale-to-zero, maintenance cron, OpenWeatherMap, and public smoke checks.
- npm previously reported 53 dependency vulnerabilities; remediation was not included because dependency upgrades were outside this deployment scope.

### Review tooling

- Native risk assessment is unavailable because the package-local Gentle AI binary is missing; independent verifier runs were used under the high-risk fallback plan.

## Next step

Repository implementation is complete. If delivery is authorized later, split the candidate into reviewable work-unit commits/PR slices before any push; then perform the external deployment smoke checks with real provider credentials.

# Vercel Cold-Start Diagnosis

## Objective and problem

Determine why the Java 21 Spring Boot backend in the existing Vercel Preview intermittently returns 500 while other requests succeed. One observed startup reported 26.49 seconds in Spring (28.01 seconds process elapsed); failed invocations reported that the container did not accept connections on `PORT=80` before their observed timeout. This is not evidence of a permanently incorrect port. Preserve the same-origin Angular/API topology unless measured evidence requires a product decision.

## Scope and constraints

- Work only on `feat/vercel-portfolio-deployment`; keep the existing Vercel project and TiDB Preview database intact.
- Diagnose before optimizing. Instrument bounded startup milestones without logging URLs, identities, credentials, tokens, SQL, or request data.
- Do not disable TLS identity verification, production schema validation, payment signing, secure cookies, or startup configuration checks for speed.
- Do not enable legacy `data.sql` or the synthetic `demo-seed` profile; importing the user's requested legacy-derived catalog is a separate future change.
- No agent-triggered Vercel deployment, TiDB mutation, push, PR, or merge. A deployment requires a fresh user decision.
- Vercel's documented 30-second Preview idle scale-down is not a documented fixed startup limit. Compare actual request failure logs with process milestones rather than asserting such a limit.

## Execution settings

- Workflow: ODD; bounded multi-file writes delegated to `gentle-ai-worker`.
- TDD: no explicit enabled mode in current project task record; ordinary focused regression tests and functional checks. Java 21.0.9; runner `cd mordisco-api && JAVA_HOME=/Users/burgosfacundo/Library/Java/JavaVirtualMachines/ms-21.0.9/Contents/Home PATH="$JAVA_HOME/bin:$PATH" mvn -q -Dtest=<focused-test> test`, then `mvn -q -DskipTests package` under the same Java environment.
- Delivery: one Conventional Commit per complete work unit on the existing feature branch; no push or deployment without separate authorization. Review-slice risk: existing branch is already much larger than the ~400-line guideline, so do not open one cumulative PR without a review plan.
- Forecast: instrumentation/test/docs ~120–250 authored lines; later optimization scope unknown until measured. Strategy: ask-on-risk.

## Tasks

### COLD-1 — Add non-secret startup phase evidence

**Route:** delegated writer; startup instrumentation, focused tests, and operator instructions span multiple files.

- [x] Record bounded monotonic elapsed startup milestones for Spring readiness and the named entity-manager factory bean's initialization callbacks, with no sensitive values or extra SQL/remote calls. Failure remains uncaught. Full live JPA validation coverage is unproven and stays a COLD-2 measurement question.
- [x] Test callback milestone ordering, fixed-value logging, production-profile wiring, and failure propagation; add Preview log-correlation guidance. Focused tests: 3 passed, 0 failed/skipped; Java 21 Maven package and `git diff --check` passed. Independent verifier confirmed these results but cautioned that synthetic factory tests do not measure real TiDB validation. Parent spot check reran focused tests and diff check successfully.
- [x] Commit this complete code/tests/operator-guidance work unit as `06d6f38` (`chore(api): measure safe startup milestones`). Focused 3/3, package, diff check, independent verifier, and parent spot check passed. Feature-branch push was explicitly authorized; no deployment was authorized.

**Acceptance:** The next Preview run can distinguish application initialization from JPA initialization and report elapsed timings without a connection URL or secret. Normal production validation remains unchanged.

### COLD-2 — Measure and decide the bottleneck

**Route:** user-run Preview redeploy and read-only log analysis; no agent deployment.

- [ ] With user authorization, deploy the committed diagnostic candidate to the existing feature Preview and collect several cold/warm startup/request observations with redacted logs.
- [ ] Decide whether the dominant interval is JPA/TiDB, framework initialization, or platform/container readiness. If timing cannot distinguish these, record the remaining uncertainty instead of guessing.
- [ ] Record measured evidence; do not commit a checkbox alone or claim stable behavior from one 200 response.

**Acceptance:** At least two reproducible cold-start attempts, successes and failures with timestamps, and one targeted optimization hypothesis.

### COLD-3 — Apply only a measured safe optimization

**Route:** pending measurement; delegated writer if multiple non-trivial files are affected.

- [ ] Implement only the bounded, evidence-backed optimization; preserve production safety and development behavior.
- [ ] Run focused/full applicable checks, compare repeated cold-start behavior after a separately authorized Preview deployment, and document remaining risk.
- [ ] Commit the complete optimization work unit and record verification and commit identity. If no safe optimization gives adequate margin, report that Vercel Hobby is unsuitable instead of masking the issue.

**Acceptance:** Measurable improvement and repeated reliable cold requests, or an explicit evidence-based stop with alternative hosting decision.

## Current evidence and next step

- Existing Preview intermittently returns JSON 200 or `FUNCTION_INVOCATION_FAILED` 500 for the same `/api/restaurantes/ubicacion/promociones` route.
- One successful startup log: `Started MordiscoApiApplication in 26.49 seconds (process running for 28.01)`; failed Vercel invocation reported no listener on `PORT=80` before its observed ~28.6-second timeout. No component-level timing exists yet.
- `Dockerfile.vercel` passes the runtime `PORT` to Java and production properties resolve the same variable. Hibernate validates TiDB schema at normal `prod` startup; its time contribution is unmeasured. The backend STOMP broker remains enabled in `prod`, though the production frontend uses polling.
- Initial research: Vercel documents Preview scale-to-zero after 30 seconds idle, not a fixed 30-second startup deadline in the cited Docker guide.
- COLD-1 instrumentation is committed locally as `06d6f38`; the task-evidence update belongs in the next documentation commit. The instrumentation brackets synchronous factory-bean initialization callbacks; it is not proof of real TiDB validation duration. User explicitly authorized feature-branch commit and push, but not deployment. Next: push the reviewed commits, then request a separate user-run Preview redeploy for COLD-2. Do not redeploy automatically.

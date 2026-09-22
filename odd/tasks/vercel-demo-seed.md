# Vercel Preview Demo Seed

## Objective

Add an explicit, safe, idempotent recruiter demo-data bootstrap for the isolated TiDB Preview database without enabling seed behavior in normal production.

## Constraints

- Activation must require the exact intent represented by `prod,schema-bootstrap,demo-seed`.
- Normal `prod` keeps `ddl-auto=validate` and `spring.sql.init.mode=never`.
- Do not reuse the legacy 617-line `data.sql`, fixed IDs, real-looking identities, external images, admin credentials, or payment-provider identifiers.
- Use synthetic `.invalid` accounts and obvious demo-only credentials.
- Reruns must not duplicate, delete, or silently overwrite recruiter-created data.
- Vercel/TiDB concurrency must be coordinated through database state, not JVM locks.
- No live credentials or deployment changes are performed by repository implementation.

## Tasks

### SEED-1 — Preserve production behavior under combined profiles

- [x] Replace comma-string profile checks with Spring profile membership checks where production security behavior depends on `prod`.
- [x] Add a fail-closed demo-seed activation guard requiring both `prod` and `schema-bootstrap`.
- [x] Verify combined profiles preserve production CSRF/cookie/scheduling behavior and SQL initialization remains disabled.

### SEED-2 — Add idempotent synthetic recruiter data

- [x] Add a profile-gated runner and transactional seed service.
- [x] Coordinate concurrent attempts with a unique database seed marker.
- [x] Seed the smallest useful synthetic scenario: client, restaurant owner, courier, addresses, restaurant/menu/products, opening hours, and persisted demo order state without external calls.
- [x] Reuse validated natural-key matches, insert missing seed-owned records, and fail on conflicts rather than overwrite data.
- [x] Add focused first-run, rerun, partial-repair, conflict, and concurrency-oriented tests.

### SEED-3 — Document, verify, commit, and push

- [x] Document one-time `prod,schema-bootstrap,demo-seed` activation, demo credentials, immediate return to `prod`, and reset limitations.
- [x] Run focused Java 21 tests, full backend tests, package, Angular production build, and `git diff --check`.
- [x] Independently verify profile safety, idempotency, and absence of external calls/secrets.
- [ ] Create the final documentation/evidence commit and push `feat/vercel-portfolio-deployment` without opening a PR or deploying.

## Evidence

- SEED-1 independent verification: PASS; 24/24 focused Java 21 tests, Maven package, and `git diff --check` passed.
- Combined profiles preserve production validation, CSRF, secure strict cookies, and disabled scheduling; SQL initialization remains `never`.
- SEED-2 independent verification: PASS; 33/33 focused tests, Maven package, Angular asset build, and diff check passed.
- Final repository verification: PASS; 33/33 focused tests, full backend 196 passed / 11 Testcontainers skipped / 0 failed, Maven package, Angular production build, secret/URL audit, and diff check passed.
- MySQL Testcontainers and live TiDB execution remain external proof because Docker is unavailable to Testcontainers.
- Work-unit commits: `fa90b43` (combined-profile production safety) and `4d46371` (idempotent recruiter dataset).

## Acceptance checks

- Normal `prod` never seeds.
- `demo-seed` without both required profiles fails startup.
- First eligible startup creates one coherent synthetic dataset; subsequent/concurrent starts create no duplicates.
- Existing conflicting natural-key data fails visibly and is not overwritten.
- Demo credentials are documented as public Preview-only credentials.
- Repository tests pass; live TiDB execution remains an explicit external verification step.

## Verification plan

```bash
cd mordisco-api
export JAVA_HOME="/Users/burgosfacundo/Library/Java/JavaVirtualMachines/ms-21.0.9/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
mvn -q -Dtest=DemoSeedConfigurationTest,DemoSeedServiceTest,TiDbPersistenceConfigurationTest,ProductionConfigurationValidatorTest,SecurityConfigurationTest test
mvn -q test
mvn -q -DskipTests package
cd ..
git diff --check
```

## Review workload

Actual candidate is approximately 21 files and 2,120 added lines because schema-backed integration tests and explicit synthetic graph validation are substantial. Review-size risk is high; keep this isolated from unrelated deployment changes and require explicit review slicing or a size exception before PR review.

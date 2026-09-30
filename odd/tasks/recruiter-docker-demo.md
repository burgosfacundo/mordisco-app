# Recruiter Docker demo (#39)

## Outcome and scope
Docker-only local recruiter workflow: ./start.sh builds/starts and seeds, ./stop.sh preserves data, ./reset-demo.sh requires exact confirmation and removes only demo project resources/data. Bash, Docker Compose v2 and first-build Internet required; no Java/Node/MySQL/curl/OpenSSL on host. Local Mailpit replaces mandatory Gmail credentials. Mercado Pago is optional and missing keys produce one controlled notice with cash alternative/cart retained. Weather is unimplemented; key alone enables nothing. README and demo.env.example document setup, own keys, webhook reachability and production boundaries.

## Authorization and route
User approved feat/recruiter-docker-demo, strict TDD, isolated manual-regression fixes and exact singular pedido-service scope. Later confirmed all manual checks, including accents after reset, and authorized commits/push/PR. GitHub target github.com/burgosfacundo/mordisco-app: reuse existing issue39, direct verified maintainer instruction applied status:approved while preserving bug/phase. User explicitly accepted one oversized integrated PR; no cosmetic reduction of tests/docs. No merge authorization. Single coherent work unit: usable self-contained demo with scripts/config/API/UI/tests/docs together. Native review and delivery handled separately.

## Completed work
- [x] Persistent Docker lifecycle, bounded container readiness/random JWT, loopback ports and receipted transactional seed.
- [x] Explicit utf8mb4 import clients and accent/emoji round-trip proof.
- [x] Optional MP guards before SDK/order/payment persistence, safe503, scoped interceptor ownership and checkout cart retention.
- [x] Independent mail sender versus SMTP auth, demo sender, non-demo username fallback and recovery delivery proof.
- [x] README/template and regression tests; manual validation completed.

## Verification
Shell TDD RED/GREEN and final lifecycle contract/syntax pass. Docker ShellCheck on isolated script copy passes0; git diff --check0. Full frontend108 tests/build pass. Focused payment13, mail/listener/recovery11, integrated checkout6 specs pass. Latest full backend mvn clean verify with synthetic environment and separate schema: Surefire184 tests/0 failures/0 errors/0 skipped, no Failsafe reports/integration-phase claim.

Actual seeded demo start/repeat/stop-start pass with stable receipt1/users46/restaurants15/products90/orders85, HTTP200 and captured synthetic SMTP. Later isolated application recovery: one HTTP200 request, Mailpit0->1 and valid sender/recipient. Corrected importer stores accents and emoji byte-for-byte. User manually confirmed one MP notice, recovery mail, and correct names after reset/fresh seed. Earlier schema-preparation failure resolved by authorized stdin SQL; historical failure cause not claimed.

## Safety and limits
No private .env read, real payments/provider validity checks, user-runtime mutations or automatic data repair. Existing mojibake persists until separately chosen reset/repair; user personally reset and validated. Reset destructive path tested with mocks, not executed by agents. Runtime fault-injected rollback/contention/hung cleanup not exercised. Mailpit is local, not real email delivery. JWT changes each default start invalidating sessions and exists in container environment. Demo credentials never production; ports loopback. Compose doesn't forward optional APP_MAIL_FROM, but valid demo default works.

Verifier-owned temp copies/images/volumes retained; own test services stopped without deletion. Recheck ownership/ports before future Docker changes due concurrent user suites. #28/#15 remain open; this implementation links only #39. No repository-wide infrastructure/security scope expansion.

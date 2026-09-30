# Portfolio release preparation

## Outcome and boundaries
Prepare a credible Mordisco v1.0.0 candidate without publishing it. Preserve the thesis README and group authorship. Keep the demo Docker-only, with two linked walkthrough videos. No deployment, Flyway, refactoring, dependency upgrades, tag, push, GitHub Release or merge in this work unit. Publication requires a separate human decision after verification of the exact candidate.

## Tasks
- [x] Commit the already reviewed README video links as a focused work unit on the feature branch. Check: exact two Drive URLs, accurate captions, no LinkedIn link; read back diff. Verifier: `git diff --check` passed, docs-only tests/runtime N/A. Commit: 0d102cc (`docs(demo): link order walkthrough videos`).
- [x] Align API/frontend version metadata and add concise release notes with honest demo limits. Independent verifier: Node manifest consistency, Maven `-DskipTests validate` and `git diff --check` passed; dependencies unchanged; wording corrected after readback. Tests/builds not claimed. Commit: cf11bf7 (`chore(release): prepare v1.0.0 metadata and notes`). Native review unavailable (`candidate view directory is unsafe or writable`); user explicitly authorized committing this candidate without it.
- [x] Record exact-candidate local verification and remaining gates without creating a tag or release. On cf11bf7: Node manifest consistency and Maven `-DskipTests validate` passed; frontend 108/108 tests and production build passed. `git diff --check` passed. This is local evidence only, not full backend or CI evidence. Documentation commit: pending.
- [ ] Blocker before v1.0.0 publication: run backend tests against an owned isolated MySQL instance and obtain green remote CI for the exact candidate after an authorized push/PR. Recheck the final SHA; no tag/release until those gates are evidenced. Demo smoke test from clean clone remains a separate manual acceptance gate.

## Routing and checks
- Task 1: inline Git commit after delegated read-only docs verification; previously approved README candidate. No runtime boundary for link-only documentation.
- Task 2: delegated writer because version metadata and release notes touch four files. Effective TDD: not established by project/session configuration; no behavior changes, so run ordinary manifest validation instead. Allowed edits: `mordisco-api/pom.xml`, `mordisco-front/package.json`, `mordisco-front/package-lock.json`, `CHANGELOG.md`. Release is a portfolio candidate, not a production claim. Estimated authored diff under 100 lines; delivery strategy `ask-on-risk` if cumulative diff exceeds ~400 lines.
- Task 3: delegated read-only verifier for commands and candidate evidence; parent retains publication decision. Task 4 is blocked on explicit delivery authorization and an owned test database; do not conflate skipped checks with pass.

## Evidence
- Base: main at 2f85131f25e93b82052f365db3098361a1f271a3. Feature branch: docs/portfolio-release-prep.
- Existing demo videos: client flow and multi-role delivery flow; local Docker demo is not a public production service.
- Existing metadata before work: API 0.0.1-SNAPSHOT, frontend 0.0.0; CI runs on main/PR, not tags.
- Local verifier at cf11bf7: frontend 108/108 and build PASS, Maven validate PASS (tests skipped), manifest versions PASS, whitespace PASS. Backend full tests SKIPPED because safe isolated MySQL ownership was not established. GitHub CI PENDING because no push/PR was authorized. Clean-clone Docker acceptance NOT RUN. Native review for metadata/notes unavailable and waived for that candidate; it is not a review approval.

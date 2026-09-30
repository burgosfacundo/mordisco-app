# Portfolio release preparation

## Outcome and boundaries
Prepare a credible Mordisco v1.0.0 candidate without publishing it. Preserve the thesis README and group authorship. Keep the demo Docker-only, with two linked walkthrough videos. No deployment, Flyway, refactoring, dependency upgrades, tag, push, GitHub Release or merge in this work unit. Publication requires a separate human decision after verification of the exact candidate.

## Tasks
- [x] Commit the already reviewed README video links as a focused work unit on the feature branch. Check: exact two Drive URLs, accurate captions, no LinkedIn link; read back diff. Verifier: `git diff --check` passed, docs-only tests/runtime N/A. Commit: 0d102cc (`docs(demo): link order walkthrough videos`).
- [ ] Align API/frontend version metadata and add concise release notes with honest demo limits. Check: versions agree, lockfile consistent, focused manifest checks. Commit: pending.
- [ ] Verify the exact release candidate and report remaining manual/publication gates. Check: configured CI or equivalent verification result, clean diff; do not create a tag or release. Commit: pending if source changes are required.

## Routing and checks
- Task 1: inline Git commit after delegated read-only docs verification; previously approved README candidate. No runtime boundary for link-only documentation.
- Task 2: delegated writer because version metadata and release notes touch four files. Effective TDD: not established by project/session configuration; no behavior changes, so run ordinary manifest validation instead. Allowed edits: `mordisco-api/pom.xml`, `mordisco-front/package.json`, `mordisco-front/package-lock.json`, `CHANGELOG.md`. Release is a portfolio candidate, not a production claim. Estimated authored diff under 100 lines; delivery strategy `ask-on-risk` if cumulative diff exceeds ~400 lines.
- Task 3: delegated read-only verifier for commands and candidate evidence; parent retains publication decision.

## Evidence
- Base: main at 2f85131f25e93b82052f365db3098361a1f271a3. Feature branch: docs/portfolio-release-prep.
- Existing demo videos: client flow and multi-role delivery flow; local Docker demo is not a public production service.
- Existing metadata before work: API 0.0.1-SNAPSHOT, frontend 0.0.0; CI runs on main/PR, not tags.

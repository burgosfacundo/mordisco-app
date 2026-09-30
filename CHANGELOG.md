# Changelog

## v1.0.0

Mordisco is a UTN thesis project: a multi-role food-delivery platform for customers, restaurants, couriers, and administrators, presented as a local portfolio demo.

- Provides a Docker-only local recruiter demo via Compose, with the frontend, API, MySQL, and Mailpit. It is not a production service or public deployment.
- Repository CI is configured for pull requests and pushes to `main`: independent jobs run API tests against MySQL (checking Surefire for skipped tests) and frontend tests plus a frontend build. This describes the workflow, not a claim that CI was run for this release candidate.
- Mercado Pago testing requires your own test credentials. End-to-end webhook notifications also require a publicly reachable HTTPS webhook URL; localhost alone is insufficient.
- Weather functionality is not implemented.

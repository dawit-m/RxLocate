# RxLocate professional refinement roadmap

## Product direction

RxLocate is a pharmacy inventory and medicine-discovery platform for Ethiopia. The portfolio version should demonstrate more than a CRUD demo: it should show secure authentication, reliable inventory rules, a usable patient search experience, an integration-ready API, automated tests, and professional delivery practices.

## Current baseline

The uploaded application already has a clear domain concept, a Spring Boot MVC structure, pharmacy inventory management, public medicine search, Google Maps links, and a USSD flow. The highest-risk gaps are plaintext passwords, database credentials committed to source, destructive GET actions, missing authorization checks, direct entity exposure from the API, weak validation, inconsistent dependency injection, and minimal test coverage.

## Delivery phases

### Phase 1 — Production foundation

- Make configuration environment-driven and add a safe local development profile.
- Hash passwords with BCrypt and centralize authentication in a service.
- Replace destructive GET actions with POST actions and verify the owning pharmacy.
- Add bean validation for pharmacy and medicine input.
- Improve error handling and return safe API responses.
- Add H2-backed tests so the project can be verified without a local MySQL server.
- Rewrite the README with setup, architecture, API examples, screenshots/demo instructions, and known limitations.

### Phase 2 — Product quality

- Add medicine availability rules: expired and zero-stock products are not advertised as available.
- Add pagination, sorting, and location-aware search.
- Add dashboard summaries and audit-friendly timestamps.
- Improve responsive UI, empty states, feedback messages, accessibility labels, and consistent visual tokens.
- Add integration tests for registration, login, search, inventory ownership, and USSD flows.

### Phase 3 — Portfolio delivery

- Add Docker Compose for the application and MySQL.
- Add GitHub Actions for build, test, and formatting checks.
- Add OpenAPI documentation for the REST endpoints.
- Add seed/demo data and a scripted demo flow.
- Deploy a public demo and add a short architecture diagram, screenshots, and a case study to the portfolio.

## Portfolio positioning

The project should be presented as: **“RxLocate — a secure pharmacy inventory and medicine discovery platform with web, REST, and USSD access.”** The portfolio story should explain the real user problem, the design decisions, the security improvements, the testing strategy, and the trade-offs rather than only listing technologies.

## Definition of done for the first pass

A fresh clone must be able to start with documented environment variables, tests must run without developer-specific credentials, passwords must never be stored or returned in plaintext, inventory mutations must be authorized, and the README must make the project easy for a reviewer to run in under ten minutes.

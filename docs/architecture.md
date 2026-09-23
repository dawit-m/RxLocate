# RxLocate architecture

RxLocate is a **layered Spring Boot application** that connects patients with pharmacy inventory through a browser, REST, and USSD interfaces. The architecture keeps HTTP concerns, business rules, persistence, and presentation separate so the project can evolve without turning the controllers into a second service layer.

```text
Patient browser       Pharmacy browser       REST client       USSD gateway
      |                     |                    |                 |
      +---------------------+--------------------+-----------------+
                            |
                    Interface / delivery layer
              Spring MVC controllers + Thymeleaf views
                            |
                    Application / use-case layer
       PharmacyDashboardService · MedicineService · SaleService
                            |
                       Domain model
             Pharmacy · Medicine · Sale ownership rules
                            |
                    Persistence / infrastructure
              Spring Data JPA repositories · H2 / MySQL
```

## Layer responsibilities

| Layer | Location | Responsibility |
| --- | --- | --- |
| Delivery | `controller/`, `ussd/`, `templates/` | Parse requests, resolve the active pharmacy session, choose a view, and redirect after mutations. Controllers do not calculate dashboard metrics or own persistence rules. |
| Application | `service/` | Own inventory, authentication, sales, and dashboard use cases. `PharmacyDashboardService` builds one read-only snapshot shared by the dashboard, reports, inventory, and sales views. |
| Domain | `entity/` | Model pharmacy ownership, medicines, and immutable sale facts such as captured unit price, cost, revenue, profit, and timestamp. |
| Persistence | `repository/` | Encapsulate database access through Spring Data JPA. Ownership-sensitive lookups use the pharmacy as part of the query boundary. |
| Configuration | `config/`, `application*.properties` | Provide environment-driven runtime configuration, password hashing, demo/test profiles, and database selection without committing secrets. |

## Pharmacy workspace routes

The pharmacy workspace uses one shared Thymeleaf shell with focused server-rendered views. This keeps navigation bookmarkable and deployable without a client-side build pipeline.

| Route | Read model | Mutations |
| --- | --- | --- |
| `GET /pharmacy/dashboard` | Operational KPIs, alerts, and recent activity | Links to focused workflows |
| `GET /pharmacy/medicines` | Medicine catalogue, stock, expiry, and margin | Add, increase, decrease, and delete medicine |
| `GET /pharmacy/sales` | Sale form and transaction history | Record sale and atomically decrement stock |
| `GET /pharmacy/reports` | Revenue, realized gross profit, inventory value, and availability risk | Read-only |

All four routes use `PharmacyDashboardService.DashboardSnapshot`, which gives the views a consistent and testable read model. The controller only maps snapshot fields into the Thymeleaf model and delegates mutations to domain services.

## Data and security boundaries

A pharmacy is resolved from the server-side HTTP session after login. Inventory mutations always query by both medicine ID and the logged-in pharmacy, preventing one pharmacy from changing another pharmacy's stock. Passwords are encoded with BCrypt before persistence and are never exposed by the public medicine API. Sale records capture price and cost at the moment of sale so later price changes do not rewrite historical margin reporting.

The public search path only exposes medicines that are in stock, not expired, and associated with a pharmacy. The REST surface returns safe medicine information rather than JPA entities with credential or recursive relationship data. USSD uses the same medicine search service, which keeps the availability rules consistent across channels.

## Integrations and runtime profiles

- **Web:** Spring MVC and Thymeleaf under `/client` and `/pharmacy/*`.
- **REST:** `GET /api/medicines/search?name=...` for medicine discovery.
- **USSD:** `POST /api/ussd` with `sessionId` and `text` for basic-phone access.
- **Persistence:** MySQL for deployment, H2 for tests and the demo profile.
- **Delivery:** Docker Compose runs the application and MySQL; GitHub Actions runs the Maven test suite and packaging checks.

## Quality and extension strategy

The project favors explicit services and small controllers over a broad generic abstraction layer. This keeps the domain readable for a portfolio reviewer while making the next extensions predictable: pagination belongs in repository queries and read models, audit history belongs beside sales and inventory mutations, and authorization remains centralized at the service boundary. The dashboard snapshot service is the first example of this pattern and prevents presentation-specific calculations from spreading across controllers and templates.

Tests run against an isolated H2 profile, so a fresh clone can verify the application without developer-specific database credentials. The demo profile seeds a realistic catalogue for a short product walkthrough, while environment-driven properties keep production configuration outside source control.

# Restro Restaurant Management - Phase 3

Phase 3 delivers a role-based, REST-backed restaurant-management application.
The frontend is split into independently deployable microfrontends, while the
Spring Boot service owns authentication and persistent operational data.

## What is included

- Live login and role-based access for administrators, managers, and kitchen staff.
- Menu and price-cycle management.
- Customer management and customer selection during order creation.
- Live order creation with cart totals, notes, table numbers, and price cycles.
- Kitchen order queue with order acceptance.
- Invoice creation, tax/discount calculation, and payment tracking.
- Dashboard and reports generated from saved data rather than mock arrays.
- User administration for administrators.

## Architecture

```text
Angular Shell (:4200)
  |- React Dashboard remote (:5173)
  |- Vue Orders remote (:5174)
  `- Angular Reports remote (:4205)
             |
             `--> restaurant-service Spring Boot API (:8081)
                       |
                       `--> MySQL (production) or H2 (local profile)
```

The shell loads the remotes at runtime through a shared `mount` / `unmount`
contract. The applications do not share source code, but exchange the logged-in
user and authentication token through that contract.

## Services and modules

| Component | Technology | Responsibility |
|---|---|---|
| `frontend/shell` | Angular | Login, authentication state, layout, routing, role guards, inventory, billing, customers, kitchen, and users |
| `frontend/dashboard-mf` | React | Live revenue and order dashboard, top items, and operational summary |
| `frontend/orders-mf` | Vue 3 | Menu browsing, cart, customer selection, order placement, and running orders |
| `frontend/reports-mf` | Angular | Revenue trend, summary metrics, and top-selling-item reporting |
| `restaurant-service` | Spring Boot | Authentication, users, menu items, customers, orders, invoices, and reports |

## Roles

| Role | Access |
|---|---|
| `ADMIN` | Dashboard, orders, inventory, billing, customers, users, and reports |
| `MANAGER` | Dashboard, orders, inventory, billing, customers, and reports |
| `KITCHEN` | Kitchen queue and order acceptance |

## Local setup

### 1. Start the backend

Install Java 21 and Maven, then run the service from the repository root:

```powershell
cd restaurant-service
$env:SPRING_PROFILES_ACTIVE='local'
mvn spring-boot:run
```

The `local` profile uses an in-memory H2 database and creates this development
account:

```text
username: admin
password: admin123
```

Override it with `RESTRO_BOOTSTRAP_ADMIN_USERNAME`,
`RESTRO_BOOTSTRAP_ADMIN_PASSWORD`, and `RESTRO_BOOTSTRAP_ADMIN_NAME` when
needed. For a persistent environment, provide `DB_URL`, `DB_USERNAME`, and
`DB_PASSWORD` for MySQL.

### 2. Start the frontend applications

Open four terminals from `frontend`:

```powershell
cd dashboard-mf; npm install; npm run dev
cd orders-mf; npm install; npm run dev
cd reports-mf; npm install; npm run dev
cd shell; npm install; npm start
```

Open `http://localhost:4200` and sign in with the local development account.

## Main API areas

| Area | Base path |
|---|---|
| Authentication | `/api/auth` |
| Users | `/api/users` |
| Menu items | `/api/v1/items` |
| Orders | `/api/v1/orders` |
| Customers | `/api/customers` |
| Invoices | `/api/billing/invoices` |
| Dashboard and reports | `/api/reports` |

The UI uses these endpoints for all business data. Menu items, customers,
orders, invoices, and report totals are created from live API responses rather
than hard-coded frontend fixtures.

## Build verification

The following production builds complete successfully:

```powershell
cd frontend/dashboard-mf; npm run build
cd frontend/orders-mf; npm run build
cd frontend/reports-mf; npm run build
cd frontend/shell; npm run build
```

Backend test execution requires Maven to be installed or a Maven wrapper to be
added to `restaurant-service`.

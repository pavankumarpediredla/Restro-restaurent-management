# Restro POS - Single Angular Frontend

This is the complete standalone Angular frontend for the restaurant management
system. It replaces the previous shell plus React, Vue, and Angular remote
microfrontends with one Angular build.

## Included features

- Authentication, layout, routing, and role-based navigation
- Dashboard, orders, reports, inventory, billing, kitchen, customers, tables,
  users, and attendance pages
- Direct API integration with `restaurant-service`; no remote frontend servers
  or runtime module loading are required

## Run locally

```bash
cd frontend/angular-frontend
npm install
npm start
```

Open http://localhost:4200. With the backend running locally, the frontend uses
`http://localhost:8081`; deployed builds use the configured production API.

## Build

```bash
npm run build
```

Deploy the generated `dist/restro-angular-frontend/browser` directory as one
static frontend bundle.

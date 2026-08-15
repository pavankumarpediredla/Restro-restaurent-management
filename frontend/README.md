# Restro POS Frontend

The deployable frontend is the standalone Angular application in
[`angular-frontend`](./angular-frontend). It contains the complete restaurant
management UI in one project and produces one static deployment bundle.

## Run

```bash
cd angular-frontend
npm install
npm start
```

The app runs on http://localhost:4200 and uses the local restaurant service at
http://localhost:8081 during development.

## Build

```bash
cd angular-frontend
npm run build
```

Deploy `angular-frontend/dist/restro-angular-frontend/browser`.

The `shell`, `dashboard-mf`, `orders-mf`, and `reports-mf` directories are the
previous microfrontend implementation and are retained only as source-history
references. They are not required to run or deploy the Angular frontend.

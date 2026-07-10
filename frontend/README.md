# Restro POS — Frontend (Microfrontends)

A restaurant management app split into 4 independently-built, independently-deployable
apps, in **4 different frameworks**, stitched together at runtime by one Shell.

```
frontend/
├── shell/          Angular  — login, sidebar, header, footer, routing, loads everything below
├── dashboard-mf/    React    — Dashboard: stat cards, sales chart, revenue chart, recent orders
├── orders-mf/       Vue 3    — Orders: New Order (menu + cart), Running Orders
└── reports-mf/      Angular  — Reports: revenue trend, top-selling items
```

## The one rule that makes this work

The Shell never imports any of the other three apps' source code, and doesn't know
React from Vue from Angular. It only knows **one contract**, which every remote
implements:

```ts
export function mount(container: HTMLElement, props: Record<string, unknown>): void
export function unmount(container: HTMLElement): void
```

At runtime, the Shell does a plain browser `import(url)` of whatever URL is
configured for each remote in `shell/src/app/core/remote.config.ts`, gets back
an object with `mount`/`unmount`, and calls them. That's the entire integration
— no shared build step, no monorepo build tool required, no framework-specific
federation runtime. Each app is genuinely standalone; you could hand `orders-mf`
to a different team using a completely different repo and it would still work,
as long as it keeps serving a `mount`/`unmount` module at the agreed URL.

## Run the whole thing locally

Open 4 terminals:

```bash
# Terminal 1 — Dashboard (React)
cd dashboard-mf && npm install && npm run dev      # :5173

# Terminal 2 — Orders (Vue)
cd orders-mf && npm install && npm run dev         # :5174

# Terminal 3 — Reports (Angular remote)
cd reports-mf && npm install && npm run dev        # :4205

# Terminal 4 — Shell (start LAST — it loads the other three)
cd shell && npm install && npm start               # :4200
```

Open **http://localhost:4200**, log in with any email + password (mock auth
for now), and you'll land on the Dashboard. The Orders and Reports nav items
are live too — Inventory / Billing / Kitchen / Customers are still shown
greyed-out in the sidebar, ready to be built the same way.

## How a page actually loads, step by step

1. You click "Orders" in the sidebar → Angular Router navigates to `/orders`.
2. The Shell renders `OrdersPage`, which renders `<app-remote-mount remoteKey="orders">`.
3. `RemoteMount` looks up `'orders'` in `remote.config.ts` → gets a URL
   (`http://localhost:5174/src/mount.ts` in dev).
4. It runs `await import(thatUrl)` — the browser fetches and executes Vue's
   `mount.ts` directly from the Orders dev server, cross-origin (that's why
   `cors: true` is set in each remote's Vite/Angular dev server config).
5. It calls `module.mount(containerDiv, { token, user })` — Vue takes it from there.
6. When you navigate away, Angular destroys `RemoteMount`, which calls
   `module.unmount(containerDiv)` — Vue tears itself down cleanly.

Same flow for React (Dashboard) and Angular (Reports) — only what happens
*inside* `mount()` differs per framework.

## Adding a new module (e.g. Inventory)

1. Build a new standalone app in whatever framework you like.
2. Give it a `mount(container, props)` / `unmount(container)` export (copy
   the pattern from `dashboard-mf/src/mount.tsx`, `orders-mf/src/mount.ts`,
   or `reports-mf/src/mount.ts` depending on framework).
3. Add an entry to `shell/src/app/core/remote.config.ts`.
4. Add a route + page wrapper in `shell/src/app/pages/` (copy `orders-page`).
5. Enable its sidebar item in `shell/src/app/layout/layout.ts` (remove `disabled: true`).

No changes needed in any *other* remote.

## Production deployment (later)

Each app builds to one self-contained JS file:

| App | Build command | Output |
|---|---|---|
| dashboard-mf | `npm run build` | `dist/dashboard-mf.js` |
| orders-mf | `npm run build` | `dist/orders-mf.js` |
| reports-mf | `npm run build:remote` | `dist/remote/browser/main.js` |
| shell | `npm run build` | `dist/shell/` (the deployable Angular app) |

Upload each remote's single JS file to a CDN or static host, then update
`shell/src/app/core/remote.config.ts` to point `entryUrl` at the production
URLs instead of `localhost`. Rebuild and redeploy the Shell.

## What's mock vs real right now

Everything is running on hard-coded mock data (menu items, orders, revenue
numbers) so the UI is fully clickable without a backend. Every mock data
spot has a comment showing exactly which REST endpoint replaces it once the
backend (see the separate `restro-pos-build-guide.md` for the Spring Boot
microservices) is running behind the API gateway.

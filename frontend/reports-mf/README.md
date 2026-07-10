# Reports MF — Angular

Microfrontend remote for **Reports** (revenue trend + top-selling items).
Built with Angular 20. Exposes `mount(container, props)` / `unmount(container)`
using Angular's `createApplication()` + `createComponent()` APIs, so it can
attach into any DOM element the Shell hands it — not just a fixed `<app-root>`.

## Run standalone (normal Angular dev flow)

```bash
npm install
npm start
```

Open http://localhost:4200 to see the Reports page on its own (uses the
regular `main.ts` → `App` → `<app-reports>` bootstrap).

## Run as part of the full app (as a remote)

This is a different build target — it compiles `src/mount.ts` (not `main.ts`)
into one dependency-free `main.js` and serves it on port 4205 for the Shell
to dynamically import:

```bash
npm run dev
```

This runs two things together (`concurrently`):
1. `watch:remote` — rebuilds `dist/remote/browser/main.js` on every change
2. `serve:remote` — serves that folder as static files on :4205 with CORS on

The Shell's `remote.config.ts` points at `http://localhost:4205/main.js`.

## Production build

```bash
npm run build:remote
```

Produces `dist/remote/browser/main.js` — deploy this file to a CDN/static
host and update the Shell's `remote.config.ts`.

## What's here

- `src/app/reports/reports.ts` / `.html` / `.scss` — the actual Reports UI
  (bar chart + top items table, mock data for now)
- `src/mount.ts` — the remote's public contract — the ONLY file the Shell touches
- `src/app/app.ts` + `src/main.ts` — standalone dev harness only

## Next steps

Replace the mock `monthlyRevenue` / `topItems` arrays in `reports.ts` with
real calls to `report-service` via the gateway, using the `token` @Input
passed in from the Shell.

# Dashboard MF — React

Microfrontend remote for the Restro POS **Dashboard** page. Built with React 19 + Vite.
Exposes a tiny `mount(container, props)` / `unmount(container)` contract — the Shell
(Angular) dynamically imports this at runtime and mounts it into a `<div>`.

## Run standalone (for local development)

```bash
npm install
npm run dev
```

Open http://localhost:5173 to see the Dashboard on its own, outside the Shell.

## Run as part of the full app

Just `npm run dev` and leave it running on port 5173 — the Shell's
`remote.config.ts` points at `http://localhost:5173/src/mount.tsx` in dev mode.

## Production build

```bash
npm run build
```

Produces `dist/dashboard-mf.js` — a single self-contained ES module (React is
bundled in, so it doesn't depend on the Shell's React version). Deploy this
file to a CDN/static host and update the Shell's `remote.config.ts`
`entryUrl` to point at it.

## What's here

- `src/dashboard/Dashboard.tsx` — the actual dashboard UI (stat cards, sales
  bar chart, revenue donut, recent orders, notifications — all mock data for now)
- `src/mount.tsx` — the remote's public contract (`mount` / `unmount`) — this
  is the ONLY file the Shell ever touches
- `src/App.tsx` + `src/main.tsx` — standalone dev harness only, not used by the Shell

## Next steps

Replace the mock arrays in `Dashboard.tsx` with real calls to
`order-service` / `report-service` via the API gateway, using the `token`
prop passed in from the Shell.

# Orders MF — Vue 3

Microfrontend remote for **Orders** (New Order + Running Orders). Built with
Vue 3 + Vite. Exposes `mount(container, props)` / `unmount(container)` — the
Shell dynamically imports this at runtime.

## Run standalone

```bash
npm install
npm run dev
```

Open http://localhost:5174 to see it on its own, outside the Shell.

## Run as part of the full app

Just `npm run dev` and leave it on port 5174 — the Shell's `remote.config.ts`
points at `http://localhost:5174/src/mount.ts` in dev mode.

## Production build

```bash
npm run build
```

Produces a single self-contained `dist/orders-mf.js` (Vue + CSS bundled in).
Deploy it to a CDN/static host and update the Shell's `remote.config.ts`.

## What's here

- `src/orders/OrdersApp.vue` — tab container (New Order / Running Orders)
- `src/orders/NewOrder.vue` — menu picker + cart + submit (mock menu data)
- `src/orders/RunningOrders.vue` — active orders table (mock data)
- `src/mount.ts` — the remote's public contract — the ONLY file the Shell touches
- `src/App.vue` + `src/main.ts` — standalone dev harness only

## Next steps

Replace the mock `menu` array and `submitOrder()` in `NewOrder.vue`, and the
mock `orders` array in `RunningOrders.vue`, with real calls to `order-service`
via the gateway, using the `token` prop passed in from the Shell.

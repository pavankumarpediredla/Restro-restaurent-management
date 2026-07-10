# Shell — Angular

The host application. This is the ONLY app the user's browser talks to directly.

## Owns

- Login page (mock auth for now — see `src/app/core/auth.service.ts`)
- Sidebar, header, footer (`src/app/layout`)
- Global routing (`src/app/app.routes.ts`)
- The `RemoteMount` component that loads every other module at runtime
  (`src/app/remote-mount/remote-mount.ts`)
- `src/app/core/remote.config.ts` — the registry of where each remote lives

## Does NOT own

Dashboard, Orders, Reports pages — those are separate apps, loaded at runtime.
The Shell only knows one thing about them: a URL to an ES module that exports
`mount(container, props)` / `unmount(container)`.

## Run it

```bash
npm install
npm start
```

Open http://localhost:4200 — but you'll only see Dashboard/Orders/Reports
render if their dev servers are also running (see the root `frontend/README.md`).

Log in with any email + password (mock auth).

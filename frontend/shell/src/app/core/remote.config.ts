/**
 * Registry of remote microfrontends the Shell can load at runtime.
 *
 * Each remote is built independently (its own repo/CI pipeline, its own
 * framework) and exposes ONE thing at a predictable URL: a JS module with
 * a `mount(container, props)` and `unmount(container)` export. The Shell
 * doesn't know or care what's inside — React, Vue, Angular, anything that
 * respects this tiny contract can be plugged in here.
 *
 * In production, replace these dev URLs with your CDN / static hosting
 * URLs for each remote's built bundle (see each remote's README).
 */
export interface RemoteConfig {
  key: string;
  name: string;
  /** URL of the remote's built ES module entry file */
  entryUrl: string;
  /**
   * Optional dev-mode-only setup some frameworks' dev servers need before
   * their module can be `import()`-ed directly, bypassing their normal
   * HTML entry point. Not needed in production (built bundles don't need this).
   */
  devPreamble?: () => Promise<void>;
}

/**
 * Vite + React's dev server normally injects a small "preamble" script into
 * index.html that sets up React Fast Refresh (window.$RefreshReg$ etc.)
 * before any component code runs. Since we import mount.tsx directly and
 * skip that HTML entry point, we have to run the same preamble manually —
 * otherwise React throws "@vitejs/plugin-react can't detect preamble."
 * This exact snippet is what Vite itself injects; see the Vite/React plugin
 * docs. Vue and Angular remotes don't need an equivalent step.
 */
async function reactViteDevPreamble(origin: string): Promise<void> {
  const w = window as any;
  if (w.__vite_plugin_react_preamble_installed__) return; // only once

  const refreshModule = await import(/* @vite-ignore */ `${origin}/@react-refresh`);
  refreshModule.injectIntoGlobalHook(window);
  w.$RefreshReg$ = () => {};
  w.$RefreshSig$ = () => (type: unknown) => type;
  w.__vite_plugin_react_preamble_installed__ = true;
}

export const REMOTES: Record<string, RemoteConfig> = {
  dashboard: {
    key: 'dashboard',
    name: 'Dashboard (React)',
    entryUrl: 'https://restro-dashboard.netlify.app/dashboard-mf.js',
  },
  orders: {
    key: 'orders',
    name: 'Orders (Vue)',
    entryUrl: 'https://restro-orders.netlify.app/orders-mf.js',
  },
  reports: {
    key: 'reports',
    name: 'Reports (Angular)',
    entryUrl: 'https://restro-reports.netlify.app/main.js',
  },
};

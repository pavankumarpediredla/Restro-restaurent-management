import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { resolve } from 'path';
import cssInjectedByJsPlugin from 'vite-plugin-css-injected-by-js';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), cssInjectedByJsPlugin()],

  // dev server: the Shell dynamically imports http://localhost:5173/src/mount.tsx
  // while this dev server is running — Vite transforms TSX on the fly.
  server: {
    port: 5173,
    cors: true, // required: the Shell (localhost:4200) fetches this cross-origin
  },

  // production build: bundles mount.tsx into a single importable ES module
  // at dist/dashboard-mf.js — point the Shell's remote.config.ts at this file
  // once you deploy it (e.g. https://cdn.yoursite.com/dashboard-mf/dashboard-mf.js)
  build: {
    lib: {
      entry: resolve(__dirname, 'src/mount.tsx'),
      name: 'dashboardMf',
      fileName: () => 'dashboard-mf.js',
      formats: ['es'],
    },
    outDir: 'dist',
  },
});

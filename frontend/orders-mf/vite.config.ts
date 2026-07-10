import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import cssInjectedByJsPlugin from 'vite-plugin-css-injected-by-js';

export default defineConfig({
  plugins: [vue(), cssInjectedByJsPlugin()],

  // dev server: the Shell dynamically imports http://localhost:5174/src/mount.ts
  // while this dev server is running — Vite transforms .vue/.ts on the fly.
  server: {
    port: 5174,
    cors: true, // required: the Shell (localhost:4200) fetches this cross-origin
  },

  // production build: bundles mount.ts into a single importable ES module
  // at dist/orders-mf.js — point the Shell's remote.config.ts at this file
  // once you deploy it (e.g. https://cdn.yoursite.com/orders-mf/orders-mf.js)
  build: {
    lib: {
      entry: resolve(__dirname, 'src/mount.ts'),
      name: 'ordersMf',
      fileName: () => 'orders-mf.js',
      formats: ['es'],
    },
    outDir: 'dist',
  },
});

import { createApplication } from '@angular/platform-browser';
import { createComponent, type ApplicationRef, type ComponentRef } from '@angular/core';
import { appConfig } from './app/app.config';
import { Reports } from './app/reports/reports';

/**
 * Remote contract: mount(container, props) / unmount(container).
 * Unlike React/Vue, Angular apps normally "own" the whole page — here we
 * use createApplication() + createComponent() to manually attach the
 * Reports component into whatever DOM element the Shell hands us, instead
 * of bootstrapping against a fixed <app-root> selector in index.html.
 *
 * IMPORTANT: this file deliberately does NOT `import 'zone.js'`. When this
 * remote is mounted inside the Shell (also Angular), the Shell has already
 * loaded zone.js on the page — Zone.js patches global browser APIs once,
 * and loading a second copy causes NG0909 / "assertNotInAngularZone"
 * errors from the two instances fighting over the same patched APIs.
 * `createApplication()` picks up the Shell's already-loaded Zone automatically.
 *
 * This means this specific build (`npm run build:remote` / `npm run dev`)
 * only works when loaded inside a page that already has zone.js — i.e.
 * inside the Shell. The separate standalone dev harness (`ng serve`,
 * main.ts → App → <app-reports>) still works on its own because that
 * build target includes zone.js via the normal "polyfills" option.
 */
let appRef: ApplicationRef | null = null;
let componentRef: ComponentRef<Reports> | null = null;

export async function mount(
  container: HTMLElement,
  props: Record<string, unknown> = {}
): Promise<void> {
  appRef = await createApplication(appConfig);

  componentRef = createComponent(Reports, {
    environmentInjector: appRef.injector,
    hostElement: container,
  });

  if ('token' in props) componentRef.setInput('token', props['token']);
  if ('user' in props) componentRef.setInput('user', props['user']);

  appRef.attachView(componentRef.hostView);
  componentRef.changeDetectorRef.detectChanges();
}

export function unmount(_container: HTMLElement): void {
  appRef?.destroy();
  appRef = null;
  componentRef = null;
}

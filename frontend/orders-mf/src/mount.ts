import { createApp, type App as VueApp } from 'vue';
import OrdersApp from './orders/OrdersApp.vue';
import './style.css';

const apps = new WeakMap<HTMLElement, VueApp>();

export function mount(container: HTMLElement, props: Record<string, unknown> = {}): void {
  const app = createApp(OrdersApp, props);
  apps.set(container, app);
  app.mount(container);
}

export function unmount(container: HTMLElement): void {
  apps.get(container)?.unmount();
  apps.delete(container);
}

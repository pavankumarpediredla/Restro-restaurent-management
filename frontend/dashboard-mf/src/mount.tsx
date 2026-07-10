import { createRoot, type Root } from 'react-dom/client';
import { Dashboard } from './dashboard/Dashboard';
import './index.css';

/**
 * This is the ONE thing the Shell (or any host) needs to know about.
 * It has nothing to do with React internals from the outside — just
 * mount(container, props) / unmount(container).
 */
const roots = new WeakMap<HTMLElement, Root>();

export function mount(container: HTMLElement, props: Record<string, unknown> = {}): void {
  const root = createRoot(container);
  roots.set(container, root);
  root.render(<Dashboard {...props} />);
}

export function unmount(container: HTMLElement): void {
  roots.get(container)?.unmount();
  roots.delete(container);
}

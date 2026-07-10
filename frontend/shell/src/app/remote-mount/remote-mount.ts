import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { REMOTES } from '../core/remote.config';
import { AuthService } from '../core/auth.service';

/** Contract every remote microfrontend bundle must implement. */
interface RemoteModule {
  mount: (container: HTMLElement, props: Record<string, unknown>) => void;
  unmount: (container: HTMLElement) => void;
}

@Component({
  selector: 'app-remote-mount',
  standalone: true,
  template: `
    <div class="remote-mount-wrap">
      @if (loading) {
        <div class="remote-loading">Loading {{ label }}…</div>
      }
      @if (error) {
        <div class="remote-error">
          Couldn't load <strong>{{ label }}</strong>. Is its dev server running?
          <div class="remote-error-detail">{{ error }}</div>
        </div>
      }
      <div class="remote-container" #container></div>
    </div>
  `,
  styles: [`
    .remote-mount-wrap { min-height: 200px; }
    .remote-loading, .remote-error {
      font-family: var(--font-body);
      font-size: 13px;
      color: var(--ink-soft);
      padding: 16px 0;
    }
    .remote-error { color: var(--rust); }
    .remote-error-detail {
      font-family: var(--font-mono);
      font-size: 11px;
      margin-top: 6px;
      opacity: 0.8;
    }
  `],
})
export class RemoteMount implements OnInit, AfterViewInit, OnDestroy {
  /** Key from REMOTES, e.g. 'dashboard' | 'orders' | 'reports' */
  @Input({ required: true }) remoteKey!: string;

  @ViewChild('container', { static: true }) containerRef!: ElementRef<HTMLElement>;

  loading = true;
  error = '';
  label = '';

  private module: RemoteModule | null = null;

  constructor(private auth: AuthService) {}

  ngOnInit(): void {
    // Resolved here (before the view is first checked), NOT in
    // ngAfterViewInit — setting it there triggers Angular's
    // ExpressionChangedAfterItHasBeenCheckedError in dev mode, because
    // ngAfterViewInit runs after the view has already been checked once.
    const remote = REMOTES[this.remoteKey];
    this.label = remote?.name ?? this.remoteKey;
  }

  async ngAfterViewInit(): Promise<void> {
    const remote = REMOTES[this.remoteKey];

    if (!remote) {
      this.error = `Unknown remote "${this.remoteKey}"`;
      this.loading = false;
      return;
    }

    try {
      // Dev-mode-only setup some frameworks' dev servers need before a
      // module can be imported directly (bypassing their normal HTML
      // entry point). No-op for remotes that don't need it.
      await remote.devPreamble?.();

      // Dynamic import — this is the whole trick. The Shell doesn't bundle
      // any React/Vue/Angular remote code; it fetches it from the remote's
      // own dev server / static host at runtime.
      this.module = (await import(/* @vite-ignore */ remote.entryUrl)) as RemoteModule;

      this.module.mount(this.containerRef.nativeElement, {
        // shared context every remote gets, e.g. the JWT for its own API calls
        token: localStorage.getItem('restro_token'),
        user: this.auth.currentUser(),
      });
      this.loading = false;
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
      this.loading = false;
    }
  }

  ngOnDestroy(): void {
    this.module?.unmount(this.containerRef.nativeElement);
  }
}

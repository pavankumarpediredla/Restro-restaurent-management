import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';

const TOKEN_KEY = 'restro_token';
const USER_KEY = 'restro_user';

export interface AuthUser {
  name: string;
  role: 'ADMIN' | 'MANAGER' | 'STAFF';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  // signal so the header/sidebar can react to login state instantly
  readonly currentUser = signal<AuthUser | null>(this.readUser());

  constructor(private router: Router) {}

  get isLoggedIn(): boolean {
    return !!localStorage.getItem(TOKEN_KEY);
  }

  /**
   * TEMP: mock login so the UI can be built end-to-end without the backend running yet.
   * Replace the body of this method with a real HTTP call once auth-service /
   * the gateway is up:
   *
   *   return this.http.post<{ token: string; user: AuthUser }>(
   *     '/api/auth/login', { email, password }
   *   );
   */
  login(email: string, password: string): Promise<boolean> {
    return new Promise((resolve) => {
      setTimeout(() => {
        if (!email || !password) {
          resolve(false);
          return;
        }
        const fakeToken = 'mock-jwt-token';
        const user: AuthUser = { name: email.split('@')[0], role: 'ADMIN' };
        localStorage.setItem(TOKEN_KEY, fakeToken);
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        this.currentUser.set(user);
        resolve(true);
      }, 400); // simulate network latency
    });
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private readUser(): AuthUser | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}

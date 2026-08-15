import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';

const TOKEN_KEY = 'restro_token';
const USER_KEY = 'restro_user';
const API_BASE = window.location.hostname === 'localhost'
  ? 'http://localhost:8081'
  : 'https://restro-restaurent-management.onrender.com';

export interface AuthUser {
  id: number;
  username: string;
  displayName: string;
  role: 'OWNER' | 'ADMIN' | 'MANAGER' | 'CHEF' | 'WAITER' | 'CASHIER' | 'CLEANER' | 'SECURITY' | 'KITCHEN';
  enabled: boolean;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly currentUser = signal<AuthUser | null>(this.readUser());

  constructor(private router: Router) {}

  get isLoggedIn(): boolean {
    return !!localStorage.getItem(TOKEN_KEY);
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  login(username: string, password: string): Promise<boolean> {
    return fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    })
      .then(async (response) => {
        if (!response.ok) {
          return false;
        }
        const payload = await response.json();
        localStorage.setItem(TOKEN_KEY, payload.token);
        localStorage.setItem(USER_KEY, JSON.stringify(payload.user));
        this.currentUser.set(payload.user);
        return true;
      })
      .catch(() => false);
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

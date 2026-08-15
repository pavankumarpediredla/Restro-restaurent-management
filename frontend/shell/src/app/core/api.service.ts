import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';

const API_BASE = 'http://localhost:8081';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private auth: AuthService) {}

  async get<T>(path: string): Promise<T> {
    const response = await fetch(`${API_BASE}${path}`, {
      headers: this.headers(),
    });
    return this.handleResponse<T>(response);
  }

  async post<T>(path: string, body: unknown): Promise<T> {
    const response = await fetch(`${API_BASE}${path}`, {
      method: 'POST',
      headers: this.headers(),
      body: JSON.stringify(body),
    });
    return this.handleResponse<T>(response);
  }

  async put<T>(path: string, body: unknown): Promise<T> {
    const response = await fetch(`${API_BASE}${path}`, {
      method: 'PUT',
      headers: this.headers(),
      body: JSON.stringify(body),
    });
    return this.handleResponse<T>(response);
  }

  async patch<T>(path: string, body?: unknown): Promise<T> {
    const response = await fetch(`${API_BASE}${path}`, {
      method: 'PATCH',
      headers: this.headers(),
      body: body === undefined ? undefined : JSON.stringify(body),
    });
    return this.handleResponse<T>(response);
  }

  private headers(): HeadersInit {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    const token = this.auth.token;
    if (token) {
      headers['Authorization'] = token;
    }
    return headers;
  }

  private async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      const message = await response.text();
      throw new Error(message || response.statusText);
    }
    if (response.status === 204) {
      return undefined as T;
    }
    return (await response.json()) as T;
  }
}

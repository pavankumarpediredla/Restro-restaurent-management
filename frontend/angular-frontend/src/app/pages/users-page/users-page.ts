import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { AppUser, Role } from '../../core/models';

interface UserForm {
  username: string;
  password: string;
  displayName: string;
  role: Role;
  enabled: boolean;
}

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users-page.html',
  styleUrl: './users-page.scss',
})
export class UsersPage implements OnInit {
  users: AppUser[] = [];
  loading = false;
  saving = false;
  error = '';
  form: UserForm = this.emptyForm();

  constructor(private api: ApiService, private auth: AuthService) {}

  get canCreateStaff(): boolean {
    const role = this.auth.currentUser()?.role;
    return role === 'OWNER' || role === 'ADMIN';
  }

  ngOnInit(): void {
    void this.load();
  }

  async load(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.users = await this.api.get<AppUser[]>('/api/users');
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.loading = false;
    }
  }

  async save(): Promise<void> {
    if (!this.form.username.trim() || !this.form.password.trim()) {
      return;
    }
    this.saving = true;
    this.error = '';
    try {
      await this.api.post<AppUser>('/api/users', {
        username: this.form.username.trim(),
        password: this.form.password,
        displayName: this.form.displayName.trim() || null,
        role: this.form.role,
        enabled: this.form.enabled,
      });
      this.form = this.emptyForm();
      await this.load();
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.saving = false;
    }
  }

  async setEnabled(user: AppUser, enabled: boolean): Promise<void> {
    await this.api.patch<AppUser>(`/api/users/${user.id}/enabled`, enabled);
    await this.load();
  }

  private emptyForm(): UserForm {
    return {
      username: '',
      password: '',
      displayName: '',
      role: 'MANAGER',
      enabled: true,
    };
  }
}

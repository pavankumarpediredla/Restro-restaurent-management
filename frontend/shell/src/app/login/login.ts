import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  username = '';
  password = '';
  loading = signal(false);
  error = signal('');

  constructor(private auth: AuthService, private router: Router) {}

  async onSubmit(): Promise<void> {
    this.error.set('');
    this.loading.set(true);
    const ok = await this.auth.login(this.username, this.password);
    this.loading.set(false);

    if (ok) {
      const role = this.auth.currentUser()?.role;
      this.router.navigate([role === 'CHEF' || role === 'KITCHEN' ? '/kitchen' : '/dashboard']);
    } else {
      this.error.set('Enter a valid username and password.');
    }
  }
}

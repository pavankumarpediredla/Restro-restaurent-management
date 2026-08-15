import { Component, OnInit, signal } from '@angular/core';
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
export class Login implements OnInit {
  username = '';
  password = '';
  loading = signal(false);
  error = signal('');

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (this.auth.isLoggedIn) this.router.navigate([this.defaultRoute()]);
  }

  async onSubmit(): Promise<void> {
    this.error.set('');
    this.loading.set(true);
    const ok = await this.auth.login(this.username, this.password);
    this.loading.set(false);

    if (ok) {
      this.router.navigate([this.defaultRoute()]);
    } else {
      this.error.set('Enter a valid username and password.');
    }
  }

  private defaultRoute(): string {
    const role = this.auth.currentUser()?.role;
    if (role === 'CHEF' || role === 'KITCHEN') return '/kitchen';
    if (role === 'WAITER') return '/orders';
    if (role === 'CASHIER') return '/billing';
    return '/dashboard';
  }
}

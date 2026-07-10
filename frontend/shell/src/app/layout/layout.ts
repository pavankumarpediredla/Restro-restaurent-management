import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';

interface NavItem {
  label: string;
  path: string;
  icon: string;
  disabled?: boolean; // sections not built yet — greyed out, ready to wire up later
}

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './layout.html',
  styleUrl: './layout.scss',
})
export class Layout {
  year = new Date().getFullYear();

  nav: NavItem[] = [
    { label: 'Dashboard', path: '/dashboard', icon: '◆' },
    { label: 'Orders', path: '/orders', icon: '🧾' },
    { label: 'Inventory', path: '/inventory', icon: '📦', disabled: true },
    { label: 'Billing', path: '/billing', icon: '💰', disabled: true },
    { label: 'Kitchen', path: '/kitchen', icon: '🍳', disabled: true },
    { label: 'Customers', path: '/customers', icon: '👥', disabled: true },
    { label: 'Reports', path: '/reports', icon: '📊' },
  ];

  constructor(public auth: AuthService) {}

  logout(): void {
    this.auth.logout();
  }
}

import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { Role } from '../core/models';

interface NavItem {
  label: string;
  path: string;
  icon: string;
  roles: Role[];
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
    { label: 'Dashboard', path: '/dashboard', icon: 'DB', roles: ['ADMIN', 'MANAGER'] },
    { label: 'Orders', path: '/orders', icon: 'OR', roles: ['ADMIN', 'MANAGER'] },
    { label: 'Inventory', path: '/inventory', icon: 'IN', roles: ['ADMIN', 'MANAGER'] },
    { label: 'Billing', path: '/billing', icon: 'BL', roles: ['ADMIN', 'MANAGER'] },
    { label: 'Customers', path: '/customers', icon: 'CU', roles: ['ADMIN', 'MANAGER'] },
    { label: 'Users', path: '/users', icon: 'US', roles: ['ADMIN'] },
    { label: 'Kitchen', path: '/kitchen', icon: 'KH', roles: ['KITCHEN'] },
    { label: 'Reports', path: '/reports', icon: 'RP', roles: ['ADMIN', 'MANAGER'] },
  ];

  constructor(public auth: AuthService) {}

  get visibleNav(): NavItem[] {
    const role = this.auth.currentUser()?.role;
    if (!role) {
      return [];
    }
    return this.nav.filter((item) => item.roles.includes(role));
  }

  get displayName(): string {
    const user = this.auth.currentUser();
    return user?.displayName || user?.username || 'Guest';
  }

  get profileRole(): string {
    return this.auth.currentUser()?.role || '';
  }

  get avatar(): string {
    return this.displayName.trim().charAt(0).toUpperCase() || '?';
  }

  logout(): void {
    this.auth.logout();
  }
}

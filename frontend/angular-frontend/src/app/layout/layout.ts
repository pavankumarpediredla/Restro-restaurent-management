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
    { label: 'Dashboard', path: '/dashboard', icon: 'DB', roles: ['OWNER', 'ADMIN', 'MANAGER'] },
    { label: 'Orders', path: '/orders', icon: 'OR', roles: ['OWNER', 'ADMIN', 'MANAGER', 'WAITER'] },
    { label: 'Tables', path: '/tables', icon: 'TB', roles: ['OWNER', 'ADMIN', 'MANAGER', 'WAITER', 'CASHIER'] },
    { label: 'Inventory', path: '/inventory', icon: 'IN', roles: ['OWNER', 'ADMIN', 'MANAGER'] },
    { label: 'Billing', path: '/billing', icon: 'BL', roles: ['OWNER', 'ADMIN', 'MANAGER', 'CASHIER'] },
    { label: 'Customers', path: '/customers', icon: 'CU', roles: ['OWNER', 'ADMIN', 'MANAGER', 'WAITER'] },
    { label: 'Staff', path: '/users', icon: 'ST', roles: ['OWNER', 'ADMIN', 'MANAGER'] },
    { label: 'Attendance', path: '/attendance', icon: 'AT', roles: ['OWNER', 'MANAGER'] },
    { label: 'Kitchen', path: '/kitchen', icon: 'KH', roles: ['OWNER', 'CHEF', 'KITCHEN'] },
    { label: 'Reports', path: '/reports', icon: 'RP', roles: ['OWNER', 'ADMIN', 'MANAGER'] },
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

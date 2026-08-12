import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Layout } from './layout/layout';
import { roleGuard } from './core/role.guard';
import { DashboardPage } from './pages/dashboard-page/dashboard-page';
import { OrdersPage } from './pages/orders-page/orders-page';
import { InventoryPage } from './pages/inventory-page/inventory-page';
import { BillingPage } from './pages/billing-page/billing-page';
import { KitchenPage } from './pages/kitchen-page/kitchen-page';
import { CustomersPage } from './pages/customers-page/customers-page';
import { UsersPage } from './pages/users-page/users-page';
import { ReportsPage } from './pages/reports-page/reports-page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'login', component: Login },
  {
    path: '',
    component: Layout,
    children: [
      {
        path: 'dashboard',
        component: DashboardPage,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
      {
        path: 'orders',
        component: OrdersPage,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
      {
        path: 'inventory',
        component: InventoryPage,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
      {
        path: 'billing',
        component: BillingPage,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
      {
        path: 'customers',
        component: CustomersPage,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
      {
        path: 'users',
        component: UsersPage,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'kitchen',
        component: KitchenPage,
        canActivate: [roleGuard],
        data: { roles: ['KITCHEN'] },
      },
      {
        path: 'reports',
        component: ReportsPage,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];

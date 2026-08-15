import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Layout } from './layout/layout';
import { roleGuard } from './core/role.guard';
import { authGuard } from './core/auth.guard';
import { DashboardPage } from './pages/dashboard-page/dashboard-page';
import { OrdersPage } from './pages/orders-page/orders-page';
import { InventoryPage } from './pages/inventory-page/inventory-page';
import { BillingPage } from './pages/billing-page/billing-page';
import { KitchenPage } from './pages/kitchen-page/kitchen-page';
import { CustomersPage } from './pages/customers-page/customers-page';
import { UsersPage } from './pages/users-page/users-page';
import { ReportsPage } from './pages/reports-page/reports-page';
import { AttendancePage } from './pages/attendance-page/attendance-page';
import { AttendanceReportPage } from './pages/attendance-report/attendance-report-page';
import { AttendanceSettingsPage } from './pages/attendance-settings/attendance-settings-page';
import { TablesPage } from './pages/tables-page/tables-page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'login', component: Login },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'ADMIN', 'MANAGER'] },
      },
      {
        path: 'orders',
        component: OrdersPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'ADMIN', 'MANAGER', 'WAITER'] },
      },
      {
        path: 'inventory',
        component: InventoryPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'ADMIN', 'MANAGER'] },
      },
      {
        path: 'billing',
        component: BillingPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'ADMIN', 'MANAGER', 'CASHIER'] },
      },
      {
        path: 'customers',
        component: CustomersPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'ADMIN', 'MANAGER', 'WAITER'] },
      },
      {
        path: 'users',
        component: UsersPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'ADMIN', 'MANAGER'] },
      },
      { path: 'tables', component: TablesPage, canActivate: [roleGuard], data: { roles: ['OWNER', 'ADMIN', 'MANAGER', 'WAITER', 'CASHIER'] } },
      {
        path: 'attendance',
        component: AttendancePage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'MANAGER'] },
      },
      {
        path: 'attendance/report',
        component: AttendanceReportPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'MANAGER'] },
      },
      { path: 'attendance/settings', component: AttendanceSettingsPage, canActivate: [roleGuard], data: { roles: ['OWNER', 'MANAGER'] } },
      {
        path: 'kitchen',
        component: KitchenPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'CHEF', 'KITCHEN'] },
      },
      {
        path: 'reports',
        component: ReportsPage,
        canActivate: [roleGuard],
        data: { roles: ['OWNER', 'ADMIN', 'MANAGER'] },
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];

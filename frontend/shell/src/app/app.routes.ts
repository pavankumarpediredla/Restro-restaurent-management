import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Layout } from './layout/layout';
import { authGuard } from './core/auth.guard';
import { DashboardPage } from './pages/dashboard-page/dashboard-page';
import { OrdersPage } from './pages/orders-page/orders-page';
import { ReportsPage } from './pages/reports-page/reports-page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'login', component: Login },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardPage },   // React remote
      { path: 'orders', component: OrdersPage },          // Vue remote
      { path: 'reports', component: ReportsPage },        // Angular remote
      // inventory / billing / kitchen / customers will be added the same way
    ],
  },
  { path: '**', redirectTo: 'login' },
];

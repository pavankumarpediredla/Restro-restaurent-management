import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { DashboardSummary, MonthlyRevenuePoint, RecentOrder, TopItem } from '../../core/models';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss',
})
export class DashboardPage implements OnInit {
  loading = true;
  error = '';
  summary: DashboardSummary | null = null;
  monthlyRevenue: MonthlyRevenuePoint[] = [];
  topItems: TopItem[] = [];
  recentOrders: RecentOrder[] = [];

  constructor(private api: ApiService) {}

  async ngOnInit(): Promise<void> {
    try {
      [this.summary, this.monthlyRevenue, this.topItems, this.recentOrders] = await Promise.all([
        this.api.get<DashboardSummary>('/api/reports/summary'),
        this.api.get<MonthlyRevenuePoint[]>('/api/reports/monthly-revenue'),
        this.api.get<TopItem[]>('/api/reports/top-items'),
        this.api.get<RecentOrder[]>('/api/reports/recent-orders'),
      ]);
    } catch (error) {
      this.error = error instanceof Error ? error.message : String(error);
    } finally { this.loading = false; }
  }

  get maxRevenue(): number { return Math.max(1, ...this.monthlyRevenue.map((item) => item.revenue)); }
  money(value: number): string { return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(value); }
}

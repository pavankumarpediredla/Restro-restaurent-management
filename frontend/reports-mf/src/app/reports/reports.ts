import { Component, Input, OnInit } from '@angular/core';

interface Summary {
  totalRevenue: number;
  todayRevenue: number;
  totalOrders: number;
  todayOrders: number;
  activeItems: number;
  activeCustomers: number;
  pendingInvoices: number;
}

interface MonthlyRevenuePoint {
  month: string;
  revenue: number;
}

interface TopItemRow {
  menuItemId: number;
  item: string;
  category: string | null;
  unitsSold: number;
  revenue: number;
}

const API_BASE = 'http://localhost:8081';
const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 2,
});

async function fetchJson<T>(path: string, token?: string | null): Promise<T> {
  const headers: HeadersInit = {};
  if (token) {
    headers.Authorization = token;
  }

  const response = await fetch(`${API_BASE}${path}`, { headers });
  if (!response.ok) {
    throw new Error(await response.text() || response.statusText);
  }
  return (await response.json()) as T;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  templateUrl: './reports.html',
  styleUrl: './reports.scss',
})
export class Reports implements OnInit {
  @Input() token: string | null = null;
  @Input() user: { username?: string; displayName?: string; role?: string } | null = null;

  loading = true;
  error = '';
  summary: Summary | null = null;
  monthlyRevenue: MonthlyRevenuePoint[] = [];
  topItems: TopItemRow[] = [];

  async ngOnInit(): Promise<void> {
    this.loading = true;
    this.error = '';

    try {
      if (!this.token) {
        throw new Error('Login through the shell to load live report data.');
      }

      const [summary, monthlyRevenue, topItems] = await Promise.all([
        fetchJson<Summary>('/api/reports/summary', this.token),
        fetchJson<MonthlyRevenuePoint[]>('/api/reports/monthly-revenue', this.token),
        fetchJson<TopItemRow[]>('/api/reports/top-items', this.token),
      ]);

      this.summary = summary;
      this.monthlyRevenue = monthlyRevenue;
      this.topItems = topItems;
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.loading = false;
    }
  }

  get maxRevenue(): number {
    return Math.max(1, ...this.monthlyRevenue.map((month) => month.revenue));
  }

  formatMoney(value: number): string {
    return currency.format(value);
  }
}

import { Component, Input } from '@angular/core';

interface ReportRow {
  item: string;
  category: string;
  unitsSold: number;
  revenue: string;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  templateUrl: './reports.html',
  styleUrl: './reports.scss',
})
export class Reports {
  @Input() token: string | null = null;
  @Input() user: { name: string; role: string } | null = null;

  // TEMP mock data — swap for GET /api/reports/top-items,
  // GET /api/reports/monthly-revenue once report-service is live.
  monthlyRevenue = [
    { month: 'Feb', value: 62 },
    { month: 'Mar', value: 71 },
    { month: 'Apr', value: 68 },
    { month: 'May', value: 84 },
    { month: 'Jun', value: 96 },
    { month: 'Jul', value: 103 },
  ];

  topItems: ReportRow[] = [
    { item: 'Paneer Butter Masala', category: 'Main', unitsSold: 342, revenue: '₹75,240' },
    { item: 'Veg Biryani', category: 'Main', unitsSold: 298, revenue: '₹56,620' },
    { item: 'Butter Naan', category: 'Bread', unitsSold: 610, revenue: '₹24,400' },
    { item: 'Cold Coffee', category: 'Beverage', unitsSold: 275, revenue: '₹24,750' },
    { item: 'Masala Dosa', category: 'South Indian', unitsSold: 190, revenue: '₹20,900' },
  ];

  get maxRevenue(): number {
    return Math.max(...this.monthlyRevenue.map((m) => m.value));
  }
}

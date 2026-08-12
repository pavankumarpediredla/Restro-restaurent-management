import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { Order } from '../../core/models';

@Component({
  selector: 'app-kitchen-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './kitchen-page.html',
  styleUrl: './kitchen-page.scss',
})
export class KitchenPage implements OnInit {
  orders: Order[] = [];
  loading = false;
  savingId: number | null = null;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    void this.load();
  }

  get activeOrders(): Order[] {
    return this.orders.filter((order) => order.status === 'NEW' || order.status === 'ACCEPTED');
  }

  async load(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.orders = await this.api.get<Order[]>('/api/v1/orders');
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.loading = false;
    }
  }

  async accept(order: Order): Promise<void> {
    this.savingId = order.id;
    this.error = '';
    try {
      await this.api.post<Order>(`/api/v1/orders/${order.id}/accept`, {});
      await this.load();
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.savingId = null;
    }
  }

  itemSummary(order: Order): string {
    return order.items.map((line) => `${line.menuItemName} x${line.quantity}`).join(', ');
  }

  statusClass(status: Order['status']): string {
    return status.toLowerCase().replace('_', '-');
  }
}

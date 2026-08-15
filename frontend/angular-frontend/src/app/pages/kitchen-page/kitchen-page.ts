import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { Order } from '../../core/models';

@Component({
  selector: 'app-kitchen-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './kitchen-page.html',
  styleUrl: './kitchen-page.scss',
})
export class KitchenPage implements OnInit, OnDestroy {
  orders: Order[] = [];
  selectedOrder: Order | null = null;
  loading = false;
  savingId: number | null = null;
  error = '';
  private eventSource: EventSource | null = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    void this.load();
    this.connectLiveUpdates();
  }

  ngOnDestroy(): void {
    this.eventSource?.close();
  }

  get activeOrders(): Order[] {
    return this.orders.filter((order) => order.status === 'NEW' || order.status === 'ACCEPTED' || order.status === 'READY');
  }

  get newOrderCount(): number {
    return this.activeOrders.filter((order) => order.status === 'NEW').length;
  }

  async load(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.orders = await this.api.get<Order[]>('/api/v1/orders');
      if (this.selectedOrder) {
        this.selectedOrder = this.orders.find((order) => order.id === this.selectedOrder?.id) ?? null;
      }
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

  async markReady(order: Order): Promise<void> {
    this.savingId = order.id;
    try {
      await this.api.patch<Order>(`/api/v1/orders/${order.id}/ready`);
      await this.load();
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.savingId = null;
    }
  }

  select(order: Order): void {
    this.selectedOrder = order;
  }

  private connectLiveUpdates(): void {
    this.eventSource = new EventSource('http://localhost:8082/api/notifications/kitchen/stream');
    this.eventSource.addEventListener('order-created', () => void this.load());
  }

  itemSummary(order: Order): string {
    return order.items.map((line) => `${line.menuItemName} x${line.quantity}`).join(', ');
  }

  statusClass(status: Order['status']): string {
    return status.toLowerCase().replace('_', '-');
  }
}

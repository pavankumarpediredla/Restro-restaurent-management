import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { Invoice, Order } from '../../core/models';

interface BillingForm {
  orderId: number | null;
  taxAmount: number | null;
  discountAmount: number | null;
  notes: string;
}

@Component({
  selector: 'app-billing-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './billing-page.html',
  styleUrl: './billing-page.scss',
})
export class BillingPage implements OnInit {
  invoices: Invoice[] = [];
  orders: Order[] = [];
  availableOrders: Order[] = [];
  loading = false;
  saving = false;
  error = '';
  form: BillingForm = this.emptyForm();

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    void this.load();
  }

  async load(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      const [invoices, orders] = await Promise.all([
        this.api.get<Invoice[]>('/api/billing/invoices'),
        this.api.get<Order[]>('/api/v1/orders'),
      ]);
      this.invoices = invoices;
      this.orders = orders;
      const invoicedOrderIds = new Set(invoices.map((invoice) => invoice.orderId));
      this.availableOrders = orders.filter((order) => !invoicedOrderIds.has(order.id));
      if (this.form.orderId === null && this.availableOrders.length > 0) {
        this.form.orderId = this.availableOrders[0].id;
      }
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.loading = false;
    }
  }

  get selectedOrder(): Order | undefined {
    return this.orders.find((order) => order.id === this.form.orderId);
  }

  async save(): Promise<void> {
    if (!this.form.orderId) {
      this.error = 'Select an order before creating an invoice.';
      return;
    }
    this.saving = true;
    this.error = '';
    try {
      await this.api.post<Invoice>('/api/billing/invoices', {
        orderId: this.form.orderId,
        taxAmount: this.form.taxAmount ?? null,
        discountAmount: this.form.discountAmount ?? null,
        notes: this.form.notes.trim() || null,
      });
      this.form = this.emptyForm();
      await this.load();
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.saving = false;
    }
  }

  async markPaid(invoice: Invoice): Promise<void> {
    this.saving = true;
    this.error = '';
    try {
      await this.api.patch<Invoice>(`/api/billing/invoices/${invoice.id}/paid`);
      await this.load();
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.saving = false;
    }
  }

  private emptyForm(): BillingForm {
    return {
      orderId: null,
      taxAmount: 0,
      discountAmount: 0,
      notes: '',
    };
  }
}

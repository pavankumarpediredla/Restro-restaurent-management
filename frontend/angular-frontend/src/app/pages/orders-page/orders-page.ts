import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { Customer, MenuItem, Order } from '../../core/models';

interface CartLine { item: MenuItem; quantity: number; }
interface RestaurantTable { id: number; tableNumber: string; active: boolean; }

@Component({
  selector: 'app-orders-page',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './orders-page.html',
  styleUrl: './orders-page.scss',
})
export class OrdersPage implements OnInit {
  tab: 'new' | 'running' = 'new'; loading = true; saving = false; error = ''; success = '';
  menu: MenuItem[] = []; customers: Customer[] = []; tables: RestaurantTable[] = []; orders: Order[] = []; cart: CartLine[] = [];
  form = { customerId: '', tableId: '', customerName: '', customerPhone: '', customerEmail: '', customerAddress: '', notes: '' };
  constructor(private api: ApiService) {}
  async ngOnInit(): Promise<void> { await this.load(); }
  async load(): Promise<void> { this.loading = true; this.error = ''; try { const [menu, customers, tables, orders] = await Promise.all([this.api.get<MenuItem[]>('/api/v1/items'), this.api.get<Customer[]>('/api/customers'), this.api.get<RestaurantTable[]>('/api/v1/tables'), this.api.get<Order[]>('/api/v1/orders')]); this.menu = menu.filter((item) => item.active); this.customers = customers.filter((item) => item.active); this.tables = tables.filter((item) => item.active); this.orders = orders; } catch (error) { this.error = error instanceof Error ? error.message : String(error); } finally { this.loading = false; } }
  price(item: MenuItem): number { return item.prices[0]?.amount ?? 0; }
  money(value: number): string { return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(value); }
  get total(): number { return this.cart.reduce((sum, line) => sum + this.price(line.item) * line.quantity, 0); }
  add(item: MenuItem): void { const line = this.cart.find((value) => value.item.id === item.id); if (line) line.quantity++; else this.cart.push({ item, quantity: 1 }); }
  change(line: CartLine, amount: number): void { line.quantity += amount; if (line.quantity < 1) this.cart = this.cart.filter((value) => value !== line); }
  remove(line: CartLine): void { this.cart = this.cart.filter((value) => value !== line); }
  async submit(): Promise<void> { if (!this.cart.length) return; this.saving = true; this.error = ''; try { await this.api.post('/api/v1/orders', { customerId: this.form.customerId ? Number(this.form.customerId) : null, customerName: this.form.customerId ? null : this.blank(this.form.customerName), customerPhone: this.form.customerId ? null : this.blank(this.form.customerPhone), customerEmail: this.form.customerId ? null : this.blank(this.form.customerEmail), customerAddress: this.form.customerId ? null : this.blank(this.form.customerAddress), tableId: this.form.tableId ? Number(this.form.tableId) : null, notes: this.blank(this.form.notes), items: this.cart.map((line) => ({ menuItemId: line.item.id, quantity: line.quantity, priceCycle: 'DAILY' })) }); this.cart = []; this.success = 'Order saved successfully.'; await this.load(); } catch (error) { this.error = error instanceof Error ? error.message : String(error); } finally { this.saving = false; } }
  private blank(value: string): string | null { return value.trim() || null; }
}

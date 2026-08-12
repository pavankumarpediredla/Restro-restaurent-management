import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { MenuItem } from '../../core/models';

interface ItemForm {
  id: number | null;
  name: string;
  description: string;
  category: string;
  active: boolean;
  dailyPrice: number | null;
  weeklyPrice: number | null;
  monthlyPrice: number | null;
}

@Component({
  selector: 'app-inventory-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory-page.html',
  styleUrl: './inventory-page.scss',
})
export class InventoryPage implements OnInit {
  items: MenuItem[] = [];
  loading = false;
  saving = false;
  error = '';
  form: ItemForm = this.emptyForm();

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    void this.load();
  }

  async load(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.items = await this.api.get<MenuItem[]>('/api/v1/items');
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.loading = false;
    }
  }

  edit(item: MenuItem): void {
    const daily = item.prices.find((price) => price.cycle === 'DAILY')?.amount ?? null;
    const weekly = item.prices.find((price) => price.cycle === 'WEEKLY')?.amount ?? null;
    const monthly = item.prices.find((price) => price.cycle === 'MONTHLY')?.amount ?? null;
    this.form = {
      id: item.id,
      name: item.name,
      description: item.description ?? '',
      category: item.category ?? '',
      active: item.active,
      dailyPrice: daily,
      weeklyPrice: weekly,
      monthlyPrice: monthly,
    };
  }

  reset(): void {
    this.form = this.emptyForm();
  }

  async save(): Promise<void> {
    if (!this.form.name.trim()) {
      return;
    }
    const prices = [
      { cycle: 'DAILY', amount: this.form.dailyPrice },
      { cycle: 'WEEKLY', amount: this.form.weeklyPrice },
      { cycle: 'MONTHLY', amount: this.form.monthlyPrice },
    ]
      .filter((price) => price.amount !== null && price.amount !== undefined)
      .map((price) => ({
        cycle: price.cycle,
        amount: Number(price.amount),
      }))
      .filter((price) => !Number.isNaN(price.amount));

    if (prices.length === 0) {
      this.error = 'Add at least one price cycle before saving the item.';
      return;
    }

    this.saving = true;
    this.error = '';
    const payload = {
      name: this.form.name.trim(),
      description: this.form.description.trim() || null,
      category: this.form.category.trim() || null,
      active: this.form.active,
      prices,
    };

    try {
      if (this.form.id) {
        await this.api.put<MenuItem>(`/api/v1/items/${this.form.id}`, payload);
      } else {
        await this.api.post<MenuItem>('/api/v1/items', payload);
      }
      this.reset();
      await this.load();
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.saving = false;
    }
  }

  async setActive(item: MenuItem, active: boolean): Promise<void> {
    const payload = {
      name: item.name,
      description: item.description ?? '',
      category: item.category ?? '',
      active,
      prices: item.prices,
    };
    await this.api.put<MenuItem>(`/api/v1/items/${item.id}`, payload);
    await this.load();
  }

  formatPrice(item: MenuItem, cycle: string): string {
    return item.prices.find((price) => price.cycle === cycle)?.amount.toFixed(2) ?? '-';
  }

  private emptyForm(): ItemForm {
    return {
      id: null,
      name: '',
      description: '',
      category: '',
      active: true,
      dailyPrice: null,
      weeklyPrice: null,
      monthlyPrice: null,
    };
  }
}

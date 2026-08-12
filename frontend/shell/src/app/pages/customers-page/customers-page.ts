import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { Customer } from '../../core/models';

interface CustomerForm {
  id: number | null;
  fullName: string;
  phone: string;
  email: string;
  address: string;
  active: boolean;
}

@Component({
  selector: 'app-customers-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers-page.html',
  styleUrl: './customers-page.scss',
})
export class CustomersPage implements OnInit {
  customers: Customer[] = [];
  loading = false;
  saving = false;
  error = '';
  form: CustomerForm = this.emptyForm();

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    void this.load();
  }

  async load(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.customers = await this.api.get<Customer[]>('/api/customers');
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.loading = false;
    }
  }

  edit(customer: Customer): void {
    this.form = {
      id: customer.id,
      fullName: customer.fullName,
      phone: customer.phone ?? '',
      email: customer.email ?? '',
      address: customer.address ?? '',
      active: customer.active,
    };
  }

  reset(): void {
    this.form = this.emptyForm();
  }

  async save(): Promise<void> {
    if (!this.form.fullName.trim()) {
      return;
    }
    this.saving = true;
    this.error = '';
    const payload = {
      fullName: this.form.fullName.trim(),
      phone: this.form.phone.trim() || null,
      email: this.form.email.trim() || null,
      address: this.form.address.trim() || null,
      active: this.form.active,
    };

    try {
      if (this.form.id) {
        await this.api.put<Customer>(`/api/customers/${this.form.id}`, payload);
      } else {
        await this.api.post<Customer>('/api/customers', payload);
      }
      this.reset();
      await this.load();
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.saving = false;
    }
  }

  async setActive(customer: Customer, active: boolean): Promise<void> {
    await this.api.put<Customer>(`/api/customers/${customer.id}`, {
      fullName: customer.fullName,
      phone: customer.phone ?? null,
      email: customer.email ?? null,
      address: customer.address ?? null,
      active,
    });
    await this.load();
  }

  private emptyForm(): CustomerForm {
    return {
      id: null,
      fullName: '',
      phone: '',
      email: '',
      address: '',
      active: true,
    };
  }
}

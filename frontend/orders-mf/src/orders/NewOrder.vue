<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';

interface Props {
  token?: string | null;
  user?: {
    username?: string;
    displayName?: string;
    role?: string;
  } | null;
}

interface MenuPrice {
  cycle: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  amount: number;
}

interface MenuItem {
  id: number;
  name: string;
  description?: string | null;
  category?: string | null;
  active: boolean;
  prices: MenuPrice[];
}

interface Customer {
  id: number;
  fullName: string;
  phone?: string | null;
  email?: string | null;
  address?: string | null;
  active: boolean;
}

interface CartLine {
  item: MenuItem;
  qty: number;
  cycle: MenuPrice['cycle'];
}

const API_BASE = 'http://localhost:8081';
const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 2,
});

const props = defineProps<Props>();

const menu = ref<MenuItem[]>([]);
const customers = ref<Customer[]>([]);
const cart = ref<CartLine[]>([]);
const loading = ref(true);
const saving = ref(false);
const error = ref('');
const submitted = ref(false);

const form = reactive({
  tableNumber: '',
  notes: '',
  customerId: '' as number | '',
  customerName: '',
  customerPhone: '',
  customerEmail: '',
  customerAddress: '',
});

const activeCustomers = computed(() => customers.value.filter((customer) => customer.active));
const selectedCustomer = computed(() =>
  customers.value.find((customer) => String(customer.id) === String(form.customerId)),
);
const total = computed(() =>
  cart.value.reduce((sum, line) => sum + priceFor(line.item, line.cycle) * line.qty, 0),
);
function headers(): HeadersInit {
  const value: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  if (props.token) {
    value.Authorization = props.token;
  }
  return value;
}

async function fetchJson<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, options);
  if (!response.ok) {
    throw new Error(await response.text() || response.statusText);
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return (await response.json()) as T;
}

function priceFor(item: MenuItem, cycle: MenuPrice['cycle']): number {
  return item.prices.find((price) => price.cycle === cycle)?.amount ?? item.prices[0]?.amount ?? 0;
}

function cycleOptions(item: MenuItem): MenuPrice[] {
  return item.prices;
}

function trimOrNull(value: string): string | null {
  const trimmed = value.trim();
  return trimmed.length === 0 ? null : trimmed;
}

function addItem(item: MenuItem): void {
  const existing = cart.value.find((line) => line.item.id === item.id);
  if (existing) {
    existing.qty += 1;
    return;
  }
  cart.value.push({
    item,
    qty: 1,
    cycle: item.prices[0]?.cycle ?? 'DAILY',
  });
}

function incrementLine(index: number): void {
  cart.value[index].qty += 1;
}

function decrementLine(index: number): void {
  cart.value[index].qty -= 1;
  if (cart.value[index].qty <= 0) {
    cart.value.splice(index, 1);
  }
}

function removeLine(index: number): void {
  cart.value.splice(index, 1);
}

async function load(): Promise<void> {
  loading.value = true;
  error.value = '';

  try {
    if (!props.token) {
      throw new Error('Login through the shell to load live order data.');
    }

    const [items, customerList] = await Promise.all([
      fetchJson<MenuItem[]>('/api/v1/items', { headers: headers() }),
      fetchJson<Customer[]>('/api/customers', { headers: headers() }),
    ]);

    menu.value = items.filter((item) => item.active);
    customers.value = customerList;

    if (!form.customerId && activeCustomers.value.length > 0) {
      form.customerId = activeCustomers.value[0].id;
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err);
  } finally {
    loading.value = false;
  }
}

async function submitOrder(): Promise<void> {
  if (cart.value.length === 0) {
    return;
  }

  saving.value = true;
  error.value = '';

  try {
    const useExistingCustomer = form.customerId !== '';
    await fetchJson('/api/v1/orders', {
      method: 'POST',
      headers: headers(),
      body: JSON.stringify({
        customerId: useExistingCustomer ? Number(form.customerId) : null,
        customerName: useExistingCustomer ? null : trimOrNull(form.customerName),
        customerPhone: useExistingCustomer ? null : trimOrNull(form.customerPhone),
        customerEmail: useExistingCustomer ? null : trimOrNull(form.customerEmail),
        customerAddress: useExistingCustomer ? null : trimOrNull(form.customerAddress),
        tableNumber: trimOrNull(form.tableNumber),
        notes: trimOrNull(form.notes),
        items: cart.value.map((line) => ({
          menuItemId: line.item.id,
          quantity: line.qty,
          priceCycle: line.cycle,
        })),
      }),
    });

    submitted.value = true;
    cart.value = [];
    form.tableNumber = '';
    form.notes = '';
    form.customerName = '';
    form.customerPhone = '';
    form.customerEmail = '';
    form.customerAddress = '';
    form.customerId = activeCustomers.value[0]?.id ?? '';
    await load();
    window.setTimeout(() => {
      submitted.value = false;
    }, 1800);
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err);
  } finally {
    saving.value = false;
  }
}

onMounted(() => {
  void load();
});
</script>

<template>
  <div class="new-order">
    <div class="menu-panel">
      <div class="panel-head">
        <h3>Menu</h3>
        <button class="ghost-btn" type="button" @click="load" :disabled="loading">Refresh</button>
      </div>

      <div v-if="loading" class="empty-state">Loading live menu...</div>
      <div v-else class="menu-grid">
        <button
          class="menu-item"
          v-for="item in menu"
          :key="item.id"
          type="button"
          @click="addItem(item)"
        >
          <span class="menu-item-cat">{{ item.category || 'Menu item' }}</span>
          <span class="menu-item-name">{{ item.name }}</span>
          <span class="menu-item-desc">{{ item.description || 'No description' }}</span>
          <div class="price-row">
            <span class="price-chip" v-for="price in item.prices" :key="price.cycle">
              {{ price.cycle }} {{ currency.format(price.amount) }}
            </span>
          </div>
          <span class="menu-item-action">Add to order</span>
        </button>
      </div>
    </div>

    <div class="cart-panel">
      <div class="panel-head">
        <h3>Order summary</h3>
        <span class="mono">{{ cart.length }} lines</span>
      </div>

      <div class="meta-grid">
        <label>
          Table number
          <input type="text" v-model="form.tableNumber" placeholder="T12 or Takeaway" />
        </label>

        <label>
          Existing customer
          <select v-model="form.customerId">
            <option :value="''">Walk-in / new customer</option>
            <option v-for="customer in activeCustomers" :key="customer.id" :value="customer.id">
              {{ customer.fullName }}{{ customer.phone ? ` - ${customer.phone}` : '' }}
            </option>
          </select>
        </label>
      </div>

      <div v-if="selectedCustomer" class="selected-customer">
        Using saved customer: <strong>{{ selectedCustomer.fullName }}</strong>
      </div>

      <div v-else class="customer-grid">
        <label>
          Customer name
          <input type="text" v-model="form.customerName" placeholder="Guest name" />
        </label>
        <label>
          Phone
          <input type="text" v-model="form.customerPhone" placeholder="Phone number" />
        </label>
        <label>
          Email
          <input type="email" v-model="form.customerEmail" placeholder="customer@example.com" />
        </label>
        <label>
          Address
          <input type="text" v-model="form.customerAddress" placeholder="Delivery address" />
        </label>
      </div>

      <label class="notes">
        Notes
        <textarea v-model="form.notes" rows="3" placeholder="Special instructions"></textarea>
      </label>

      <div v-if="cart.length === 0" class="cart-empty">
        Add menu items to start an order.
      </div>

      <ul v-else class="cart-list">
        <li v-for="(line, index) in cart" :key="line.item.id">
          <div class="cart-line-info">
            <strong>{{ line.item.name }}</strong>
            <span class="cart-line-meta">{{ currency.format(priceFor(line.item, line.cycle)) }} each</span>
          </div>
          <div class="cart-controls">
            <select v-model="line.cycle">
              <option v-for="price in cycleOptions(line.item)" :key="price.cycle" :value="price.cycle">
                {{ price.cycle }}
              </option>
            </select>
            <div class="qty-stepper">
              <button type="button" @click="decrementLine(index)">-</button>
              <span>{{ line.qty }}</span>
              <button type="button" @click="incrementLine(index)">+</button>
            </div>
            <span class="mono">{{ currency.format(priceFor(line.item, line.cycle) * line.qty) }}</span>
            <button type="button" class="link-btn" @click="removeLine(index)">Remove</button>
          </div>
        </li>
      </ul>

      <div class="cart-total">
        <span>Total</span>
        <span class="mono">{{ currency.format(total) }}</span>
      </div>

      <button class="submit-btn" type="button" :disabled="saving || cart.length === 0" @click="submitOrder">
        {{ saving ? 'Saving order...' : 'Create order' }}
      </button>

      <p v-if="submitted" class="success">Order saved to the backend.</p>
    </div>
  </div>
</template>

<style scoped>
.new-order {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 16px;
  align-items: start;
}

.menu-panel,
.cart-panel {
  background: var(--paper);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 20px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.panel-head h3 {
  font-size: 15px;
}

.ghost-btn,
.link-btn {
  border: 1px solid var(--line);
  border-radius: 6px;
  background: #fff;
  color: var(--ink);
  cursor: pointer;
}

.ghost-btn {
  padding: 8px 12px;
}

.link-btn {
  padding: 6px 10px;
  font-size: 12px;
}

.empty-state,
.cart-empty,
.success,
.selected-customer {
  font-size: 13px;
  color: var(--ink-soft);
}

.selected-customer {
  padding: 10px 12px;
  border: 1px solid var(--line);
  border-radius: 6px;
  background: rgba(95, 122, 90, 0.08);
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.menu-item {
  text-align: left;
  background: var(--paper-dim);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  transition: border-color 0.12s ease, background 0.12s ease;
}

.menu-item:hover {
  border-color: var(--ember);
  background: #fff;
}

.menu-item-cat {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--ink-soft);
}

.menu-item-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--ink);
}

.menu-item-desc {
  font-size: 12px;
  color: var(--ink-soft);
  min-height: 32px;
}

.price-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.price-chip {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(91, 86, 74, 0.08);
  color: var(--ink);
}

.menu-item-action {
  font-size: 12px;
  color: var(--ember);
  font-weight: 600;
}

.meta-grid,
.customer-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.meta-grid label,
.customer-grid label,
.notes {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 12px;
  color: var(--ink-soft);
}

.meta-grid input,
.meta-grid select,
.customer-grid input,
.notes textarea,
.cart-controls select {
  border: 1px solid var(--line);
  border-radius: 6px;
  padding: 10px 12px;
  font: inherit;
  color: var(--ink);
  background: #fff;
}

.notes {
  margin-top: 10px;
}

.notes textarea {
  resize: vertical;
}

.cart-list {
  list-style: none;
  margin: 16px 0 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cart-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  border-top: 1px solid var(--line);
  padding-top: 12px;
}

.cart-line-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.cart-line-meta {
  font-size: 11px;
  color: var(--ink-soft);
}

.cart-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.qty-stepper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.qty-stepper button {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 1px solid var(--line);
  background: #fff;
  font-size: 13px;
  line-height: 1;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 600;
  padding-top: 12px;
  border-top: 1px solid var(--line);
  margin-top: 14px;
  margin-bottom: 14px;
}

.submit-btn {
  width: 100%;
  background: var(--char);
  color: var(--paper);
  border: none;
  border-radius: var(--radius);
  padding: 12px;
  font-size: 14px;
  font-weight: 600;
}

.submit-btn:hover:not(:disabled) {
  background: var(--ember);
}

.submit-btn:disabled {
  opacity: 0.5;
}

.mono {
  font-family: var(--font-mono);
}

@media (max-width: 1000px) {
  .new-order,
  .menu-grid,
  .meta-grid,
  .customer-grid {
    grid-template-columns: 1fr;
  }

  .cart-list li {
    align-items: flex-start;
    flex-direction: column;
  }

  .cart-controls {
    justify-content: flex-start;
  }
}
</style>

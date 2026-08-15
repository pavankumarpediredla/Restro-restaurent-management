<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

interface Props {
  token?: string | null;
  user?: {
    username?: string;
    displayName?: string;
    role?: string;
  } | null;
}

interface OrderLine {
  menuItemId: number;
  menuItemName: string;
  priceCycle: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

interface Order {
  id: number;
  orderNumber: string;
  status: 'NEW' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
  customerName?: string | null;
  customerPhone?: string | null;
  tableNumber?: string | null;
  totalAmount: number;
  createdAt: string;
  acceptedAt?: string | null;
  items: OrderLine[];
}

const API_BASE = window.location.hostname === 'localhost'
  ? 'http://localhost:8081'
  : 'https://restro-restaurent-management.onrender.com';
const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 2,
});

const props = defineProps<Props>();

const orders = ref<Order[]>([]);
const loading = ref(true);
const error = ref('');

const activeOrders = computed(() =>
  orders.value.filter((order) => order.status !== 'REJECTED' && order.status !== 'CANCELLED'),
);
const displayName = computed(() => props.user?.displayName || props.user?.username || 'team');

async function fetchJson<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, options);
  if (!response.ok) {
    throw new Error(await response.text() || response.statusText);
  }
  return (await response.json()) as T;
}

function headers(): HeadersInit {
  const value: Record<string, string> = {};
  if (props.token) {
    value.Authorization = props.token;
  }
  return value;
}

function statusClass(status: Order['status']): string {
  return status.toLowerCase().replace('_', '-');
}

function itemSummary(order: Order): string {
  return order.items.map((line) => `${line.menuItemName} x${line.quantity}`).join(', ');
}

async function load(): Promise<void> {
  loading.value = true;
  error.value = '';

  try {
    if (!props.token) {
      throw new Error('Login through the shell to load live order data.');
    }
    orders.value = await fetchJson<Order[]>('/api/v1/orders', {
      headers: headers(),
    });
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  void load();
});
</script>

<template>
  <div class="running-orders">
    <div class="panel-head">
      <div>
        <h3>Running Orders</h3>
        <p>Live queue for {{ displayName }}.</p>
      </div>
      <button class="ghost-btn" type="button" @click="load" :disabled="loading">Refresh</button>
    </div>

    <div v-if="error" class="alert">{{ error }}</div>
    <div v-if="loading" class="loading">Loading active orders...</div>

    <div v-else class="panel">
      <table class="orders-table">
        <thead>
          <tr>
            <th>Order</th>
            <th>Customer</th>
            <th>Items</th>
            <th>Placed</th>
            <th>Total</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in activeOrders" :key="order.id">
            <td class="mono">{{ order.orderNumber }}</td>
            <td>
              <strong>{{ order.customerName || 'Walk-in' }}</strong>
              <div class="muted">{{ order.tableNumber || '-' }}</div>
            </td>
            <td class="items-cell">{{ itemSummary(order) }}</td>
            <td class="mono">{{ order.createdAt }}</td>
            <td class="mono">{{ currency.format(order.totalAmount) }}</td>
            <td>
              <span :class="['status-pill', statusClass(order.status)]">{{ order.status }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.running-orders {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
}

.panel-head h3 {
  font-size: 15px;
}

.panel-head p {
  margin: 4px 0 0;
  color: var(--ink-soft);
  font-size: 14px;
}

.panel {
  background: var(--paper);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 20px;
  overflow: auto;
}

.ghost-btn {
  border: 1px solid var(--line);
  border-radius: 6px;
  background: #fff;
  color: var(--ink);
  cursor: pointer;
  padding: 8px 12px;
}

.loading,
.alert {
  font-size: 13px;
}

.alert {
  color: var(--rust);
  background: rgba(179, 70, 44, 0.08);
  border: 1px solid rgba(179, 70, 44, 0.2);
  padding: 12px 14px;
  border-radius: 6px;
}

.loading {
  color: var(--ink-soft);
  padding: 12px 0;
}

.orders-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.orders-table th {
  text-align: left;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  color: var(--ink-soft);
  font-weight: 600;
  padding: 0 8px 10px;
  border-bottom: 1px solid var(--line);
}

.orders-table td {
  padding: 12px 8px;
  border-bottom: 1px solid var(--line);
  vertical-align: top;
}

.orders-table tr:last-child td {
  border-bottom: none;
}

.items-cell {
  max-width: 320px;
}

.mono {
  font-family: var(--font-mono);
  font-size: 12px;
}

.muted {
  color: var(--ink-soft);
  font-size: 12px;
  margin-top: 4px;
}

.status-pill {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 9px;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.status-pill.new {
  background: rgba(198, 138, 59, 0.15);
  color: var(--amber);
}

.status-pill.accepted {
  background: rgba(95, 122, 90, 0.15);
  color: var(--sage);
}

.status-pill.rejected {
  background: rgba(179, 70, 44, 0.12);
  color: var(--rust);
}

.status-pill.cancelled {
  background: rgba(91, 86, 74, 0.12);
  color: var(--ink-soft);
}
</style>

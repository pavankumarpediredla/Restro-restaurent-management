<script setup lang="ts">
interface RunningOrder {
  id: string;
  table: string;
  items: string[];
  total: number;
  status: 'New' | 'In Kitchen' | 'Ready' | 'Billed';
  placedAt: string;
}

// TEMP mock data — swap for GET /api/orders?status=active once order-service is live
const orders: RunningOrder[] = [
  { id: '#1042', table: 'T5', items: ['Paneer Butter Masala', 'Butter Naan x2'], total: 860, status: 'In Kitchen', placedAt: '7:42 PM' },
  { id: '#1041', table: 'T2', items: ['Cold Coffee x2'], total: 180, status: 'Ready', placedAt: '7:38 PM' },
  { id: '#1040', table: 'Takeaway', items: ['Masala Dosa'], total: 110, status: 'New', placedAt: '7:44 PM' },
  { id: '#1039', table: 'T8', items: ['Veg Biryani x2', 'Gulab Jamun'], total: 440, status: 'Billed', placedAt: '7:20 PM' },
];

function statusClass(status: RunningOrder['status']): string {
  return status.toLowerCase().replace(' ', '-');
}
</script>

<template>
  <div class="running-orders">
    <div class="panel">
      <table class="orders-table">
        <thead>
          <tr>
            <th>Order</th>
            <th>Table</th>
            <th>Items</th>
            <th>Total</th>
            <th>Placed</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id">
            <td class="mono">{{ o.id }}</td>
            <td>{{ o.table }}</td>
            <td class="items-cell">{{ o.items.join(', ') }}</td>
            <td class="mono">₹{{ o.total }}</td>
            <td class="mono">{{ o.placedAt }}</td>
            <td>
              <span :class="['status-pill', statusClass(o.status)]">{{ o.status }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.panel {
  background: var(--paper);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 20px;
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
}
.orders-table tr:last-child td {
  border-bottom: none;
}

.items-cell {
  color: var(--ink-soft);
  max-width: 260px;
}

.mono {
  font-family: var(--font-mono);
  font-size: 12px;
}

.status-pill {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 9px;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}
.status-pill.new { background: rgba(198, 138, 59, 0.15); color: var(--amber); }
.status-pill.in-kitchen { background: rgba(217, 119, 6, 0.15); color: var(--ember); }
.status-pill.ready { background: rgba(95, 122, 90, 0.15); color: var(--sage); }
.status-pill.billed { background: rgba(91, 86, 74, 0.12); color: var(--ink-soft); }
</style>

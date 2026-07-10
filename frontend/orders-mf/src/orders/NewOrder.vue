<script setup lang="ts">
import { computed, ref } from 'vue';

interface MenuItem {
  id: string;
  name: string;
  price: number;
  category: string;
}

interface CartLine {
  item: MenuItem;
  qty: number;
}

// TEMP mock menu — swap for GET /api/inventory/menu-items once inventory-service is live
const menu: MenuItem[] = [
  { id: 'm1', name: 'Paneer Butter Masala', price: 220, category: 'Main' },
  { id: 'm2', name: 'Butter Naan', price: 40, category: 'Bread' },
  { id: 'm3', name: 'Veg Biryani', price: 190, category: 'Main' },
  { id: 'm4', name: 'Masala Dosa', price: 110, category: 'South Indian' },
  { id: 'm5', name: 'Cold Coffee', price: 90, category: 'Beverage' },
  { id: 'm6', name: 'Gulab Jamun (2 pc)', price: 60, category: 'Dessert' },
];

const tables = ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'Takeaway'];

const selectedTable = ref(tables[0]);
const cart = ref<CartLine[]>([]);
const submitted = ref(false);

const total = computed(() =>
  cart.value.reduce((sum, line) => sum + line.item.price * line.qty, 0)
);

function addItem(item: MenuItem) {
  const existing = cart.value.find((l) => l.item.id === item.id);
  if (existing) {
    existing.qty++;
  } else {
    cart.value.push({ item, qty: 1 });
  }
}

function decrementItem(item: MenuItem) {
  const existing = cart.value.find((l) => l.item.id === item.id);
  if (!existing) return;
  existing.qty--;
  if (existing.qty <= 0) {
    cart.value = cart.value.filter((l) => l.item.id !== item.id);
  }
}

/**
 * TEMP: mock submit. Replace with a real call once order-service /
 * the gateway is up:
 *
 *   await fetch('/api/orders', {
 *     method: 'POST',
 *     headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
 *     body: JSON.stringify({ tableNumber: selectedTable.value, items: cart.value }),
 *   });
 */
function submitOrder() {
  if (cart.value.length === 0) return;
  submitted.value = true;
  setTimeout(() => {
    submitted.value = false;
    cart.value = [];
  }, 1800);
}
</script>

<template>
  <div class="new-order">
    <div class="menu-panel">
      <div class="panel-head">
        <h3>Menu</h3>
        <label class="table-select">
          Table
          <select v-model="selectedTable">
            <option v-for="t in tables" :key="t" :value="t">{{ t }}</option>
          </select>
        </label>
      </div>

      <div class="menu-grid">
        <button class="menu-item" v-for="item in menu" :key="item.id" @click="addItem(item)">
          <span class="menu-item-cat">{{ item.category }}</span>
          <span class="menu-item-name">{{ item.name }}</span>
          <span class="menu-item-price mono">₹{{ item.price }}</span>
        </button>
      </div>
    </div>

    <div class="cart-panel">
      <div class="panel-head">
        <h3>Order Summary</h3>
      </div>

      <div v-if="cart.length === 0" class="cart-empty">
        Tap a menu item to add it to the order.
      </div>

      <ul v-else class="cart-list">
        <li v-for="line in cart" :key="line.item.id">
          <div class="cart-line-info">
            <span class="cart-line-name">{{ line.item.name }}</span>
            <span class="cart-line-price mono">₹{{ line.item.price * line.qty }}</span>
          </div>
          <div class="qty-stepper">
            <button @click="decrementItem(line.item)">−</button>
            <span class="mono">{{ line.qty }}</span>
            <button @click="addItem(line.item)">+</button>
          </div>
        </li>
      </ul>

      <div class="cart-total">
        <span>Total</span>
        <span class="mono">₹{{ total }}</span>
      </div>

      <button class="submit-btn" :disabled="cart.length === 0" @click="submitOrder">
        {{ submitted ? 'Order sent to kitchen ✓' : `Send order for ${selectedTable}` }}
      </button>
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

.menu-panel, .cart-panel {
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

.table-select {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--ink-soft);
}
.table-select select {
  font-family: var(--font-mono);
  font-size: 12px;
  padding: 5px 8px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: #fff;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
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
.menu-item-price {
  font-size: 12px;
  color: var(--amber);
}

.cart-empty {
  font-size: 13px;
  color: var(--ink-soft);
  padding: 20px 0;
  text-align: center;
}

.cart-list {
  list-style: none;
  margin: 0 0 14px;
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
}

.cart-line-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.cart-line-name {
  font-size: 13px;
}
.cart-line-price {
  font-size: 11px;
  color: var(--ink-soft);
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
</style>

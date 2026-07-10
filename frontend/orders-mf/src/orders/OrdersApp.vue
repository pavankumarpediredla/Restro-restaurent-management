<script setup lang="ts">
import { ref } from 'vue';
import NewOrder from './NewOrder.vue';
import RunningOrders from './RunningOrders.vue';

defineProps<{
  token?: string | null;
  user?: { name: string; role: string } | null;
}>();

const tab = ref<'new' | 'running'>('new');
</script>

<template>
  <div class="orders-app">
    <div class="page-head">
      <h2>Orders</h2>
      <p>Create new orders and track what's running on the floor.</p>
    </div>

    <div class="tabs">
      <button :class="['tab', { active: tab === 'new' }]" @click="tab = 'new'">
        New Order
      </button>
      <button :class="['tab', { active: tab === 'running' }]" @click="tab = 'running'">
        Running Orders
      </button>
    </div>

    <NewOrder v-if="tab === 'new'" />
    <RunningOrders v-else />
  </div>
</template>

<style scoped>
.orders-app {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 1100px;
}

.page-head h2 {
  font-size: 24px;
}
.page-head p {
  margin: 4px 0 0;
  color: var(--ink-soft);
  font-size: 14px;
}

.tabs {
  display: flex;
  gap: 4px;
  border-bottom: 1px solid var(--line);
}

.tab {
  background: none;
  border: none;
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 600;
  color: var(--ink-soft);
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}

.tab.active {
  color: var(--ember);
  border-bottom-color: var(--ember);
}
</style>

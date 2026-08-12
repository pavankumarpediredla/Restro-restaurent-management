<script setup lang="ts">
import { computed, ref } from 'vue';
import NewOrder from './NewOrder.vue';
import RunningOrders from './RunningOrders.vue';

const props = defineProps<{
  token?: string | null;
  user?: {
    username?: string;
    displayName?: string;
    role?: string;
  } | null;
}>();

const tab = ref<'new' | 'running'>('new');

const displayName = computed(() => props.user?.displayName || props.user?.username || 'team');
</script>

<template>
  <div class="orders-app">
    <div class="page-head">
      <div>
        <h2>Orders</h2>
        <p>Live order entry and queue tracking for {{ displayName }}.</p>
      </div>
    </div>

    <div class="tabs">
      <button :class="['tab', { active: tab === 'new' }]" @click="tab = 'new'">
        New Order
      </button>
      <button :class="['tab', { active: tab === 'running' }]" @click="tab = 'running'">
        Running Orders
      </button>
    </div>

    <NewOrder v-if="tab === 'new'" :token="props.token" :user="props.user" />
    <RunningOrders v-else :token="props.token" :user="props.user" />
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

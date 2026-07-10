<!-- Standalone dev harness — lets you run `npm run dev` and see Orders
     on its own at http://localhost:5174, without the Shell.
     The Shell itself never imports this file — it only imports mount.ts -->
<script setup lang="ts">
import OrdersApp from './orders/OrdersApp.vue';
</script>

<template>
  <OrdersApp />
</template>

<template>
  <div class="d-flex w-100">
    <!-- 1. Barra Lateral -->
    <Sidebar />

    <!-- 2. Área Principal de Contenido -->
    <main class="flex-grow-1 p-4 overflow-auto">
      <!-- Aquí se renderizarán las vistas como CrearProductoView.vue o InventarioView.vue -->
      <router-view />
    </main>
  </div>
</template>

<script setup>
import Sidebar from '@/components/comun/Sidebar.vue';
// Aquí se podría añadir lógica para verificar si el usuario es un 'vendedor'
// y redirigirlo si no lo es, asegurando que solo los vendedores usen este layout.
</script>

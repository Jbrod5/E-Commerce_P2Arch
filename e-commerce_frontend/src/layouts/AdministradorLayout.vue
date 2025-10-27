<template>
  <div class="d-flex w-100">
  <!-- 1. Barra Lateral -->
    <AdminSidebar />

    <!-- 2. Área Principal de Contenido -->
    <!-- Se aplica un margen izquierdo para que el contenido no quede debajo del sidebar fijo -->
    <main class="flex-grow-1 p-4" style="margin-left: 250px;">
    <!-- Aquí se renderizarán las vistas del administrador (Dashboard, Gestión Empleados, etc.) -->
      <router-view />
    </main>
  </div>
</template>

<script setup>
import AdminSidebar from '@/components/administrador/AdministradorSidebar.vue';
</script>

<template>
  <div class="container py-5">
    <h1 class="mb-5">Historial de Mis Pedidos</h1>

    <div v-if="isLoading" class="text-center p-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Cargando pedidos...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else-if="pedidos.length === 0" class="alert alert-info text-center">
      Aún no tienes pedidos en tu historial.
    </div>

    <div v-else class="list-group">
      <router-link 
        v-for="pedido in pedidos" 
        :key="pedido.idPedido" 
        :to="{ name: 'detallePedido', params: { id: pedido.idPedido } }"
        class="list-group-item list-group-item-action pedido-resumen d-flex justify-content-between align-items-center"
      >
        <div>
          <h5 class="mb-1 fw-bold">Pedido #{{ pedido.idPedido }}</h5>
          <p class="mb-1 text-muted">Realizado: {{ formatDate(pedido.fechaRealizacion) }}</p>
          <small class="text-truncate" style="max-width: 90%;">Envío a: {{ pedido.direccion }}</small>
        </div>
        <div class="text-end">
          <span :class="['badge', getStatusBadge(pedido.estadoNombre)]">{{ pedido.estadoNombre }}</span>
          <h4 class="mt-2 mb-0">{{ formatCurrency(pedido.montoTotal) }}</h4>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { obtenerMisPedidos } from '@/api/pedido'; // Importamos la función API

const pedidos = ref([]);
const isLoading = ref(false);
const error = ref(null);

// --- Utilidades ---
const formatCurrency = (amount) => {
    const num = parseFloat(amount); 
    if (isNaN(num)) return 'Q0.00';
    return new Intl.NumberFormat('es-GT', { style: 'currency', currency: 'GTQ' }).format(num);
};

const formatDate = (dateString) => {
    const date = new Date(dateString);
    if (isNaN(date)) return 'N/A';
    return date.toLocaleDateString('es-GT', { year: 'numeric', month: 'short', day: 'numeric' });
};

const getStatusBadge = (status) => {
    switch (status.toLowerCase()) {
        case 'entregado': return 'bg-success';
        case 'enviado': return 'bg-info';
        case 'en curso': return 'bg-warning text-dark';
        default: return 'bg-secondary';
    }
};

// --- Lógica de Carga ---
const loadPedidos = async () => {
    isLoading.value = true;
    error.value = null;
    try {
        pedidos.value = await obtenerMisPedidos();
    } catch (err) {
        error.value = "No se pudieron cargar los pedidos: " + (err.response?.data || err.message);
    } finally {
        isLoading.value = false;
    }
};

onMounted(loadPedidos);
</script>

<style scoped>
.pedido-resumen {
    padding: 15px;
    border-radius: 5px;
    transition: background-color 0.2s;
}
.pedido-resumen:hover {
    background-color: #f8f9fa;
}
</style>
<template>
  <div class="container py-5">
    <router-link :to="{ name: 'misPedidos' }" class="btn btn-outline-secondary btn-sm mb-4">
        <i class="bi bi-arrow-left me-2"></i> Volver a Mis Pedidos
    </router-link>

    <h1 class="mb-4">Detalle del Pedido #{{ id }}</h1>

    <div v-if="isLoading" class="text-center p-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Cargando detalles...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else-if="pedido" class="row">
      <div class="col-md-5">
        <div class="card shadow-sm p-4 mb-4">
          <h4 class="mb-3">Información General</h4>
          <p><strong>Estado Actual:</strong> 
            <span :class="['badge', 'fs-6', getStatusBadge(pedido.estadoNombre)]">{{ pedido.estadoNombre }}</span>
          </p>
          <hr>
          <p class="mb-1"><strong>Realizado en:</strong> {{ formatDate(pedido.fechaRealizacion) }}</p>
          <p class="mb-1"><strong>Entrega Estimada:</strong> {{ formatDate(pedido.fechaEntregaEstimada) }}</p>
          <p class="mb-1" v-if="pedido.fechaEntregaReal"><strong>Entregado el:</strong> {{ formatDate(pedido.fechaEntregaReal) }}</p>
          <p class="mb-1" v-else><strong>Entregado el:</strong> Pendiente</p>
          <hr>
          <p class="mb-1"><strong>Tarjeta:</strong> **** **** **** {{ pedido.tarjetaParteVisible }}</p>
          <p class="mb-0"><strong>Dirección:</strong> {{ pedido.direccion }}</p>
        </div>
      </div>

      <div class="col-md-7">
        <div class="card shadow-sm p-4">
          <h4 class="mb-3">Artículos del Pedido</h4>
          <div class="table-responsive">
            <table class="table table-striped table-sm">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th class="text-end">Cantidad</th>
                  <th class="text-end">P. Unitario</th>
                  <th class="text-end">Subtotal</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in pedido.items" :key="item.idProducto">
                  <td>{{ item.nombreProducto }}</td>
                  <td class="text-end">{{ item.cantidad }}</td>
                  <td class="text-end">{{ formatCurrency(item.precioUnitario) }}</td>
                  <td class="text-end fw-bold">{{ formatCurrency(item.subtotal) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="d-flex justify-content-between border-top pt-3">
            <h4 class="fw-bold">Total Final:</h4>
            <h4><strong>{{ formatCurrency(pedido.montoTotal) }}</strong></h4>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { obtenerDetallePedido } from '@/api/pedido'; // Importamos la función API
import { useRoute } from 'vue-router';

//  La ruta dinámica /pedidos/:id inyecta el ID como prop si se usa props: true en el router.
// Sin embargo, usar useRoute es más común en Composition API.
const route = useRoute();
const id = ref(route.params.id);

const pedido = ref(null);
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
    if (isNaN(date.getTime()) || dateString === null) return 'N/A';
    return date.toLocaleDateString('es-GT', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
};

const getStatusBadge = (status) => {
    switch (status?.toLowerCase()) {
        case 'entregado': return 'bg-success';
        case 'enviado': return 'bg-info';
        case 'en curso': return 'bg-warning text-dark';
        default: return 'bg-secondary';
    }
};

// --- Lógica de Carga ---
const loadDetallePedido = async (pedidoId) => {
    if (!pedidoId) return;

    isLoading.value = true;
    error.value = null;
    pedido.value = null;
    try {
        pedido.value = await obtenerDetallePedido(pedidoId);
    } catch (err) {
        const message = err.response?.status === 404 
            ? `Pedido #${pedidoId} no encontrado o no te pertenece.`
            : "Error al cargar el detalle del pedido: " + (err.response?.data || err.message);
        error.value = message;
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    loadDetallePedido(id.value);
});

// Observa si el ID del pedido cambia en la ruta (útil si navegamos entre detalles)
watch(() => route.params.id, (newId) => {
    id.value = newId;
    loadDetallePedido(newId);
});
</script>

<style scoped>
.badge {
    min-width: 90px;
}
</style>
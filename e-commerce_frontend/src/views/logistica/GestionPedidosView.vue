<template>
  <div class="gestion-pedidos-logistica container mt-4">
    <h1 class="mb-4 text-primary">Gestión de Pedidos</h1>
    <p class="lead">Órdenes pendientes de entrega (En Curso y Enviadas).</p>

    <div v-if="mensaje.texto" :class="['alert', `alert-${mensaje.tipo}`, 'd-flex align-items-center']" role="alert">
        <i :class="['bi me-2', mensaje.tipo === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill']"></i>
        <div>
            {{ mensaje.texto }}
        </div>
        <button type="button" class="btn-close ms-auto" @click="mensaje.texto = ''" aria-label="Close"></button>
    </div>

    <div v-if="cargando" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-2">Cargando pedidos pendientes...</p>
    </div>

    <div v-else-if="pedidos.length === 0" class="alert alert-info text-center mt-5">
      <i class="bi bi-info-circle me-2"></i> No hay pedidos pendientes de gestión (en curso o enviados). ¡Todo al día!
    </div>

    <div v-else class="table-responsive">
      <table class="table table-striped table-hover align-middle shadow-sm">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Estado</th>
            <th>Fecha Realización</th>
            <th>Monto</th>
            <th>Dirección</th>
            <th>Acción</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pedido in pedidos" :key="pedido.idPedido">
            <td>#{{ pedido.idPedido }}</td>
            <td>
              <span 
                :class="getStatusClass(pedido.estadoNombre)"
                class="badge p-2"
              >
                {{ pedido.estadoNombre.toUpperCase() }}
              </span>
            </td>
            <td>{{ formatDate(pedido.fechaRealizacion) }}</td>
            <td>Q {{ pedido.montoTotal.toFixed(2) }}</td>
            <td style="max-width: 200px; white-space: normal;">{{ pedido.direccion }}</td>
            <td>
              <button 
                @click="avanzarEstado(pedido.idPedido, pedido.estadoNombre)" 
                :disabled="avanzando[pedido.idPedido] || pedido.estadoNombre === 'entregado'"
                :class="getButtonClass(pedido.estadoNombre)"
                class="btn btn-sm"
              >
                <span v-if="avanzando[pedido.idPedido]" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                {{ getButtonText(pedido.estadoNombre) }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/plugins/axios.js'; // Asume esta ruta para tu instancia de Axios

const pedidos = ref([]);
const cargando = ref(true);
const avanzando = ref({}); // Para controlar el estado de carga por pedido
const mensaje = ref({
    texto: '',
    tipo: 'success' // success, danger
});

onMounted(() => {
  obtenerPedidos();
});

const obtenerPedidos = async () => {
  cargando.value = true;
  mensaje.value.texto = ''; 
  try {
    const response = await api.get('/logistica/pedidos');
    pedidos.value = response.data;
  } catch (error) {
    console.error('Error al obtener pedidos:', error);
    mostrarMensaje('Error al cargar la lista de pedidos.', 'danger');
  } finally {
    cargando.value = false;
  }
};

const avanzarEstado = async (idPedido, estadoActual) => {
  if (estadoActual === 'entregado') return;

  avanzando.value = { ...avanzando.value, [idPedido]: true };
  mensaje.value.texto = '';

  try {
    const response = await api.patch(`/logistica/pedidos/${idPedido}/avanzar`);

    mostrarMensaje(response.data.mensaje || `Pedido #${idPedido} actualizado correctamente.`, 'success');
    
    // Recargar la lista para mostrar el nuevo estado
    obtenerPedidos(); 
    
  } catch (error) {
    console.error('Error al avanzar estado:', error);
    
    const errorMsg = error.response?.data?.error || 'No se pudo avanzar el estado del pedido. Revise la consola.';
    mostrarMensaje(errorMsg, 'danger');

  } finally {
    avanzando.value = { ...avanzando.value, [idPedido]: false };
  }
};

const mostrarMensaje = (texto, tipo) => {
    mensaje.value.texto = texto;
    mensaje.value.tipo = tipo;
    // Opcional: desaparecer el mensaje después de unos segundos
    // setTimeout(() => { mensaje.value.texto = ''; }, 5000); 
};

// --- Utilidades de Interfaz ---

const formatDate = (dateString) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('es-GT', { 
        year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' 
    });
};

const getStatusClass = (estado) => {
  if (estado === 'en curso') return 'bg-warning text-dark';
  if (estado === 'enviado') return 'bg-info';
  if (estado === 'entregado') return 'bg-success';
  return 'bg-secondary';
};

const getButtonText = (estado) => {
  if (estado === 'en curso') return 'Marcar como ENVIADO';
  if (estado === 'enviado') return 'Marcar como ENTREGADO';
  return 'Entregado';
};

const getButtonClass = (estado) => {
  if (estado === 'en curso') return 'btn-primary';
  if (estado === 'enviado') return 'btn-success';
  return 'btn-secondary disabled';
};
</script>

<style scoped>
.gestion-pedidos-logistica {
  max-width: 1400px;
}
.table-striped > tbody > tr:nth-child(odd) > td {
  background-color: #f8f9fa; /* Color de fondo para filas impares */
}
</style>
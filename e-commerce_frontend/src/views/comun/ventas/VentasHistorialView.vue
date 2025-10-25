<template>
  <div class="container mt-4">
    <h2 class="mb-4">Historial de Ventas</h2>
    
    <!-- Filtros por Fecha -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3 align-items-end">
          <div class="col-md-4">
            <label class="form-label">Fecha desde</label>
            <input 
              type="date" 
              class="form-control" 
              v-model="fechaDesde"
            >
          </div>
          <div class="col-md-4">
            <label class="form-label">Fecha hasta</label>
            <input 
              type="date" 
              class="form-control" 
              v-model="fechaHasta"
            >
          </div>
          <div class="col-md-4">
            <button 
              class="btn btn-outline-secondary w-100"
              @click="limpiarFiltros"
            >
              Limpiar Filtros
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="card">
      <div class="card-body p-4">
        <!-- Estado de Carga -->
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
          <p class="mt-2">Cargando detalles de ventas...</p>
        </div>

        <!-- Estado de Error -->
        <div v-else-if="error" class="alert alert-danger" role="alert">
          <h4 class="alert-heading">Error al cargar datos</h4>
          <p>{{ error }}</p>
        </div>

        <!-- Estado de Datos Vacíos -->
        <div v-else-if="ventasFiltradas.length === 0" class="alert alert-warning text-center" role="alert">
          <h4 class="alert-heading">No se encontraron ventas</h4>
          <p class="mb-0" v-if="filtroActivo">
            No hay ventas en el rango de fechas seleccionado.
          </p>
          <p class="mb-0" v-else>
            Parece que aún no hay registros de ventas.
          </p>
        </div>

        <!-- Tabla de Datos (Si hay ventas) -->
        <div v-else class="table-responsive">
          <table class="table table-striped table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th>Fecha</th>
                <th>Producto</th>
                <th>Cantidad</th>
                <th class="text-end">Precio Unitario</th>
                <th class="text-end">Subtotal</th>
                <th class="text-end">Ganancia Neta<br>(95% vendedor)</th>
                <th>ID Pedido</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="venta in ventasFiltradas" :key="venta.idDetalleVenta">
                <td>{{ formatFecha(venta.fecha) }}</td>
                <td>{{ venta.nombreProducto }}</td>
                <td>{{ venta.cantidad }}</td>
                <td class="text-end">{{ formatMoneda(venta.precioUnitario) }}</td>
                <td class="text-end">{{ formatMoneda(venta.subtotal) }}</td>
                <td class="text-end fw-bold text-success">{{ formatMoneda(venta.gananciaVendedor) }}</td>
                <td><span class="badge bg-secondary">{{ venta.idPedido }}</span></td>
              </tr>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="5" class="text-end fw-bold table-active">Total Ganancia:</td>
                    <td class="text-end fw-bold text-success table-active">{{ formatMoneda(totalGanancia) }}</td>
                    <td class="table-active"></td>
                </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useAuthStore } from '@/stores/auth';
import api from '@/plugins/axios.js';

const API_BASE_URL = "/ingresos/vendedor";

// --- Estados ---
const ventas = ref([]);
const loading = ref(true);
const error = ref(null);
const authStore = useAuthStore();
const fechaDesde = ref('');
const fechaHasta = ref('');

// --- Computed Properties ---
const filtroActivo = computed(() => {
  return fechaDesde.value !== '' || fechaHasta.value !== '';
});

const ventasFiltradas = computed(() => {
  if (!filtroActivo.value) {
    return ventas.value;
  }

  return ventas.value.filter(venta => {
    const fechaVenta = new Date(venta.fecha);
    let cumpleFiltro = true;

    if (fechaDesde.value) {
      const desde = new Date(fechaDesde.value);
      cumpleFiltro = cumpleFiltro && fechaVenta >= desde;
    }

    if (fechaHasta.value) {
      const hasta = new Date(fechaHasta.value);
      // Ajustamos la fecha hasta para incluir todo el día
      hasta.setHours(23, 59, 59, 999);
      cumpleFiltro = cumpleFiltro && fechaVenta <= hasta;
    }

    return cumpleFiltro;
  });
});

const totalGanancia = computed(() => {
  if (ventasFiltradas.value.length === 0) return 0;
  return ventasFiltradas.value.reduce((sum, venta) => {
    return sum + parseFloat(venta.gananciaVendedor || 0);
  }, 0);
});

// --- Métodos ---
const fetchVentas = async () => {
  loading.value = true;
  error.value = null;
  
  const correoVendedor = authStore.user?.correo;
  
  if (!correoVendedor) {
    error.value = "No se encontró el correo de usuario en la sesión.";
    loading.value = false;
    return;
  }
  
  try {
    const url = `${API_BASE_URL}/${correoVendedor}`;
    const response = await api.get(url);
    
    if (response.data && response.data.length > 0) {
      ventas.value = response.data;
    } else {
      ventas.value = [];
    }

  } catch (err) {
    if (err.response) {
      if (err.response.status === 404 && err.response.data && err.response.data.error) {
        error.value = err.response.data.error;
      } else {
        error.value = `Error ${err.response.status}: Fallo al cargar los datos de ventas.`;
      }
    } else {
      error.value = `Error de red: No se pudo conectar con el servidor.`;
    }
    console.error("Fallo al obtener ventas:", err);
    ventas.value = [];
  } finally {
    loading.value = false;
  }
};

const limpiarFiltros = () => {
  fechaDesde.value = '';
  fechaHasta.value = '';
};

const formatFecha = (fecha) => {
  if (!fecha) return 'N/A';
  try {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-GT', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric'
    });
  } catch (e) {
    return fecha;
  }
};

// Cambiado a Quetzales (GTQ)
const formatMoneda = (valor) => {
  if (valor === null || valor === undefined) return 'Q 0.00';
  const num = parseFloat(valor);
  return num.toLocaleString('es-GT', {
    style: 'currency',
    currency: 'GTQ',
    minimumFractionDigits: 2
  });
};

onMounted(() => {
  if (authStore.user?.correo) {
    fetchVentas();
  } else {
    error.value = "No se ha encontrado una sesión de usuario activa.";
    loading.value = false;
  }
});
</script>
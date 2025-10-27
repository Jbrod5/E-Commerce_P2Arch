<template>
  <div class="p-3 p-md-5">
    <h1 class="text-3xl font-weight-bold mb-4">Panel de Administración - Reportes</h1>

    <div class="card shadow-sm mb-5">
      <div class="card-body">
        <h2 class="h5 card-title mb-3">Filtros de Reporte</h2>
        <div class="row g-3">
          <div class="col-12 col-md-4">
            <label for="fechaInicio" class="form-label text-sm">Fecha de Inicio</label>
            <input type="datetime-local" id="fechaInicio" v-model="filtro.fechaInicio" class="form-control" />
          </div>
          <div class="col-12 col-md-4">
            <label for="fechaFin" class="form-label text-sm">Fecha de Fin</label>
            <input type="datetime-local" id="fechaFin" v-model="filtro.fechaFin" class="form-control" />
          </div>
          <div class="col-12 col-md-4 d-flex align-items-end">
            <button @click="cargarReportes" :disabled="!filtro.fechaInicio || !filtro.fechaFin" class="btn btn-primary w-100" style="min-height: 38px;">
              Generar Reportes
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-4 mb-5">
      
      <div class="col-12 col-lg-6">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <h2 class="h5 card-title text-indigo-700 mb-3">Top 10 Productos Más Vendidos</h2>
            <p v-if="cargando" class="text-secondary">Cargando...</p>
            <p v-else-if="reporteProductos.length === 0" class="text-danger">No hay datos de ventas en el intervalo seleccionado.</p>
            <div v-else class="list-group list-group-flush reporte-scrollable">
              <div v-for="(item, index) in reporteProductos" :key="index" class="list-group-item d-flex justify-content-between align-items-center bg-light-subtle border-0 rounded my-1">
                <span class="font-weight-medium text-gray-800">{{ index + 1 }}. {{ item.nombreProducto }}</span>
                <span class="h6 mb-0 text-success">{{ item.totalVendido }} unidades</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-12 col-lg-6">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <h2 class="h5 card-title text-orange-700 mb-3">Top 5 Vendedores por Unidades Vendidas</h2>
            <p v-if="cargando" class="text-secondary">Cargando...</p>
            <p v-else-if="reporteVendedores.length === 0" class="text-danger">No hay datos de ventas de vendedores en el intervalo seleccionado.</p>
            <div v-else class="list-group list-group-flush reporte-scrollable">
              <div v-for="(item, index) in reporteVendedores" :key="index" class="list-group-item d-flex justify-content-between align-items-center bg-light-subtle border-0 rounded my-1">
                <span class="font-weight-medium text-gray-800">{{ index + 1 }}. {{ item.nombreVendedor }}</span>
                <span class="h6 mb-0 text-orange-600">{{ item.totalUnidadesVendidas }} unidades</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-6">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <h2 class="h5 card-title text-blue-700 mb-3">Top 5 Clientes (Compradores) por Ganancia Generada</h2>
            <p v-if="cargando" class="text-secondary">Cargando...</p>
            <p v-else-if="reporteCompradores.length === 0" class="text-danger">No hay datos de compras en el intervalo seleccionado.</p>
            <div v-else class="list-group list-group-flush reporte-scrollable">
              <div v-for="(item, index) in reporteCompradores" :key="index" class="list-group-item d-flex justify-content-between align-items-center bg-light-subtle border-0 rounded my-1">
                <span class="font-weight-medium text-gray-800">{{ index + 1 }}. {{ item.nombreCliente }}</span>
                <div class="text-end">
                  <p class="h6 mb-0 text-blue-600">Total Comprado: Q{{ parseFloat(item.montoTotalCompras).toFixed(2) }}</p>
                  <p class="small text-muted mb-0">Ganancia Plataforma (5%): Q{{ parseFloat(item.gananciaPlataforma).toFixed(2) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-12 col-lg-6">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <h2 class="h5 card-title text-purple-700 mb-3">Top 10 Clientes con Más Pedidos</h2>
            <p v-if="cargando" class="text-secondary">Cargando...</p>
            <p v-else-if="reportePedidos.length === 0" class="text-danger">No hay datos de pedidos en el intervalo seleccionado.</p>
            <div v-else class="list-group list-group-flush reporte-scrollable">
              <li v-for="(item, index) in reportePedidos" :key="index" class="list-group-item d-flex justify-content-between align-items-center bg-light-subtle border-0 rounded my-1">
                <span class="font-weight-medium text-gray-800">{{ index + 1 }}. {{ item.nombreCliente }}</span>
                <span class="h6 mb-0 text-purple-600">{{ item.totalPedidos }} pedidos</span>
              </li>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-6">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <h2 class="h5 card-title text-pink-700 mb-3">Top 10 Vendedores con Más Productos Listados</h2>
            <p v-if="cargando" class="text-secondary">Cargando...</p>
            <p v-else-if="reporteVendedoresProductos.length === 0" class="text-danger">No hay vendedores listando productos actualmente.</p>
            <div v-else class="list-group list-group-flush reporte-scrollable">
              <li v-for="(item, index) in reporteVendedoresProductos" :key="index" class="list-group-item d-flex justify-content-between align-items-center bg-light-subtle border-0 rounded my-1">
                <span class="font-weight-medium text-gray-800">{{ index + 1 }}. {{ item.nombreVendedor }}</span>
                <span class="h6 mb-0 text-pink-600">{{ item.totalProductosListados }} productos</span>
              </li>
            </div>
          </div>
        </div>
      </div>
    </div> <div class="card shadow-sm mb-5">
      <div class="card-body">
        <h2 class="h5 card-title text-red-700 mb-3">Historial de Sanciones (Suspensiones)</h2>
        <p v-if="cargandoSanciones" class="text-secondary">Cargando...</p>
        <p v-else-if="historialSanciones.length === 0" class="text-danger">No hay registros de sanciones.</p>
        <div v-else class="table-responsive" style="max-height: 400px; overflow-y: auto;">
          <table class="table table-striped table-hover align-middle mb-0">
            <thead class="table-light sticky-top">
              <tr>
                <th scope="col">Usuario Sancionado</th>
                <th scope="col">Moderador</th>
                <th scope="col">Motivo</th>
                <th scope="col">Inicio</th>
                <th scope="col">Fin</th>
                <th scope="col">Activa</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="sancion in historialSanciones" :key="sancion.id">
                <td>{{ sancion.nombreUsuarioSancionado }}</td>
                <td class="text-muted">{{ sancion.nombreModerador }}</td>
                <td class="text-muted">{{ sancion.motivoSuspension }}</td>
                <td class="text-muted">{{ new Date(sancion.fechaSuspension).toLocaleDateString() }}</td>
                <td class="text-muted">{{ new Date(sancion.fechaFin).toLocaleDateString() }}</td>
                <td>
                  <span :class="sancion.activa ? 'badge text-bg-danger' : 'badge text-bg-success'">
                    {{ sancion.activa ? 'SÍ' : 'NO' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="card shadow-sm mb-5">
      <div class="card-body">
        <h2 class="h5 card-title text-blue-700 mb-3">Historial de Notificaciones</h2>
        <p v-if="cargandoNotificaciones" class="text-secondary">Cargando...</p>
        <p v-else-if="historialNotificaciones.length === 0" class="text-danger">No hay registros de notificaciones.</p>
        <div v-else class="table-responsive" style="max-height: 400px; overflow-y: auto;">
          <table class="table table-striped table-hover align-middle mb-0">
            <thead class="table-light sticky-top">
              <tr>
                <th scope="col">Usuario Destino</th>
                <th scope="col">Título</th>
                <th scope="col">Cuerpo</th>
                <th scope="col">Fecha</th>
                <th scope="col">Leída</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="notif in historialNotificaciones" :key="notif.id">
                <td>{{ notif.nombreUsuarioDestino }}</td>
                <td class="text-muted text-nowrap">{{ notif.titulo }}</td>
                <td class="text-muted text-truncate" style="max-width: 250px;">{{ notif.cuerpo }}</td>
                <td class="text-muted text-nowrap">{{ new Date(notif.fecha).toLocaleDateString() }} {{ new Date(notif.fecha).toLocaleTimeString() }}</td>
                <td>
                  <span :class="notif.leida ? 'badge text-bg-success' : 'badge text-bg-danger'">
                    {{ notif.leida ? 'SÍ' : 'NO' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue';
import reportesApi from '@/api/reportes';

const filtro = reactive({
  fechaInicio: null, // "2024-01-01T00:00"
  fechaFin: null,
});

const reporteProductos = ref([]);// Top 10 productos más vendidos en un intervalo de tiempo
const reporteVendedores = ref([]);    // Top 5 clientes que más han vendido en un intervalo de tiempo (Asumo que te refieres a este como "vendedores")
const reporteCompradores = ref([]);  // Top 5 clientes que más ganancias generan en un intervalo de tiempo (Asumo que te refieres a este como "compradores")
const reportePedidos = ref([]); //Top 10 clientes con más pedidos en un intervalo de tiempo
const reporteVendedoresProductos = ref([]); //Top 10 clientes que más productos tienen a la venta


const historialSanciones = ref([]); // Reporte historial de sanciones
const historialNotificaciones = ref([]); // Reporte historial de notificaciones

const cargandoSanciones = ref(false); 
const cargandoNotificaciones = ref(false); // Estado de carga para historial de notificaciones
const cargando = ref(false); // Estado de carga para reportes con filtro de fecha

// Función separada para cargar el Historial de Sanciones (no depende del filtro de fecha)
const cargarHistorialSanciones = async () => {
    cargandoSanciones.value = true;
    historialSanciones.value = []; // Limpiar antes de cargar
    try {
        const res = await reportesApi.obtenerHistorialSuspensiones();
        historialSanciones.value = res.data;
    } catch (error) {
        console.error("Error al obtener el historial de sanciones:", error);
    } finally {
        cargandoSanciones.value = false;
    }
};

// Función separada para cargar el Historial de Notificaciones (no depende del filtro de fecha)
const cargarHistorialNotificaciones = async () => {
    cargandoNotificaciones.value = true;
    historialNotificaciones.value = []; // Limpiar antes de cargar
    try {
        const res = await reportesApi.obtenerHistorialNotificaciones();
        historialNotificaciones.value = res.data;
    } catch (error) {
        console.error("Error al obtener el historial de notificaciones:", error);
    } finally {
        cargandoNotificaciones.value = false;
    }
};


const cargarReportes = async () => {
  if (!filtro.fechaInicio || !filtro.fechaFin) {
    alert("Por favor, selecciona las fechas de inicio y fin.");
    return;
  }

  cargando.value = true;
  // Limpiar todos los reportes antes de cargar
  reporteProductos.value = []; 
  reporteVendedores.value = [];
  reporteCompradores.value = [];
  reportePedidos.value = [];
  reporteVendedoresProductos.value = [];
  

  try {
    // Top 10 productos más vendidos en un intervalo de tiempo
    const resProductos = await reportesApi.obtenerTop10ProductosVendidos(
      filtro.fechaInicio, 
      filtro.fechaFin
    );
    reporteProductos.value = resProductos.data;
    
    // Top 5 clientes que más ganancias generan en un intervalo de tiempo (Compradores por monto)
    const resCompradores = await reportesApi.obtenerTop5ClientesCompradores(
        filtro.fechaInicio,
        filtro.fechaFin
    );
    // **NOTA:** Corregí un posible error tipográfico aquí, asumiendo que `resComCompradores` debería ser `resCompradores`.
    // Si la API no retorna datos, es probable que se haya usado la variable incorrecta.
    reporteCompradores.value = resCompradores.data;


    // Top 5 clientes que más han vendido en un intervalo de tiempo (Vendedores por unidades)
    const resVendedores = await reportesApi.obtenerTop5Vendedores(
        filtro.fechaInicio,
        filtro.fechaFin
    );
    reporteVendedores.value = resVendedores.data;


    //Top 10 clientes con más pedidos en un intervalo de tiempo
    const resPedidos = await reportesApi.obtenerTop10ClientesMasPedidos(
            filtro.fechaInicio,
            filtro.fechaFin
        );
        reportePedidos.value = resPedidos.data;

    //Top 10 clientes que más productos tienen a la venta
    // Este reporte no depende del filtro de fecha, así que se llama sin ellas.
    const resVendedoresProductos = await reportesApi.obtenerTop10VendedoresConMasProductos();
        reporteVendedoresProductos.value = resVendedoresProductos.data;


    // Historial de sanciones: Llamamos a la función separada para refrescar este reporte
    await cargarHistorialSanciones(); 
    
    // Historial de notificaciones: Llamamos a la función separada para refrescar este reporte
    await cargarHistorialNotificaciones(); // <-- Implementación de la llamada

  } catch (error) {
    console.error("Error al obtener los reportes:", error);
    //alert("Hubo un error al cargar los reportes. Revisa la consola para más detalles.");
  } finally {
    cargando.value = false;
  }
};

// Cargar el historial de sanciones y otros reportes que no dependen del filtro de fechas al montar el componente
onMounted(() => {
    // Historial de sanciones se carga inmediatamente al montar la vista
    cargarHistorialSanciones(); 
    
    // Historial de notificaciones se carga inmediatamente al montar la vista
    cargarHistorialNotificaciones(); 
    
    // cargarReportes(); 
});
</script>

<style scoped>
/* Estilos adicionales para que los colores de texto personalizados (no estándar de Bootstrap) se vean bien */
/* Nota: Si tienes un archivo CSS global donde están definidos estos colores, puedes omitir este <style> */
/* Si no tienes un archivo global, necesitarás definir los colores o usar los colores estándar de Bootstrap */
.text-indigo-700 { color: #4338ca !important; } /* Equivalente aproximado a un tono de indigo */
.text-orange-700 { color: #c0631c !important; } /* Equivalente aproximado a un tono de naranja oscuro */
.text-blue-700 { color: #1d4ed8 !important; } /* Equivalente aproximado a un tono de azul oscuro */
.text-purple-700 { color: #7e22ce !important; } /* Equivalente aproximado a un tono de morado oscuro */
.text-pink-700 { color: #be185d !important; } /* Equivalente aproximado a un tono de rosa oscuro */
.text-red-700 { color: #b91c1c !important; } /* Equivalente aproximado a un tono de rojo oscuro */

/* Clase para hacer los listados de reportes scrollable y que tengan un tamaño consistente */
.reporte-scrollable {
  max-height: 250px; /* Altura máxima para hacer scroll */
  overflow-y: auto; /* Habilitar scroll vertical */
}

/* Fix para el texto truncado de la tabla de notificaciones */
.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block; /* Asegura que el truncado funcione correctamente */
}

/* Hacer que la cabecera de la tabla sea 'pegajosa' al hacer scroll */
.sticky-top {
    position: sticky;
    top: 0;
    z-index: 10;
}
</style>
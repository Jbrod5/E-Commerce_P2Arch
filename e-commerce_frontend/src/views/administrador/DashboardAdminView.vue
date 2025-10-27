<template>
  <div class="p-8">
    <h1 class="text-3xl font-bold mb-6">Panel de Administración - Reportes</h1>

    <div class="bg-white p-4 shadow rounded-lg mb-8">
      <h2 class="text-xl font-semibold mb-4">Filtros de Reporte</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label for="fechaInicio" class="block text-sm font-medium text-gray-700">Fecha de Inicio</label>
          <input type="datetime-local" id="fechaInicio" v-model="filtro.fechaInicio" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm" />
        </div>
        <div>
          <label for="fechaFin" class="block text-sm font-medium text-gray-700">Fecha de Fin</label>
          <input type="datetime-local" id="fechaFin" v-model="filtro.fechaFin" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm" />
        </div>
        <div class="flex items-end">
          <button @click="cargarReportes" :disabled="!filtro.fechaInicio || !filtro.fechaFin" class="w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50">
            Generar Reportes
          </button>
        </div>
      </div>
    </div>

    <div class="bg-white p-6 shadow rounded-lg mb-8">
      <h2 class="text-2xl font-semibold mb-4 text-indigo-700">Top 10 Productos Más Vendidos</h2>
      <p v-if="cargando" class="text-gray-500">Cargando...</p>
      <p v-else-if="reporteProductos.length === 0" class="text-red-500">No hay datos de ventas en el intervalo seleccionado.</p>
      <ul v-else class="space-y-3">
        <li v-for="(item, index) in reporteProductos" :key="index" class="p-3 bg-gray-50 border border-gray-200 rounded-md flex justify-between items-center">
          <span class="font-medium text-gray-800">{{ index + 1 }}. {{ item.nombreProducto }}</span>
          <span class="text-lg font-bold text-green-600">{{ item.totalVendido }} unidades</span>
        </li>
      </ul>
    </div>
    
    <div class="bg-white p-6 shadow rounded-lg mb-8">
      <h2 class="text-2xl font-semibold mb-4 text-orange-700">Top 5 Vendedores por Unidades Vendidas</h2>
      <p v-if="cargando" class="text-gray-500">Cargando...</p>
      <p v-else-if="reporteVendedores.length === 0" class="text-red-500">No hay datos de ventas de vendedores en el intervalo seleccionado.</p>
      <ul v-else class="space-y-3">
        <li v-for="(item, index) in reporteVendedores" :key="index" class="p-3 bg-gray-50 border border-gray-200 rounded-md flex justify-between items-center">
          <span class="font-medium text-gray-800">{{ index + 1 }}. {{ item.nombreVendedor }}</span>
          <span class="text-lg font-bold text-orange-600">{{ item.totalUnidadesVendidas }} unidades</span>
        </li>
      </ul>
    </div>

    <div class="bg-white p-6 shadow rounded-lg mb-8">
      <h2 class="text-2xl font-semibold mb-4 text-blue-700">Top 5 Clientes (Compradores) por Ganancia Generada</h2>
      <p v-if="cargando" class="text-gray-500">Cargando...</p>
      <p v-else-if="reporteCompradores.length === 0" class="text-red-500">No hay datos de compras en el intervalo seleccionado.</p>
      <ul v-else class="space-y-3">
        <li v-for="(item, index) in reporteCompradores" :key="index" class="p-3 bg-gray-50 border border-gray-200 rounded-md flex justify-between items-center">
          <span class="font-medium text-gray-800">{{ index + 1 }}. {{ item.nombreCliente }}</span>
          <div class="text-right">
            <p class="text-lg font-bold text-blue-600">Total Comprado: ${{ parseFloat(item.montoTotalCompras).toFixed(2) }}</p>
            <p class="text-sm text-gray-500">Ganancia Plataforma (5%): ${{ parseFloat(item.gananciaPlataforma).toFixed(2) }}</p>
          </div>
        </li>
      </ul>
    </div>




    <div class="p-8">
    <div class="bg-white p-6 shadow rounded-lg mt-8 mb-8">
      <h2 class="text-2xl font-semibold mb-4 text-purple-700">Top 10 Clientes con Más Pedidos</h2>
      <p v-if="cargando" class="text-gray-500">Cargando...</p>
      <p v-else-if="reportePedidos.length === 0" class="text-red-500">No hay datos de pedidos en el intervalo seleccionado.</p>
      <ul v-else class="space-y-3">
        <li v-for="(item, index) in reportePedidos" :key="index" class="p-3 bg-gray-50 border border-gray-200 rounded-md flex justify-between items-center">
          <span class="font-medium text-gray-800">{{ index + 1 }}. {{ item.nombreCliente }}</span>
          <span class="text-lg font-bold text-purple-600">{{ item.totalPedidos }} pedidos</span>
        </li>
      </ul>
    </div>
  </div>

  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import reportesApi from '@/api/reportes'; // Nota: Cambié a 'reportesApi' por convención

const filtro = reactive({
  fechaInicio: null, // "2024-01-01T00:00"
  fechaFin: null,
});

const reporteProductos = ref([]);// Top 10 productos más vendidos en un intervalo de tiempo
const reporteVendedores = ref([]);   // Top 5 clientes que más han vendido en un intervalo de tiempo
const reporteCompradores = ref([]);  // Top 5 clientes que más ganancias generan en un intervalo de tiempo
const reportePedidos = ref([]); //Top 10 clientes con más pedidos en un intervalo de tiempo
const cargando = ref(false);

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

  try {
   // Top 10 productos más vendidos en un intervalo de tiempo
    const resProductos = await reportesApi.obtenerTop10ProductosVendidos(
      filtro.fechaInicio, 
      filtro.fechaFin
    );
    reporteProductos.value = resProductos.data;
    
    // Top 5 clientes que más han vendido en un intervalo de tiempo
    const resCompradores = await reportesApi.obtenerTop5ClientesCompradores(
        filtro.fechaInicio,
        filtro.fechaFin
    );
    reporteCompradores.value = resCompradores.data;


    // Top 5 clientes que más ganancias generan en un intervalo de tiempo
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




  } catch (error) {
    console.error("Error al obtener los reportes:", error);
    //alert("Hubo un error al cargar los reportes. Revisa la consola para más detalles.");
  } finally {
    cargando.value = false;
  }
};
</script>
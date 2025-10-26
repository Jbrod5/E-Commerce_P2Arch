<template>
  <div class="container-fluid">
    <h1 class="mb-4 text-primary border-bottom pb-2">Mi Inventario de Productos</h1>
    
    <!-- Botón para crear un nuevo producto -->
    <router-link :to="{ name: 'crearProducto' }" class="btn btn-primary mb-4 shadow-sm">
        <i class="bi bi-plus-circle me-2"></i> Nuevo Producto
    </router-link>

    <!-- Mensaje de Estado -->
    <div v-if="isLoading" class="alert alert-info text-center">
        <span class="spinner-border spinner-border-sm me-2"></span> Cargando inventario...
    </div>
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    
    <!-- Tabla de Productos -->
    <div v-if="productos.length > 0 && !isLoading" class="table-responsive">
      <table class="table table-hover table-striped shadow-sm">
        <thead class="table-dark">
          <tr>
            <th scope="col">#</th>
            <th scope="col">Imagen</th>
            <th scope="col">Nombre</th>
            <th scope="col">Precio</th>
            <th scope="col">Stock</th>
            <th scope="col">Estado</th>
            <th scope="col">Nuevo</th>
            <th scope="col">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(producto, index) in productos" :key="producto.id">
            <th scope="row">{{ index + 1 }}</th>
            <td>
              <!-- Muestra la imagen, usando la URL que generamos en Spring Boot (e.g., http://localhost:8080/imagenes/productos/...) -->
              <img 
                v-ngrok-img="producto.imagenUrl" 
                alt="Miniatura" 
                class="img-thumbnail" 
                style="width: 50px; height: 50px; object-fit: cover;"
                onerror="this.onerror=null; this.src='https://placehold.co/50x50/cccccc/000000?text=NO+IMG';"
              >
            </td>
            <td>{{ producto.nombre }}</td>
            <td>Q{{ producto.precio.toFixed(2) }}</td>
            <td>
                <span :class="{'badge bg-success': producto.stock > 5, 'badge bg-warning text-dark': producto.stock <= 5}">
                    {{ producto.stock }}
                </span>
            </td>
            <td>
                <!-- CORRECCIÓN 1: Acceder a producto.estado.nombre para obtener el String -->
                <span :class="getStatusBadge(producto.estado.nombre)">
                    {{ producto.estado.nombre }} 
                </span>
            </td>
            <td>
                <i :class="['bi', producto.esNuevo ? 'bi-check-circle-fill text-success' : 'bi-x-circle-fill text-danger']"></i>
            </td>
            <td>
              <!-- Botones de Acción (Editar / Eliminar - Funcionalidad a implementar) -->
              <button class="btn btn-sm btn-info me-2" title="Editar Producto">
                  <i class="bi bi-pencil"></i>
              </button>
              <button class="btn btn-sm btn-danger" title="Eliminar Producto">
                  <i class="bi bi-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Mensaje si no hay productos -->
    <div v-else-if="!isLoading" class="alert alert-warning text-center">
        <i class="bi bi-info-circle me-2"></i> Aún no tienes productos registrados.
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/plugins/axios';

const productos = ref([]);
const isLoading = ref(true);
const errorMessage = ref('');

/**
 * Mapea el estado del producto (String) a una clase de badge de Bootstrap.
 * CORRECCIÓN 2: Asegurarse de que 'estado' sea un string antes de llamar a toLowerCase().
 */
const getStatusBadge = (estado) => {
    // Protección: si estado no es un string, retorna una clase por defecto para evitar el TypeError
    if (typeof estado !== 'string') {
        return 'badge bg-secondary'; 
    }
    
    switch(estado.toLowerCase()) {
        case 'aprobado': // Asumo 'aprobado' es uno de tus estados posibles
        case 'activo': return 'badge bg-success';
        case 'pendiente': return 'badge bg-warning text-dark';
        case 'rechazado': return 'badge bg-danger';
        default: return 'badge bg-secondary';
    }
}

/**
 * Obtiene la lista de productos del vendedor autenticado.
 */
const fetchInventario = async () => {
    isLoading.value = true;
    errorMessage.value = '';
    
    try {
        // Llama al endpoint que creamos en ProductoController (GET /api/productos/inventario)
        const response = await axios.get('/productos/inventario'); 
        productos.value = response.data;
    } catch (error) {
        console.error('Error al cargar el inventario:', error);
        errorMessage.value = 'No se pudo cargar el inventario. Asegúrese de estar autenticado y que el servidor esté activo.';
    } finally {
        isLoading.value = false;
    }
};

// Llama a la función al montar el componente
onMounted(fetchInventario);
</script>

<style scoped>
/* Asegura que los íconos de Bootstrap estén disponibles */
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css");

.img-thumbnail {
    border: none;
    padding: 0;
}
</style>

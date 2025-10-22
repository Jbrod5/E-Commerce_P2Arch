<template>
  <div class="container-fluid">
    <h1 class="mb-4 text-secondary border-bottom pb-2">Explorar Productos Disponibles</h1>
    
    <div v-if="isLoading" class="alert alert-info text-center">
        <span class="spinner-border spinner-border-sm me-2"></span> Cargando Marketplace...
    </div>
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    
    <div v-if="mensajeCarrito" :class="`alert alert-${tipoMensaje} alert-dismissible fade show`" role="alert">
        {{ mensajeCarrito }}
        <button type="button" class="btn-close" @click="mensajeCarrito = null"></button>
    </div>
    
    <div v-if="productos.length > 0 && !isLoading" class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
      
      <div class="col" v-for="producto in productos" :key="producto.id">
        <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden product-card">
          
          <img 
            :src="producto.imagenUrl || 'https://placehold.co/400x200/4c4c4c/ffffff?text=Sin+Imagen'" 
            class="card-img-top object-fit-cover" 
            alt="Imagen del producto" 
            style="height: 200px;"
            onerror="this.onerror=null; this.src='https://placehold.co/400x200/4c4c4c/ffffff?text=Producto';"
          />
          
          <div class="card-body d-flex flex-column">
            <h5 class="card-title text-truncate">{{ producto.nombre }} 
                <span v-if="producto.esNuevo" class="badge bg-info text-white ms-2">Nuevo</span>
            </h5>
            
            <p class="card-text text-muted small flex-grow-1 mb-3 description-text">{{ producto.descripcion.substring(0, 70) + '...' }}</p>
            
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="text-primary fw-bold mb-0">Q{{ producto.precio.toFixed(2) }}</h4>
                <small class="text-success">{{ producto.stock > 0 ? 'En Stock' : 'Agotado' }}</small>
            </div>

            <button 
                class="btn btn-primary w-100 rounded-pill shadow-sm" 
                :disabled="producto.stock <= 0 || carritoStore.cargando" 
                @click="manejarAgregarProducto(producto.id)"
            >
                <span v-if="carritoStore.cargando" class="spinner-border spinner-border-sm me-2" role="status"></span>
                <i v-else class="bi bi-cart-plus me-2"></i> 
                {{ producto.stock <= 0 ? 'Agotado' : 'Añadir al Carrito' }}
            </button>
          </div>
        </div>
      </div>
      
    </div>

    <div v-else-if="!isLoading" class="alert alert-warning text-center mt-5">
        <i class="bi bi-info-circle me-2"></i> No se encontraron productos en el Marketplace.
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/plugins/axios';
import { useCarritoStore } from '@/stores/carrito'; // Importamos el store del carrito

// --- Estado local y Stores ---
const carritoStore = useCarritoStore(); // Inicializamos el store

const productos = ref([]);
const isLoading = ref(true);
const errorMessage = ref('');
const mensajeCarrito = ref(null); // Para mostrar feedback al usuario
const tipoMensaje = ref('');


/**
 * Función para añadir al carrito.
 */
const manejarAgregarProducto = async (productoId) => {
    // Solo se agrega 1 unidad por defecto
    const cantidad = 1;
    mensajeCarrito.value = null; // Limpiar mensaje anterior

    try {
        // La acción ya maneja el estado 'cargando'
        await carritoStore.agregarOActualizarProducto(productoId, cantidad);
        
        // Mostrar mensaje de éxito
        mensajeCarrito.value = '¡Producto agregado al carrito!';
        tipoMensaje.value = 'success';
        
    } catch (error) {
        // Usamos el error lanzado por el store
        const mensajeError = typeof error === 'string' ? error : 'Error desconocido al añadir al carrito.';
        mensajeCarrito.value = `Error: ${mensajeError}`;
        tipoMensaje.value = 'danger';
    } finally {
        // Limpiar mensaje después de 4 segundos
        setTimeout(() => { mensajeCarrito.value = null; }, 4000);
    }
};

/**
 * Obtiene la lista de productos APROBADOS (Marketplace).
 */
const fetchMarketplaceProducts = async () => {
    isLoading.value = true;
    errorMessage.value = '';
    
    try {
        // La ruta GET /api/productos llama al método obtenerProductosMarketplace()
        const response = await axios.get('/productos'); 
        productos.value = response.data;
    } catch (error) {
        console.error('Error al cargar el Marketplace:', error);
        errorMessage.value = 'No se pudo cargar el Marketplace. El servidor puede estar inactivo o el endpoint /api/productos no está disponible.';
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    fetchMarketplaceProducts();
    // 🚨 Precarga el carrito para que el contador de la navbar esté listo
    carritoStore.cargarCarrito(); 
});
</script>

<style scoped>
/* Asegura que los íconos de Bootstrap estén disponibles globalmente */
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css");

.product-card {
    /* Estilo para que las tarjetas reaccionen al pasar el ratón */
    transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.25) !important;
}

.description-text {
    /* Asegura que todas las descripciones (aunque truncadas) tengan una altura mínima para evitar CLS */
    min-height: 2.5rem; 
    line-height: 1.25;
}
</style>

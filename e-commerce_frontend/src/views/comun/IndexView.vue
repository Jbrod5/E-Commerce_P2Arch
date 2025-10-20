<template>
  <div class="container-fluid">
    <h1 class="mb-4 text-secondary border-bottom pb-2">Explorar Productos Disponibles</h1>
    
    <!-- Mensaje de Estado -->
    <div v-if="isLoading" class="alert alert-info text-center">
        <span class="spinner-border spinner-border-sm me-2"></span> Cargando Marketplace...
    </div>
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    
    <!-- Cuadrícula de Productos (Marketplace Grid) -->
    <div v-if="productos.length > 0 && !isLoading" class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
      
      <div class="col" v-for="producto in productos" :key="producto.id">
        <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden product-card">
          
          <!-- Imagen del Producto -->
          <!-- Usamos una imagen de placeholder si la URL falla o está vacía -->
          <img 
            :src="producto.imagenUrl || 'https://placehold.co/400x200/4c4c4c/ffffff?text=Sin+Imagen'" 
            class="card-img-top object-fit-cover" 
            alt="Imagen del producto" 
            style="height: 200px;"
            onerror="this.onerror=null; this.src='https://placehold.co/400x200/4c4c4c/ffffff?text=Producto';"
          />
          
          <div class="card-body d-flex flex-column">
            <!-- Nombre y Badge de Novedad -->
            <h5 class="card-title text-truncate">{{ producto.nombre }} 
                <span v-if="producto.esNuevo" class="badge bg-info text-white ms-2">Nuevo</span>
            </h5>
            
            <!-- Descripción (Truncada) -->
            <p class="card-text text-muted small flex-grow-1 mb-3 description-text">{{ producto.descripcion.substring(0, 70) + '...' }}</p>
            
            <!-- Precio -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="text-primary fw-bold mb-0">Q{{ producto.precio.toFixed(2) }}</h4>
                <small class="text-success">{{ producto.stock > 0 ? 'En Stock' : 'Agotado' }}</small>
            </div>

            <!-- Botón de Acción -->
            <button 
                class="btn btn-primary w-100 rounded-pill shadow-sm" 
                :disabled="producto.stock <= 0"
                @click="addToCart(producto.id)"
            >
                <i class="bi bi-cart-plus me-2"></i> Añadir al Carrito
            </button>
          </div>
        </div>
      </div>
      
    </div>

    <!-- Mensaje si no hay productos -->
    <div v-else-if="!isLoading" class="alert alert-warning text-center mt-5">
        <i class="bi bi-info-circle me-2"></i> No se encontraron productos en el Marketplace.
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/plugins/axios';
// Importamos la store de autenticación para obtener el token, si fuera necesario para la API
// import { useAuthStore } from '@/stores/auth'; 

const productos = ref([]);
const isLoading = ref(true);
const errorMessage = ref('');

/**
 * Simula la acción de añadir al carrito.
 */
const addToCart = (productoId) => {
    // Usamos console.log para no usar alert()
    console.log(`Producto ID ${productoId} añadido al carrito (simulación).`);
    // Aquí iría la lógica real de Pinia para el carrito
};

/**
 * Obtiene la lista de TODOS los productos (Marketplace).
 */
const fetchMarketplaceProducts = async () => {
    isLoading.value = true;
    errorMessage.value = '';
    
    try {
        // Asumimos un endpoint para OBTENER TODOS los productos.
        // Si el endpoint /productos es público, la llamada funciona sin problemas.
        // Si requiere autenticación, el token será añadido automáticamente por el interceptor de Axios.
        const response = await axios.get('/productos'); 
        productos.value = response.data;
    } catch (error) {
        console.error('Error al cargar el Marketplace:', error);
        errorMessage.value = 'No se pudo cargar el Marketplace. El servidor puede estar inactivo o el endpoint /api/productos no está disponible.';
    } finally {
        isLoading.value = false;
    }
};

onMounted(fetchMarketplaceProducts);
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
